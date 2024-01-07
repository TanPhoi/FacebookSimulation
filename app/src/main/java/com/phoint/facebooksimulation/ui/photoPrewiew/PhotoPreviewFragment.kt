package com.phoint.facebooksimulation.ui.photoPrewiew

import android.os.Handler
import android.os.Looper
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentPhotoPreviewBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment

class PhotoPreviewFragment : BaseFragment<FragmentPhotoPreviewBinding, PhotoPreviewViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_photo_preview
    }

    override fun viewModelClass(): Class<PhotoPreviewViewModel> {
        return PhotoPreviewViewModel::class.java
    }

    override fun initView() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Không làm gì khi nút Back được nhấn
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as BaseActivity<*, *>).showLoading()
            } else {
                (activity as BaseActivity<*, *>).hiddenLoading()
            }
        }
        viewModel.getIdUser(viewModel.getIdPreferences())
        val imageUrl = arguments?.getString("imageUrl")
        Glide.with(requireContext()).load(imageUrl).into(binding.imgUser)

        binding.abPhotoPreview.setBackGroundButtonImageString(imageUrl)

        binding.abPhotoPreview.clickSaveImage {
            viewModel.informationUser.observe(this) {
                it.avatarUser = imageUrl
                viewModel.updateUser(it)
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                (activity as BaseActivity<*, *>).showLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_photoPreviewFragment_to_editInformationFragment)
                }, 4000)
            }
        }

        binding.abPhotoPreview.setOnSingClickLeft {
            findNavController().navigate(R.id.action_photoPreviewFragment_to_showImageFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

}
