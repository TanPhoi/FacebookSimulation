package com.phoint.facebooksimulation.ui.posts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
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
import com.phoint.facebooksimulation.databinding.FragmentPostsAnArticleBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.permission.PermissionFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PostsAnArticleFragment :
    BaseFragment<FragmentPostsAnArticleBinding, PostsAnArticleViewModel>() {
    private var imageUrl: Uri? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingDialog: BottomDialogPostBinding? = null
    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Bạn đã cấp quyền", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Bạn đã từ chối cấp quyền", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private var startForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data
                handleResultImage(imageUrl)
            }
        }

    private fun handleResultImage(imageUrl: Uri?) {
        this.imageUrl = imageUrl
        if (imageUrl != null) {
            binding.imgStatus.adjustViewBounds = true
            binding.abPost.setBackGroundButtonImage(imageUrl)
            Glide.with(requireContext()).load(imageUrl).into(binding.imgStatus)
        } else {
            binding.imgStatus.setImageDrawable(null)
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_posts_an_article
    }

    override fun viewModelClass(): Class<PostsAnArticleViewModel> {
        return PostsAnArticleViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun initView() {
        val idUser = viewModel.getIdPreferences()
        viewModel.getInformationUser(idUser)
        viewModel.informationUser.observe(this) {
            if (it.avatarUser != null) {
                Glide.with(requireContext()).load(it.avatarUser).into(binding.imgAvatar)
            }
            binding.tvName.text = it.nameUser
        }

        binding.editContent.requestLayout()

        binding.editContent.addTextChangedListener {
            binding.abPost.setBackGroundButtonContent(it.toString())
        }

        binding.abPost.onSingClickListenerRight {
            val content = binding.editContent.text.toString().trim()
            if (content.isNotEmpty() || binding.imgStatus.drawable != null) {
                val post = Post().apply {
                    idPost = System.currentTimeMillis()
                    userIdPost = idUser
                    contentPost = content
                    dateTimePost = System.currentTimeMillis()
                    if (imageUrl != null) {
                        srcPost = imageUrl.toString()
                    }
                    privacyPost = binding.tvPermission.text.toString().trim()
                    viewModel.setIdPost(idPost!!)
                }
                viewModel.insertPost(post)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng nhập content hoặc thêm ảnh của bạn",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }

        val textRadio = arguments?.getString(PermissionFragment.PERMISSION) ?: "Công khai"
        val drawableLeftResId = arguments?.getInt(PermissionFragment.DRAWABLE_LEFT)

        binding.tvPermission.text = textRadio

        drawableLeftResId?.let { resourceId ->
            val drawableLeft = ContextCompat.getDrawable(requireContext(), resourceId)
            binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            val drawableRight =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)

            binding.tvPermission.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                drawableRight,
                null
            )
        }

        binding.rlPost.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.rlPost.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.rlPost.height

            // Tính toán kích thước của bàn phím
            val keyboardHeight = screenHeight - rect.bottom

            // Kiểm tra nếu bàn phím hiển thị và Fragment này đang hiển thị
            if (keyboardHeight > 0 && isVisible) {
                // Đẩy nút lên trong trường hợp bàn phím hiển thị
                val layoutParams =
                    binding.cvBottomDialog.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = keyboardHeight
                binding.cvBottomDialog.layoutParams = layoutParams
            } else {
                // Khôi phục vị trí ban đầu của nút khi bàn phím không hiển thị
                val layoutParams =
                    binding.cvBottomDialog.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = 0
                binding.cvBottomDialog.layoutParams = layoutParams
            }
        }

        dialog = BottomSheetDialog(requireContext())
        dialog?.show()

        binding.cvBottomDialog.setOnSingClickListener {
            dialog?.show()
        }

        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        createDialog()

        binding.tvPermission.setOnSingClickListener {
            findNavController().navigate(R.id.action_postsAnArticleFragment_to_permissionFragment)
        }

        binding.abPost.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    private fun createDialog() {
        bindingDialog = BottomDialogPostBinding.inflate(layoutInflater)
        bindingDialog!!.btnImage.setOnSingClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startForActivityResult.launch(intent)
                dialog?.dismiss()
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
        dialog?.setContentView(bindingDialog?.root!!)
    }

}
