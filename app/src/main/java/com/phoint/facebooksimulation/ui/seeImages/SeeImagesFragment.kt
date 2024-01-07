package com.phoint.facebooksimulation.ui.seeImages

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentSeeImagesBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class SeeImagesFragment : BaseFragment<FragmentSeeImagesBinding, SeeImagesViewModel>() {
    private var adapter: ImageAdapter? = null
    private var imageList = ArrayList<Post>()
    override fun layoutRes(): Int {
        return R.layout.fragment_see_images
    }

    override fun viewModelClass(): Class<SeeImagesViewModel> {
        return SeeImagesViewModel::class.java
    }

    override fun initView() {
        val user = arguments?.getParcelable("user") ?: User()

        adapter = ImageAdapter(imageList)
        binding.rcvImages.adapter = adapter

        viewModel.getJoinDataPost(user.idUser!!)
        viewModel.postUserById.observe(this) {
            imageList.clear()
            imageList.addAll(it)
            adapter?.notifyDataSetChanged()
        }

        adapter?.onItemClick = { post ->
            findNavController().navigate(
                R.id.action_seeImagesFragment_to_displayImageFragment, bundleOf(
                    Pair("post", post)
                )
            )
        }

        binding.tvName.text = "Ảnh của ${user.nameUser}"

        binding.abImage.setBackCurrentScreen {
            findNavController().popBackStack()
        }

        binding.abImage.setName("${user.nameUser}")

        binding.abImage.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_seeImagesFragment_to_searchHistoryFragment)
        }
    }
}
