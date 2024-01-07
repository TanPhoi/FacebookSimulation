package com.phoint.facebooksimulation.ui.editInformation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.BottomDialogProfileAvatarBinding
import com.phoint.facebooksimulation.databinding.FragmentEditInformationBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class EditInformationFragment :
    BaseFragment<FragmentEditInformationBinding, EditInformationViewModel>() {
    private var dialog: BottomSheetDialog? = null
    private var bindingImage: BottomDialogProfileAvatarBinding? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã cap quyền truy cập vào thư viện ảnh",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã từ chối quyền truy cập vào thư viện ảnh",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val startForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data
                handleImageResult(imageUrl)
            }
        }

    private fun handleImageResult(imageUrl: Uri?) {
        try {
            val id = viewModel.getIdPreferences()
            viewModel.informationUser.observe(this) {
                if (id == it.idUser) {
                    it.coverImageUser = imageUrl.toString()
                    viewModel.updateUser(it)
                    Glide.with(requireContext()).load(it.coverImageUser)
                        .into(binding.imgCoverUser)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun layoutRes(): Int {
        return R.layout.fragment_edit_information
    }

    override fun viewModelClass(): Class<EditInformationViewModel> {
        return EditInformationViewModel::class.java
    }

    override fun initView() {
        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.getInformationUser(viewModel.getIdPreferences())
        setData()

        viewModel.informationUser.observe(this) {

            if (it.avatarUser != null) {
                Glide.with(requireContext()).load(it.avatarUser).into(binding.imgAvatarUser)
                binding.btnSrcUser.text = "Chỉnh sửa"
            } else {
                binding.btnSrcUser.text = "Thêm"
            }

            if (it.coverImageUser != null) {
                Glide.with(requireContext()).load(it.coverImageUser).into(binding.imgCoverUser)
                binding.btnCoverSrc.text = "Chỉnh sửa"
            } else {
                binding.btnCoverSrc.text = "Thêm"
            }

            if (it.story != null) {
                binding.tvStory.text = it.story?.nameStory
                binding.btnStory.text = "Chỉnh sửa"
            } else {
                binding.btnStory.text = "Thêm"
            }

            if (it.workExperience != null) {
                binding.tvWork.text = it.workExperience?.nameWork
            } else {
                binding.tvWork.text = "Học vấn"
            }

            if (it.colleges != null) {
                binding.tvColleges.text = it.colleges?.nameColleges
            } else {
                binding.tvColleges.text = "Học vấn"
            }

            if (it.highSchool != null) {
                binding.tvHighSchool.text = it.highSchool?.nameHighSchool
                binding.tvHighSchool.visibility = View.VISIBLE
            } else {
                binding.tvHighSchool.visibility = View.GONE
            }

            if (it.currentCityAndProvince != null) {
                binding.tvCurrentCity.text = it.currentCityAndProvince?.nameCurrentProvinceOrCity
            } else {
                binding.tvCurrentCity.text = "Tỉnh/Thành phố hiện tại"
            }

            if (it.homeTown != null) {
                binding.tvHomeTown.text = it.homeTown?.nameHomeTown
                binding.tvHomeTown.visibility = View.VISIBLE
            } else {
                binding.tvHomeTown.visibility = View.GONE
            }


            if (it.relationship != null) {
                binding.tvRelationship.text = it.relationship?.nameRelationship
            } else {
                binding.tvRelationship.text = "Tình trạng mối quan hệ"
            }
        }

        dialog = BottomSheetDialog(requireContext())

        binding.btnEditInformationUser.setOnSingClickListener {
            findNavController().navigate(R.id.action_editInformationFragment_to_sendInformationFragment)
        }

        binding.btnSrcUser.setOnClickListener {
            createDialogImageUser()
            dialog?.show()
        }

        binding.btnAddStory.setOnSingClickListener {
            findNavController().navigate(R.id.action_editInformationFragment_to_addStoryFragment)
        }

        binding.btnCoverSrc.setOnSingClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startForActivityResult.launch(intent)
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        binding.btnPermission.setOnSingClickListener {
            findNavController().navigate(R.id.action_editInformationFragment_to_editPermissionUserFragment)
        }

        binding.abEditInformation.setOnSingClickLeft {
            findNavController().navigateUp()
        }
    }

    private fun setData() {
        viewModel.informationUser.observe(this) {

            if (it.coverImageUser != null) {
                Glide.with(requireContext()).load(it.coverImageUser).into(binding.imgCoverUser)
                binding.btnCoverSrc.text = "Chỉnh sửa"
            } else {
                binding.btnCoverSrc.text = "Thêm"
            }

            if (it.story != null) {
                binding.tvStory.text = it.story?.nameStory
                binding.btnStory.text = "Chỉnh sửa"
            } else {
                binding.btnStory.text = "Thêm"
            }

            if (it.workExperience != null) {
                binding.tvWork.text = it.workExperience?.nameWork
            } else {
                binding.tvWork.text = "Học vấn"
            }

            if (it.colleges != null) {
                binding.tvColleges.text = it.colleges?.nameColleges
            } else {
                binding.tvColleges.text = "Học vấn"
            }

            if (it.highSchool != null) {
                binding.tvHighSchool.text = it.highSchool?.nameHighSchool
                binding.tvHighSchool.visibility = View.VISIBLE
            } else {
                binding.tvHighSchool.visibility = View.GONE
            }

            if (it.currentCityAndProvince != null) {
                binding.tvCurrentCity.text = it.currentCityAndProvince?.nameCurrentProvinceOrCity
            } else {
                binding.tvCurrentCity.text = "Tỉnh/Thành phố hiện tại"
            }

            if (it.homeTown != null) {
                binding.tvHomeTown.text = it.homeTown?.nameHomeTown
                binding.tvHomeTown.visibility = View.VISIBLE
            } else {
                binding.tvHomeTown.visibility = View.GONE
            }


            if (it.relationship != null) {
                binding.tvRelationship.text = it.relationship?.nameRelationship
            } else {
                binding.tvRelationship.text = "Tình trạng mối quan hệ"
            }
        }
    }

    private fun createDialogImageUser() {
        bindingImage = BottomDialogProfileAvatarBinding.inflate(layoutInflater)

        bindingImage?.btnProfileUser?.setOnSingClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                findNavController().navigate(R.id.action_editInformationFragment_to_showImageFragment)
                dialog?.dismiss()
            } else {
                requestPermissionLauncher.launch(permission)
            }

        }

        dialog?.setContentView(bindingImage?.root!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
