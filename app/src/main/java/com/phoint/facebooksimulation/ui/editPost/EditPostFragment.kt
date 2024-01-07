package com.phoint.facebooksimulation.ui.editPost

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.databinding.BottomDialogPostBinding
import com.phoint.facebooksimulation.databinding.FragmentEditPostBinding
import com.phoint.facebooksimulation.databinding.LayoutDialogEditPostBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.profile.ProfileFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class EditPostFragment : BaseFragment<FragmentEditPostBinding, EditPostViewModel>() {
    companion object {
        const val PUBLIC = "Công khai"
        const val FRIENDS = "Bạn bè"
        const val PRIVATE = "Chỉ mình tôi"
        const val KEY_UPDATE = "UPDATE_POST"
        const val KEY_PROFILE_EDIT_POST = "KEY_PROFILE_EDIT_POST"
    }

    private var isImageModified: Boolean = false
    private var imageUri: Uri? = null
    private var post: Post? = null
    private var keyUpdate: String? = null
    private var keyEditPost: String? = null
    private var bottomDialog: BottomSheetDialog? = null
    private var bindingDialogCenter: LayoutDialogEditPostBinding? = null
    private var bindingBottomDialog: BottomDialogPostBinding? = null
    private val startActivityForImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result.data?.data
                handleResultImage(image)
            }
        }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "You have granted access", Toast.LENGTH_SHORT)
                    .show()
                Log.d("permission", "You have granted access")
            } else {
                Toast.makeText(requireContext(), "You have denied access", Toast.LENGTH_SHORT)
                    .show()
                Log.d("permission", "You have denied access")
            }
        }

    private fun handleResultImage(image: Uri?) {
        try {
            if (image != null) {
                this.imageUri = image
                binding.imgStatus.adjustViewBounds = true
                binding.abEditPost.setBackGroundButtonImage(image)
                Glide.with(requireContext()).load(image).into(binding.imgStatus)
                isImageModified = true
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_edit_post
    }

    override fun viewModelClass(): Class<EditPostViewModel> {
        return EditPostViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (keyEditPost == KEY_PROFILE_EDIT_POST) {
                showExitDialogProfile()
            } else {
                showExitDialog()
            }
        }
    }

    override fun initView() {
        post = arguments?.getParcelable("post") as? Post
        keyUpdate = arguments?.getString(KEY_UPDATE)
        keyEditPost = arguments?.getString(ProfileFragment.KEY_PROFILE_EDIT_POST)

        binding.editContent.addTextChangedListener {
            binding.abEditPost.setBackGroundButtonContent(it.toString())
        }

        if (post != null && keyEditPost == KEY_PROFILE_EDIT_POST) {
            binding.editContent.setText(post?.contentPost)
            binding.tvPermission.text = post?.privacyPost.toString()
            if (post?.srcPost != null) {
                binding.imgStatus.adjustViewBounds = true
                Glide.with(requireContext()).load(post?.srcPost).into(binding.imgStatus)
            }

            setInformationUserPost()
            handlePermission()
            updatePostProfile()
            handleActionBarProfile()

            binding.cvBottomDialog.setOnSingClickListener {
                handleBottomDialog()
            }
        } else {
            binding.editContent.setText(post?.contentPost)
            binding.tvPermission.text = post?.privacyPost.toString()
            if (post?.srcPost != null) {
                binding.imgStatus.adjustViewBounds = true
                Glide.with(requireContext()).load(post?.srcPost).into(binding.imgStatus)
            }
            setInformationUserPost()
            handleActionBar()
            handlePermission()
            updatePost()

            binding.cvBottomDialog.setOnSingClickListener {
                handleBottomDialog()
            }
        }

    }

    private fun updatePost() {
        val imageFromDatabase = post?.srcPost
        binding.abEditPost.onSingClickListenerRight {
            val imageSrc = if (isImageModified) imageUri?.toString() else imageFromDatabase
            post?.contentPost = binding.editContent.text.toString().trim()
            post?.srcPost = imageSrc
            post?.privacyPost = binding.tvPermission.text.toString()
            viewModel.updatePost(post!!)
        }

        viewModel.doneUpdatePost.observe(this) { updated ->
            if (updated == true) {
                findNavController().popBackStack()
            }
        }
    }

    private fun updatePostProfile() {
        val imageFromDatabase = post?.srcPost
        binding.abEditPost.onSingClickListenerRight {
            val imageSrc = if (isImageModified) imageUri?.toString() else imageFromDatabase
            post?.contentPost = binding.editContent.text.toString().trim()
            post?.srcPost = imageSrc
            post?.privacyPost = binding.tvPermission.text.toString()
            viewModel.updatePost(post!!)
        }

        viewModel.doneUpdatePost.observe(this) { updated ->
            if (updated == true) {
                findNavController().popBackStack()
            }
        }
    }

    private fun handleBottomDialog() {
        bottomDialog = BottomSheetDialog(requireContext())
        bindingBottomDialog = BottomDialogPostBinding.inflate(layoutInflater)
        bottomDialog?.create()
        bottomDialog?.show()
        bindingBottomDialog?.btnImage?.setOnSingClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForImage.launch(intent)
                bottomDialog?.dismiss()
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
        bottomDialog?.setContentView(bindingBottomDialog?.root!!)

    }

    private fun handlePermission() {
        val drawableRight =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)

        when (post?.privacyPost) {
            PUBLIC -> {
                var drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_public)
                binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
            }

            FRIENDS -> {
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_friend)
                binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
            }

            PRIVATE -> {
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_padlock)
                binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
            }

            else -> {
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_friend)
                binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
            }
        }
    }

    private fun handleActionBar() {
        val builder = AlertDialog.Builder(requireContext())
        bindingDialogCenter = LayoutDialogEditPostBinding.inflate(layoutInflater)
        builder.setView(bindingDialogCenter?.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        var isEditTextChanged = false

        binding.editContent.addTextChangedListener {
            val hasTextChanged = it?.toString()?.isNotEmpty() ?: false
            isEditTextChanged = hasTextChanged
        }

        binding.abEditPost.setOnSingClickLeft {
            if (isEditTextChanged) {
                alertDialog.show()

                bindingDialogCenter?.btnDropChange?.setOnSingClickListener {
                    findNavController().popBackStack()
                    alertDialog.dismiss()
                }

                alertDialog.setOnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isEditTextChanged) {
                            alertDialog.show()
                        }
                        return@setOnKeyListener true
                    }
                    false
                }

                bindingDialogCenter?.btnChange?.setOnSingClickListener {
                    alertDialog.dismiss()
                }
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun handleActionBarProfile() {
        val builder = AlertDialog.Builder(requireContext())
        bindingDialogCenter = LayoutDialogEditPostBinding.inflate(layoutInflater)
        builder.setView(bindingDialogCenter?.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        var isEditTextChanged = false

        binding.editContent.addTextChangedListener {
            val hasTextChanged = it?.toString()?.isNotEmpty() ?: false
            isEditTextChanged = hasTextChanged
        }

        binding.abEditPost.setOnSingClickLeft {
            if (isEditTextChanged) {
                alertDialog.show()

                bindingDialogCenter?.btnDropChange?.setOnSingClickListener {
                    findNavController().popBackStack()
                    alertDialog.dismiss()
                }

                alertDialog.setOnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isEditTextChanged) {
                            alertDialog.show()
                        }
                        return@setOnKeyListener true
                    }
                    false
                }

                bindingDialogCenter?.btnChange?.setOnSingClickListener {
                    alertDialog.dismiss()
                }
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(requireContext())
        bindingDialogCenter = LayoutDialogEditPostBinding.inflate(layoutInflater)
        builder.setView(bindingDialogCenter?.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        var isEditTextChanged = false
        binding.editContent.addTextChangedListener {
            val hasTextChanged = it?.toString()?.isNotEmpty() ?: false
            isEditTextChanged = hasTextChanged
        }
        bindingDialogCenter?.btnDropChange?.setOnSingClickListener {
            findNavController().popBackStack()
            alertDialog.dismiss()
        }

        bindingDialogCenter?.btnChange?.setOnSingClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showExitDialogProfile() {
        val builder = AlertDialog.Builder(requireContext())
        bindingDialogCenter = LayoutDialogEditPostBinding.inflate(layoutInflater)
        builder.setView(bindingDialogCenter?.root)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        var isEditTextChanged = false
        binding.editContent.addTextChangedListener {
            val hasTextChanged = it?.toString()?.isNotEmpty() ?: false
            isEditTextChanged = hasTextChanged
        }
        bindingDialogCenter?.btnDropChange?.setOnSingClickListener {
            findNavController().popBackStack()
            alertDialog.dismiss()
        }

        bindingDialogCenter?.btnChange?.setOnSingClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun setInformationUserPost() {
        viewModel.getInformationUser(viewModel.getIdUserPreference())
        viewModel.informationUser.observe(this) {
            if (it.avatarUser != null) {
                Glide.with(requireContext()).load(it.avatarUser).into(binding.imgAvatar)
            } else {
                Glide.with(requireContext()).load(R.drawable.ic_user).into(binding.imgAvatar)
            }

            binding.tvName.text = it.nameUser
        }
    }

}
