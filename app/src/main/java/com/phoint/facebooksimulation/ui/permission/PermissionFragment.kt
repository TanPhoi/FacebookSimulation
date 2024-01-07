package com.phoint.facebooksimulation.ui.permission

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.databinding.FragmentPermissionBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.profile.ProfileFragment
import com.phoint.facebooksimulation.ui.seeComment.SeeCommentsFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PermissionFragment : BaseFragment<FragmentPermissionBinding, PermissionViewModel>() {
    companion object {
        const val PERMISSION = "PERMISSION"
        const val DRAWABLE_LEFT = "DRAWABLE_LEFT"
        const val EDIT_PERMISSION = "EDIT_PERMISSION"
        const val KEY_PROFILE_EDIT_POST = "KEY_PROFILE_EDIT_POST"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_permission
    }

    override fun viewModelClass(): Class<PermissionViewModel> {
        return PermissionViewModel::class.java
    }

    override fun initView() {
        val editPost = arguments?.getParcelable("post") as? Post
        val keyPost = arguments?.getString(SeeCommentsFragment.EDIT_PERMISSION)
        val keyProfilePost = arguments?.getString(ProfileFragment.KEY_PROFILE_EDIT_POST)

        if (keyProfilePost == KEY_PROFILE_EDIT_POST) {
            binding.btnSave.setOnSingClickListener {
                if (binding.rbPermissionPrivate.isChecked) {
                    val permissionPrivate = binding.rbPermissionPrivate.text.toString()
                    editPost?.privacyPost = permissionPrivate
                } else if (binding.rbPermissionPublic.isChecked) {
                    val permissionPublic = binding.rbPermissionPublic.text.toString()
                    editPost?.privacyPost = permissionPublic
                } else {
                    val permissionFriend = binding.rbPermissionFriend.text.toString()
                    editPost?.privacyPost = permissionFriend
                }
                findNavController().popBackStack()
                viewModel.updatePost(editPost!!)
            }
        } else if (keyPost == EDIT_PERMISSION) {
            binding.btnSave.setOnSingClickListener {
                if (binding.rbPermissionPrivate.isChecked) {
                    val permissionPrivate = binding.rbPermissionPrivate.text.toString()
                    editPost?.privacyPost = permissionPrivate
                } else if (binding.rbPermissionPublic.isChecked) {
                    val permissionPublic = binding.rbPermissionPublic.text.toString()
                    editPost?.privacyPost = permissionPublic
                } else {
                    val permissionFriend = binding.rbPermissionFriend.text.toString()
                    editPost?.privacyPost = permissionFriend
                }
                findNavController().popBackStack()
                viewModel.updatePost(editPost!!)
            }
        } else {
            binding.btnSave.setOnClickListener {
                if (binding.rbPermissionPrivate.isChecked) {
                    val drawableLeftResId = R.drawable.ic_permission_padlock
                    val textRadio = binding.rbPermissionPrivate.text.toString()

                    val bundle = bundleOf(
                        PERMISSION to textRadio, DRAWABLE_LEFT to drawableLeftResId
                    )
                    findNavController().navigate(
                        R.id.action_permissionFragment_to_postsAnArticleFragment,
                        bundle
                    )

                } else if (binding.rbPermissionPublic.isChecked) {
                    val drawableLeftResId = R.drawable.ic_permission_public
                    val textRadio = binding.rbPermissionPublic.text.toString()

                    val bundle = bundleOf(
                        PERMISSION to textRadio, DRAWABLE_LEFT to drawableLeftResId
                    )
                    findNavController().navigate(
                        R.id.action_permissionFragment_to_postsAnArticleFragment,
                        bundle
                    )
                } else {
                    val drawableLeftResId = R.drawable.ic_permission_friend
                    val textRadio = binding.rbPermissionFriend.text.toString()

                    val bundle = bundleOf(
                        PERMISSION to textRadio, DRAWABLE_LEFT to drawableLeftResId
                    )
                    findNavController().navigate(
                        R.id.action_permissionFragment_to_postsAnArticleFragment,
                        bundle
                    )
                }
            }
        }

        binding.abPermission.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
