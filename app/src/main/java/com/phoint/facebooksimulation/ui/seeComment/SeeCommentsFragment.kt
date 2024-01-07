package com.phoint.facebooksimulation.ui.seeComment

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.Sticker
import com.phoint.facebooksimulation.databinding.BottomDialogDeleteCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogDeletePostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReplyCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReportCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogSeeCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogSharePostBinding
import com.phoint.facebooksimulation.databinding.BottomReportSentBinding
import com.phoint.facebooksimulation.databinding.FragmentSeeCommentsBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.otherPeopleComment.OtherPeopleCommentFragment
import com.phoint.facebooksimulation.ui.otherPeopleComment.StickersAdapter
import com.phoint.facebooksimulation.ui.showImagePhoneApp.ImageAdapter
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SeeCommentsFragment : BaseFragment<FragmentSeeCommentsBinding, SeeCommentsViewModel>() {
    private var dialog: BottomSheetDialog? = null
    private var bindingMenu: BottomDialogSeeCommentBinding? = null
    private var bindingDeletePost: BottomDialogDeletePostBinding? = null
    private var bindingShare: BottomDialogSharePostBinding? = null
    private var bindingDeleteComment: BottomDialogDeleteCommentBinding? = null
    private var bindingReplyComment: BottomDialogReplyCommentBinding? = null
    private var bindingReportComment: BottomDialogReportCommentBinding? = null
    private var bindingReportSent: BottomReportSentBinding? = null
    private var commentList = ArrayList<Comment>()
    private var stickerList = ArrayList<Sticker>()
    private var adapter: CommentAdapter? = null
    private var adapterSticker: StickersAdapter? = null
    private var adapterImage: ImageAdapter? = null
    private var selectComment: Comment? = null
    private var selectReply: Comment? = null
    private var selectEditReply: Comment? = null
    private var positionReply: Int? = null
    private var replys: Reply? = null
    private var post: Post? = null
    private var positions: Int? = null
    private var uriImage: String? = null
    private var isButtonMode = false
    private var isInsertReplyMode = false
    private var isEditComment = false
    private var isEditReply = false
    private var isSavePost = false
    private var isLiked = false
    private var isclicked = false
    private var isClickedSticker = false

    companion object {
        const val PUBLIC = "Công khai"
        const val FRIENDS = "Bạn bè"
        const val PRIVATE = "Chỉ mình tôi"
        const val EDIT_PERMISSION = "EDIT_PERMISSION"
        private const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
    }

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

    private val startForCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl = result.data?.data
                handleCameraImageResult(imageUrl)
            }
        }

    private fun handleCameraImageResult(imageUrl: Uri?) {
        try {

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    override fun layoutRes(): Int {
        return R.layout.fragment_see_comments
    }

    override fun viewModelClass(): Class<SeeCommentsViewModel> {
        return SeeCommentsViewModel::class.java
    }

    override fun initView() {
        post = arguments?.getParcelable("post") as? Post

        viewModel.getAllDataByPost(post?.idPost ?: 0)
        handleLoading()
        handleShowPhoneKeyboard()
        showSoftInput()
        handleWatchProfileUser()
        handleDialogMenu()
        handleDialogShare()
        handleGetData()
        handleEditText()
        handleAdapter()
        handleComment()
        handleItemComment()
        handleItemReplyComment()
        handleLikePost()
        handleClickLikePost()
        handleCamera()
        handleStickerAdapter()

        binding.abComment.setExitCurrentScreen {
            findNavController().popBackStack()
        }

    }

    private fun handleWatchProfileUser() {
        binding.abComment.setOnClickImageUser {
            if (post?.userIdPost != viewModel.getIdUserPreferences()) {
                viewModel.getUserByIdPost(post?.userIdPost!!)
                viewModel.userById.observe(this) { user ->
                    findNavController().navigate(
                        R.id.action_seeCommentsFragment_to_profileOtherUserFragment,
                        bundleOf(Pair("otherUser", user))
                    )
                }
            } else {
                findNavController().navigate(R.id.action_seeCommentsFragment_to_profileFragment)
            }
        }

        binding.abComment.setOnClickNameUser {
            if (post?.userIdPost != viewModel.getIdUserPreferences()) {
                viewModel.getUserByIdPost(post?.userIdPost!!)
                viewModel.userById.observe(this) { user ->
                    findNavController().navigate(
                        R.id.action_seeCommentsFragment_to_profileOtherUserFragment,
                        bundleOf(Pair("otherUser", user))
                    )
                }
            } else {
                findNavController().navigate(R.id.action_seeCommentsFragment_to_profileFragment)
            }
        }
    }

    private fun handleStickerAdapter() {
        adapterSticker = StickersAdapter(stickerList)
        binding.rcvSticker.adapter = adapterSticker

        if (!isClickedSticker) {
            binding.btnSticker.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
        } else {
            binding.btnSticker.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling)
        }

        binding.btnSticker.setOnSingClickListener {
            if (!isClickedSticker) {
                binding.rcvSticker.visibility = View.VISIBLE
                binding.rcvShowImage.visibility = View.GONE
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.btnSticker.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling)
                binding.btnImage.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
                isClickedSticker = true
                isclicked = false
            } else {
                binding.rcvSticker.visibility = View.GONE
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                binding.btnSticker.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
                isClickedSticker = false
            }
        }

        adapterSticker?.onItemClickSticker = {
            if (it != null) {
                binding.relativeLayoutImage.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it.srcSticker).into(binding.imgComment)
                uriImage = it.srcSticker.toString()
                if (uriImage!!.isNotEmpty()) {
                    binding.btnInsertComment.visibility = View.VISIBLE
                    binding.rcvSticker.visibility = View.GONE
                    binding.btnSticker.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
                } else {
                    binding.btnInsertComment.visibility = View.GONE
                }
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        setDataSticker()
    }

    private fun handleCamera() {
        if (!isclicked) {
            binding.btnImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
        } else {
            binding.btnImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography_blue)
        }

        binding.btnImage.setOnSingClickListener {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (!isclicked) {
                    binding.rcvShowImage.visibility = View.VISIBLE
                    binding.rcvSticker.visibility = View.GONE
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    showImageGalleryAdapter()
                    adapterImage?.setShowImage(true)
                    binding.btnImage.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography_blue)
                    binding.btnSticker.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
                    isclicked = true
                    isClickedSticker = false
                } else {
                    binding.rcvShowImage.visibility = View.GONE
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    binding.btnImage.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
                    isclicked = false
                }

            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        binding.btnRemoveComment.setOnSingClickListener {
            binding.imgComment.setImageDrawable(null)
            uriImage = null
            binding.relativeLayoutImage.visibility = View.GONE
            binding.btnInsertComment.visibility = View.GONE
        }
    }

    private fun showImageGalleryAdapter() {
        adapterImage = ImageAdapter(requireContext())
        binding.rcvShowImage.adapter = adapterImage
        if (checkPermission()) {
            val imageList = getImagesFromMediaStore()
            adapterImage?.setImageList(imageList)
        } else {
            requestPermission()
        }

        adapterImage?.itemOnClick = {
            if (it != null) {
                binding.relativeLayoutImage.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it.toString()).into(binding.imgComment)
                uriImage = it.toString()
                if (uriImage?.isNotEmpty()!!) {
                    binding.btnImage.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
                    binding.btnInsertComment.visibility = View.VISIBLE
                    binding.rcvShowImage.visibility = View.GONE
                } else {
                    binding.btnInsertComment.visibility = View.GONE
                }
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun getImagesFromMediaStore(): List<Uri> {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                imageList.add(contentUri)
            }
        }
        return imageList
    }

    private fun requestPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            OtherPeopleCommentFragment.READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun setDataSticker() {
        val smileLaugh = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_smile_laugh
        }
        stickerList.add(smileLaugh)

        val cuteSmile = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_happy_cute_smile
        }
        stickerList.add(cuteSmile)

        val smileGrinning = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_smile_grinning
        }
        stickerList.add(smileGrinning)

        val smirking = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_smirking
        }
        stickerList.add(smirking)

        val cry = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_cry
        }
        stickerList.add(cry)

        val happy = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_happy
        }
        stickerList.add(happy)

        val heart = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_heart
        }
        stickerList.add(heart)

        val rich = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_rich
        }
        stickerList.add(rich)

        val sick = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_sick
        }
        stickerList.add(sick)

        val halo = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_halo
        }
        stickerList.add(halo)

        val puke = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_puke
        }
        stickerList.add(puke)

        val depressed = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_depressed
        }
        stickerList.add(depressed)

        val heartWind = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_heart_wind
        }
        stickerList.add(heartWind)

        val surprised = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_surprised
        }
        stickerList.add(surprised)

        val haha = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_haha
        }
        stickerList.add(haha)

        val shocked = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_shocked
        }
        stickerList.add(shocked)

        val devil = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_devil
        }
        stickerList.add(devil)

        val bored = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_bored
        }
        stickerList.add(bored)

        val dead = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_dead
        }
        stickerList.add(dead)

        val wondering = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_wondering
        }
        stickerList.add(wondering)

        val hungry = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_hungry
        }
        stickerList.add(hungry)

        val vomitOut = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_vomit_out
        }
        stickerList.add(vomitOut)

        val worried = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_worried
        }
        stickerList.add(worried)

        val shy = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_shy
        }
        stickerList.add(shy)

        val hurt = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_hurt
        }
        stickerList.add(hurt)

        val kiss = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_kiss
        }
        stickerList.add(kiss)

        val rest = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_rest
        }
        stickerList.add(rest)

        val wow = Sticker().apply {
            srcSticker = R.drawable.ic_feeling_wow
        }
        stickerList.add(wow)
    }

    private fun handleDialogShare() {
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        bindingShare = BottomDialogSharePostBinding.inflate(layoutInflater)
        binding.btnShare.setOnSingClickListener {
            dialog?.show()

            bindingShare?.btnPost?.setOnSingClickListener {
                dialog?.dismiss()
            }
            dialog?.setContentView(bindingShare?.root!!)
        }
    }

    private fun handleClickLikePost() {
        val currentIdUser = viewModel.getIdUserPreferences()
        binding.btnLike.setOnSingClickListener {
            viewModel.informationUserComment.observe(this) {
                if (!isLiked) {
                    val like = Like().apply {
                        likeUserId = currentIdUser
                        likePostId = post?.idPost
                        nameUser = it.nameUser
                    }
                    post?.likes?.add(like)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    binding.imgLike.setImageResource(R.drawable.ic_like_blue_tv)
                    binding.tvLike.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.blue
                        )
                    )
                    isLiked = true
                } else {
                    val likeToRemove = post?.likes?.find { it.likeUserId == currentIdUser }
                    post?.likes?.remove(likeToRemove)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    binding.imgLike.setImageResource(R.drawable.ic_like)
                    binding.tvLike.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                    isLiked = false
                }
            }
        }
        viewModel.doneUpdate.observe(this) {
            handleLikePost()
        }
    }

    private fun handleLikePost() {
        val likes = post?.likes?.map { it.likeUserId }
        val currentIdUser = viewModel.getIdUserPreferences()
        val isCurrentUserLiked = likes?.contains(currentIdUser)
        val otherLikedUserIds = likes?.filter { it != currentIdUser }
        val likeCount = likes?.size

        if (likeCount!! > 0) {
            binding.imgShowLike.visibility = View.VISIBLE
        } else {
            binding.imgShowLike.visibility = View.GONE
        }

        viewModel.userList.observe(this) { userList ->
            var likeText = if (likeCount > 3) {
                if (isCurrentUserLiked == true) {
                    val firstLikeUser = otherLikedUserIds?.firstOrNull()?.let { userId ->
                        userList?.find { it.idUser == userId }
                    }
                    if (firstLikeUser != null) {
                        val otherLikeCount = likeCount - 2
                        "Bạn và ${firstLikeUser.nameUser} $otherLikeCount người khác"
                    } else {
                        // Xử lý trường hợp không có người dùng khác đã thích bài đăng
                        val otherLikeCount = likeCount - 1
                        "Bạn và $otherLikeCount người khác"
                    }
                } else {
                    val otherLikeCount = likeCount - 1
                    "Bạn và $otherLikeCount người khác"
                }

            } else {
                if (isCurrentUserLiked == true) {
                    if (isCurrentUserLiked == true) {
                        if (likeCount == 1) {
                            "Bạn"
                        } else {
                            val otherLikeCount = likeCount - 1
                            "Bạn và $otherLikeCount người khác"
                        }
                    } else {
                        val otherLikedUsers = otherLikedUserIds?.mapNotNull { userId ->
                            userList?.find { it.idUser == userId }?.nameUser
                        }
                        val likeUserText = otherLikedUsers?.joinToString(", ")
                        "Bạn, $likeUserText và người khác"
                    }
                } else {
                    if (likeCount == 0) {
                        binding.tvPeopleLike.visibility = View.GONE
                        ""
                    } else {
                        val otherLikeCount = likeCount
                        "${otherLikeCount}"
                    }
                }
            }

            if (likeText.isNotEmpty()) {
                binding.tvPeopleLike.visibility = View.VISIBLE
                binding.tvPeopleLike.text = likeText
            } else {
                binding.tvPeopleLike.visibility = View.GONE
            }
        }

        if (isCurrentUserLiked == true) {
            binding.imgLike.setImageResource(R.drawable.ic_like_blue_tv)
            binding.tvLike.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            isLiked = true
        } else {
            binding.imgLike.setImageResource(R.drawable.ic_like)
            binding.tvLike.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            isLiked = false
        }
    }

    private fun handleShowPhoneKeyboard() {
        binding.edtComment.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun handleDialogMenu() {
        bindingMenu = BottomDialogSeeCommentBinding.inflate(layoutInflater)
        bindingDeletePost = BottomDialogDeletePostBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()

        viewModel.savePosts.observe(this) { savePostList ->
            val postId = post?.idPost ?: 0
            val isPostSaved = savePostList.any { it.savePost?.idPost == postId }
            // Cập nhật nội dung của nút btnSavePost dựa trên trạng thái lưu của bài viết
            isSavePost = isPostSaved
            if (isPostSaved) {
                bindingMenu?.btnSavePost?.text =
                    "Bỏ lưu bài viết \nGỡ khỏi danh sách các mục đã lưu"
                val drawableLeft = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_remove_save_favorite_post
                )
                bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )

            } else {
                bindingMenu?.btnSavePost?.text = "Lưu bài viết \nThêm vào danh sách các mục đã lưu"
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_post)
                bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
            }
        }

        binding.abComment.setPostMenu {
            dialog?.show()
            if (post?.isPinned == true) {
                bindingMenu?.btnPostPin?.text = "Bỏ ghim"
                val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_uppin)
                bindingMenu?.btnPostPin?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
            } else {
                bindingMenu?.btnPostPin?.text = "Ghim bài viết"
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_post_pins)
                bindingMenu?.btnPostPin?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
            }
            bindingMenu?.btnEditPost?.setOnSingClickListener {
                findNavController().navigate(
                    R.id.action_seeCommentsFragment_to_editPostFragment, bundleOf(
                        Pair("post", post)
                    )
                )
                dialog?.dismiss()
            }

            bindingMenu?.btnDeletePost?.setOnSingClickListener {
                dialog?.show()

                bindingDeletePost?.btnCancel?.setOnSingClickListener {
                    dialog?.dismiss()
                }

                bindingDeletePost?.btnEditPost?.setOnSingClickListener {
                    findNavController().navigate(
                        R.id.action_seeCommentsFragment_to_editPostFragment,
                        bundleOf(Pair("post", post))
                    )
                    dialog?.dismiss()
                }

                bindingDeletePost?.btnDeletePost?.setOnSingClickListener {
                    if (post != null) {
                        viewModel.deletePost(post!!)
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            findNavController().popBackStack()
                        }, 2000)
                        dialog?.dismiss()
                    }
                }
                dialog?.setContentView(bindingDeletePost?.root!!)
            }

            bindingMenu?.btnPostPin?.setOnSingClickListener {
                if (post?.isPinned == true) {
                    post?.isPinned = false
                    viewModel.updatePost(post!!)
                    bindingMenu?.btnPostPin?.text = "Ghim bài viết"
                } else {
                    post?.isPinned = true
                    viewModel.updatePost(post!!)
                    bindingMenu?.btnPostPin?.text = "Bỏ ghim"
                }
                dialog?.dismiss()
            }

            bindingMenu?.btnEditPermission?.setOnSingClickListener {
                val bundle = Bundle()
                bundle.putParcelable("post", post)
                bundle.putString(EDIT_PERMISSION, EDIT_PERMISSION)
                findNavController().navigate(
                    R.id.action_seeCommentsFragment_to_permissionFragment,
                    bundle
                )
                dialog?.dismiss()
            }

            // Khi click vào nút btnSavePost
            bindingMenu?.btnSavePost?.setOnClickListener {
                val postId = post?.idPost ?: 0
                val userId = viewModel.getIdUserPreferences()
                if (!isSavePost) {
                    val saveFavoritePost = SaveFavoritePost().apply {
                        id = System.currentTimeMillis()
                        userOwnerId = userId
                        postOwnerId = postId
                        timestamp = System.currentTimeMillis()
                        savePost = post
                    }
                    viewModel.insertSaveFavoritePost(saveFavoritePost)
                    bindingMenu?.btnSavePost?.text =
                        "Bỏ lưu bài viết \nGỡ khỏi danh sách các mục đã lưu"
                    val drawableLeft = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_remove_save_favorite_post
                    )
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = true
                } else {
                    viewModel.getAllSaveFavoritePost(userId, postId)
                    viewModel.saveFavoritePostUser.observe(this) {
                        viewModel.deleteSaveFavoritePost(it)
                    }
                    bindingMenu?.btnSavePost?.text =
                        "Lưu bài viết \nThêm vào danh sách các mục đã lưu"
                    val drawableLeft =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_post)
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = false
                }
                dialog?.dismiss()
            }

            dialog?.setContentView(bindingMenu?.root!!)
        }
    }

    private fun showSoftInput() {
        binding.edtComment.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun handleEditText() {
        binding.edtComment.setOnSingClickListener {
            binding.rcvShowImage.visibility = View.GONE
            binding.rcvSticker.visibility = View.GONE
            binding.btnSticker.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
            binding.btnImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
        }

        binding.edtComment.addTextChangedListener {
            if (it?.isNotEmpty()!!) {
                binding.btnInsertComment.visibility = View.VISIBLE
            } else {
                binding.btnInsertComment.visibility = View.GONE
                positionReply = null
                positions = null
                selectReply = null
                selectComment = null
                selectEditReply = null
                replys = null
            }
        }
    }

    private fun handleAdapter() {
        bindingDeleteComment = BottomDialogDeleteCommentBinding.inflate(layoutInflater)
        bindingReplyComment = BottomDialogReplyCommentBinding.inflate(layoutInflater)
        bindingReportComment = BottomDialogReportCommentBinding.inflate(layoutInflater)
        bindingReportSent = BottomReportSentBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        adapter = CommentAdapter(post?.comments!!, viewModel.getIdUserPreferences())
        binding.rcvComment.adapter = adapter
        binding.rcvComment.isNestedScrollingEnabled = false

        viewModel.commentList.observe(this) {
            commentList.addAll(it)
            adapter?.notifyDataSetChanged()
        }


        viewModel.userList.observe(this) {
            adapter?.setUsers(it)
            adapter?.notifyDataSetChanged()
        }

        viewModel.postList.observe(this) {
            adapter?.setPosts(it)
            adapter?.notifyDataSetChanged()
        }

//        viewModel.replyList.observe(this) {
//            adapter?.updateReplyList(it)
//            adapter?.notifyDataSetChanged()
//        }

        adapter?.onClickDeleteComment = { comment, position ->
            dialog?.show()
            if (comment.userIdComment == viewModel.getIdUserPreferences()) {
                // Delete comment
                bindingDeleteComment?.btnDeleteComment?.setOnSingClickListener {
                    post?.comments?.remove(comment)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyDataSetChanged()
                    dialog?.dismiss()
                }

                //dismiss
                bindingDeleteComment?.btnDismiss?.setOnSingClickListener {
                    dialog?.dismiss()
                }

                //Copy
                bindingDeleteComment?.btnCopy?.setOnSingClickListener {
                    val clipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("ContentComment", comment.contentComment)
                    clipboardManager.setPrimaryClip(clipData)

                    val clipboardData = clipboardManager.primaryClip?.getItemAt(0)?.text
                    clipboardData?.let {
                        val pastedText = clipboardData.toString()
                        Log.d("Clipboard", "Nội dung đã sao chép: $pastedText")
                    }
                    dialog?.dismiss()
                }

                //Edit
                bindingDeleteComment?.btnEdit?.setOnSingClickListener {
                    val contentComment = "${comment.contentComment}"
                    val spannableStringBuilder = SpannableStringBuilder(contentComment).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            contentComment.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isEditComment = true
                    selectComment = comment
                    positions = position
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(contentComment.length)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                //Reply
                bindingDeleteComment?.btnReply?.setOnSingClickListener {
                    val nameUser = "${comment.nameUser} "
                    val spannableStringBuilder = SpannableStringBuilder(nameUser).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            nameUser.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isButtonMode = true
                    selectComment = comment
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(nameUser.length)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                dialog?.setContentView(bindingDeleteComment?.root!!)
            } else {
                //Reply
                bindingReplyComment?.btnReply?.setOnSingClickListener {
                    val nameUser = "${comment.nameUser} "
                    val spannableStringBuilder = SpannableStringBuilder(nameUser).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            nameUser.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isButtonMode = true
                    selectComment = comment
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(nameUser.length)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                //dismiss
                bindingReplyComment?.btnDismiss?.setOnSingClickListener {
                    dialog?.dismiss()
                }

                //copy
                bindingReplyComment?.btnCopy?.setOnSingClickListener {
                    val clipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("ContentComment", comment.contentComment)
                    clipboardManager.setPrimaryClip(clipData)

                    val clipboardData = clipboardManager.primaryClip?.getItemAt(0)?.text
                    clipboardData?.let {
                        val pastedText = clipboardData.toString()
                        Log.d("Clipboard", "Nội dung đã sao chép: $pastedText")
                    }
                    dialog?.dismiss()
                }

                //Hide Comment
                val commentHide =
                    post?.comments!![position].hiddenUsers.any { it.idUser == viewModel.getIdUserPreferences() }
                if (commentHide) {
                    bindingReplyComment?.btnHide?.text = "Bỏ ẩn"
                } else {
                    bindingReplyComment?.btnHide?.text = "Ẩn"
                }
                bindingReplyComment?.btnHide?.setOnSingClickListener {
                    val commentHide =
                        post?.comments!![position].hiddenUsers.any { it.idUser == viewModel.getIdUserPreferences() }
                    if (commentHide) {
                        val currentUserPosition =
                            post?.comments!![position].hiddenUsers.indexOfFirst { it.idUser == viewModel.getIdUserPreferences() }
                        post?.comments!![position].hiddenUsers.removeAt(currentUserPosition)
                        viewModel.updatePost(post ?: Post())
                        viewModel.saveDataByPost.observe(this) {
                            it.savePost = post
                            viewModel.updateSaveFavoritePost(it)
                        }
                        adapter?.notifyDataSetChanged()
                    } else {
                        viewModel.informationUserComment.observe(this) { user ->
                            post?.comments!![position].hiddenUsers.add(user)
                            viewModel.updatePost(post ?: Post())
                            viewModel.saveDataByPost.observe(this) {
                                it.savePost = post
                                viewModel.updateSaveFavoritePost(it)
                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }
                    dialog?.dismiss()
                }

                // report comment
                bindingReplyComment?.btnReport?.setOnSingClickListener {
                    dialog?.show()

                    bindingReportComment?.btnDismiss?.setOnSingClickListener {
                        dialog?.dismiss()
                    }

                    bindingReportComment?.btnSentReport?.setOnSingClickListener {
                        bindingReportSent?.tvBlock?.text =
                            "Chặn trang cá nhân của ${comment.nameUser}"
                        bindingReportSent?.tvHideAll?.text = "Ẩn tất cả từ ${comment.nameUser}"
                        dialog?.show()
                        val report = Report().apply {
                            userId = comment.userIdComment
                            commentId = comment.idComment
                            content = comment.contentComment
                            reason = "Bình luận này có vấn đề"
                            timestamp = System.currentTimeMillis()
                        }
                        viewModel.insertReport(report)

                        bindingReportSent?.btnDone?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReportSent?.btnUndo?.setOnSingClickListener {
                            viewModel.getAllReportComment(
                                comment.idComment!!,
                                comment.userIdComment!!
                            )
                            viewModel.reportComment.observe(this) {
                                viewModel.deleteReport(it)
                                bindingReportSent?.btnUndo?.visibility = View.GONE
                            }
                        }

                        dialog?.setContentView(bindingReportSent?.root!!)
                    }

                    bindingReportComment?.btnAddInformation?.setOnSingClickListener {
                        dialog?.dismiss()
                    }


                    dialog?.setContentView(bindingReportComment?.root!!)
                }
                dialog?.setContentView(bindingReplyComment?.root!!)
            }

        }

        adapter?.onClickDeleteReply = { comment, reply, position ->
            dialog?.show()
            if (reply.userIdComment == viewModel.getIdUserPreferences()) {
                // delete
                bindingDeleteComment?.btnDeleteComment?.setOnSingClickListener {
                    comment.replies.remove(reply)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyDataSetChanged()
                    dialog?.dismiss()
                }

                // reply
                bindingDeleteComment?.btnReply?.setOnSingClickListener {
                    val nameUsers = "@${reply.userName} "
                    val spannableStringBuilder = SpannableStringBuilder(nameUsers).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            nameUsers.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isInsertReplyMode = true
                    selectReply = comment
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(nameUsers.length)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                // edit
                bindingDeleteComment?.btnEdit?.setOnSingClickListener {
                    val contentReply = "@${reply.contentReply} "
                    val spannableStringBuilder = SpannableStringBuilder(contentReply).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            contentReply.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isEditReply = true
                    positionReply = position
                    selectEditReply = comment
                    replys = reply
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(contentReply.length)

                    binding.edtComment.requestFocus()
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                // copy
                bindingDeleteComment?.btnCopy?.setOnSingClickListener {
                    val clipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("ContentComment", reply.contentReply)
                    clipboardManager.setPrimaryClip(clipData)

                    val clipboardData = clipboardManager.primaryClip?.getItemAt(0)?.text
                    clipboardData?.let {
                        val pastedText = clipboardData.toString()
                        Log.d("Clipboard", "Nội dung đã sao chép: $pastedText")
                    }
                    dialog?.dismiss()
                }

                // dismiss
                bindingDeleteComment?.btnDismiss?.setOnSingClickListener {
                    dialog?.dismiss()
                }

                dialog?.setContentView(bindingDeleteComment?.root!!)
            } else {
                // reply
                bindingReplyComment?.btnReply?.setOnSingClickListener {
                    val nameUsers = "@${reply.userName} "
                    val spannableStringBuilder = SpannableStringBuilder(nameUsers).apply {
                        setSpan(
                            ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                            0,
                            nameUsers.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        append(" ")
                    }
                    isInsertReplyMode = true
                    selectReply = comment
                    binding.edtComment.text = spannableStringBuilder
                    binding.edtComment.setSelection(nameUsers.length)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
                    dialog?.dismiss()
                }

                // dismiss
                bindingReplyComment?.btnDismiss?.setOnSingClickListener {
                    dialog?.dismiss()
                }

                // copy
                bindingReplyComment?.btnCopy?.setOnSingClickListener {
                    val clipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("ContentComment", reply.contentReply)
                    clipboardManager.setPrimaryClip(clipData)

                    val clipboardData = clipboardManager.primaryClip?.getItemAt(0)?.text
                    clipboardData?.let {
                        val pastedText = clipboardData.toString()
                        Log.d("Clipboard", "Nội dung đã sao chép: $pastedText")
                    }
                    dialog?.dismiss()
                }

                // hide reply
                val replyHide =
                    comment.replies[position].hiddenUsers.any { it.idUser == viewModel.getIdUserPreferences() }
                if (replyHide) {
                    bindingReplyComment?.btnHide?.text = "Bỏ ẩn"
                } else {
                    bindingReplyComment?.btnHide?.text = "Ẩn"
                }
                bindingReplyComment?.btnHide?.setOnSingClickListener {
                    val replyHide =
                        comment.replies[position].hiddenUsers.any { it.idUser == viewModel.getIdUserPreferences() }
                    if (replyHide) {
                        val currentUserPosition =
                            comment.replies[position].hiddenUsers.indexOfFirst { it.idUser == viewModel.getIdUserPreferences() }
                        comment.replies[position].hiddenUsers.removeAt(currentUserPosition)
                        viewModel.updatePost(post ?: Post())
                        viewModel.saveDataByPost.observe(this) {
                            it.savePost = post
                            viewModel.updateSaveFavoritePost(it)
                        }
                        adapter?.notifyDataSetChanged()
                    } else {
                        viewModel.informationUserComment.observe(this) { user ->
                            comment.replies[position].hiddenUsers.add(user)
                            viewModel.updatePost(post ?: Post())
                            viewModel.saveDataByPost.observe(this) {
                                it.savePost = post
                                viewModel.updateSaveFavoritePost(it)
                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }
                    dialog?.dismiss()
                }

                // report
                bindingReplyComment?.btnReport?.setOnSingClickListener {
                    dialog?.show()

                    bindingReportComment?.btnDismiss?.setOnSingClickListener {
                        dialog?.dismiss()
                    }

                    bindingReportComment?.btnSentReport?.setOnSingClickListener {
                        dialog?.show()
                        bindingReportSent?.tvBlock?.text =
                            "Chặn trang cá nhân của ${reply.userName}"
                        bindingReportSent?.tvHideAll?.text = "Ẩn tất cả từ ${reply.userName}"
                        val report = Report().apply {
                            userId = reply.userIdComment
                            commentId = reply.replyId
                            reason = "Bình luận này có vấn đề"
                            timestamp = System.currentTimeMillis()
                        }
                        viewModel.insertReport(report)

                        bindingReportSent?.btnDone?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReportSent?.btnUndo?.setOnSingClickListener {
                            viewModel.getAllReportComment(
                                reply.replyId ?: 0,
                                reply.userIdComment ?: 0
                            )

                            viewModel.reportComment.observe(this) {
                                viewModel.deleteReport(it)
                                bindingReportSent?.btnUndo?.visibility = View.GONE
                            }
                        }

                        dialog?.setContentView(bindingReportSent?.root!!)
                    }

                    bindingReportComment?.btnAddInformation?.setOnSingClickListener {
                        dialog?.dismiss()
                    }


                    dialog?.setContentView(bindingReportComment?.root!!)
                }

                dialog?.setContentView(bindingReplyComment?.root!!)
            }
        }

        adapter?.onClickLikeComment = {
            handleLikeClicked(it)
        }

        adapter?.onClickLikeReply = { comment, reply ->
            handleLikeClickedReply(comment, reply)
        }

    }

    private fun handleLikeClickedReply(comment: Comment, reply: Reply) {
        reply.let { clickedPost ->
            val currentUser = viewModel.getIdUserPreferences()

            // Kiểm tra xem người dùng đã like bài post trước đó chưa
            val isLikedByCurrentUser = clickedPost.likes.any { it.likeUserId == currentUser }

            if (isLikedByCurrentUser) {
                // Nếu đã like trước đó, thì xóa like của người dùng
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    clickedPost.likes.removeIf { it.likeUserId == currentUser }
                    clickedPost.liked = false
                }
            } else {
                // Ngược lại, thêm like của người dùng
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                //like.likeId = System.currentTimeMillis()
                viewModel.informationUser.observe(this) {
                    like.nameUser = it.nameUser
                }
                clickedPost.likes.add(like)
            }
            // Cập nhật bài post trong ViewModel
            viewModel.updatePost(post ?: Post())
            viewModel.saveDataByPost.observe(this) {
                it.savePost = post
                viewModel.updateSaveFavoritePost(it)
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleLikeClicked(comment: Comment?) {
        comment?.let { clickedPost ->
            val currentUser = viewModel.getIdUserPreferences()

            // Kiểm tra xem người dùng đã like bài post trước đó chưa
            val isLikedByCurrentUser = clickedPost.likes.any { it.likeUserId == currentUser }

            if (isLikedByCurrentUser) {
                // Nếu đã like trước đó, thì xóa like của người dùng
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    clickedPost.likes.removeIf { it.likeUserId == currentUser }
                    clickedPost.liked = false
                }
            } else {
                // Ngược lại, thêm like của người dùng
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                //like.likeId = System.currentTimeMillis()
                viewModel.informationUser.observe(this) {
                    like.nameUser = it.nameUser
                }
                clickedPost.likes.add(like)
            }
            // Cập nhật bài post trong ViewModel
            viewModel.updatePost(post ?: Post())
            viewModel.saveDataByPost.observe(this) {
                it.savePost = post
                viewModel.updateSaveFavoritePost(it)
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleGetData() {
        viewModel.getInformationUserComment(viewModel.getIdUserPreferences())
        viewModel.getJoinDataComment(post?.idPost ?: 0)
        viewModel.getInformationUser(post?.userIdPost ?: 0)
        viewModel.getPostDataByUser(post?.userIdPost ?: 0, post?.idPost ?: 0)

        viewModel.postDataByUser.observe(this) {
            val formattedDateTime = getFormattedDateTime(post?.dateTimePost)
            binding.abComment.setDateTime(formattedDateTime)

            if (it.srcPost != null) {
                binding.imgStatus.adjustViewBounds = true
                binding.imgStatus.visibility = View.VISIBLE
                Glide.with(requireContext()).load(it.srcPost).into(binding.imgStatus)
            } else {
                binding.imgStatus.visibility = View.GONE
            }

            if (it.contentPost != null) {
                binding.tvContent.text = it.contentPost
                binding.tvContent.visibility = View.VISIBLE
            } else {
                binding.tvContent.visibility = View.GONE
            }

            when (it.privacyPost) {
                PUBLIC -> {
                    binding.abComment.setPermissionPost(R.drawable.ic_permission_public)
                }

                PRIVATE -> {
                    binding.abComment.setPermissionPost(R.drawable.ic_permission_padlock)
                }

                FRIENDS -> {
                    binding.abComment.setPermissionPost(R.drawable.ic_permission_friend)
                }

                else -> {
                    binding.abComment.setPermissionPost(R.drawable.ic_permission_public)
                }
            }
        }

        viewModel.informationUser.observe(this) {
            binding.abComment.setTextUser(it.nameUser ?: "")
            if (it.avatarUser != null) {
                binding.abComment.setImageUser(it.avatarUser ?: "")
            }
        }
    }

    private fun handleLoading() {
        viewModel.startLoading()
        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }
    }

    private fun handleItemReplyComment() {
        adapter?.onClickReply = { comment, reply ->
            val nameUsers = "@${reply.userName} "
            val spannableStringBuilder = SpannableStringBuilder(nameUsers).apply {
                setSpan(
                    ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                    0,
                    nameUsers.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                append(" ")
            }
            isInsertReplyMode = true
            selectReply = comment
            binding.edtComment.text = spannableStringBuilder
            binding.edtComment.setSelection(nameUsers.length)
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun handleItemComment() {
        adapter?.onClickReplyComment = { comment ->
            val nameUser = "@${comment.nameUser} "
            val spannableStringBuilder = SpannableStringBuilder(nameUser).apply {
                setSpan(
                    ForegroundColorSpan(Color.parseColor("#3FA6F8")),
                    0,
                    nameUser.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                append(" ")
            }
            isButtonMode = true
            selectComment = comment
            binding.edtComment.text = spannableStringBuilder
            binding.edtComment.setSelection(nameUser.length)
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun handleComment() {
        binding.btnInsertComment.setOnSingClickListener {
            val commentLine = binding.edtComment.text.toString().trim()
            if (!isButtonMode && !isInsertReplyMode && !isEditComment && !isEditReply) {
                viewModel.informationUserComment.observe(this) { user ->
                    val comment = Comment().apply {
                        idComment = System.currentTimeMillis()
                        userIdComment = user.idUser
                        postIdComment = post?.idPost
                        nameUser = user.nameUser
                        contentComment = commentLine
                        if (uriImage != null) {
                            imageComment = uriImage.toString()
                        }
                        timestamp = System.currentTimeMillis()
                        replies = ArrayList()
                    }
                    post?.comments?.add(0, comment)
                    viewModel.updatePost(post!!)
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyItemInserted(0)
                }
                val imm1 =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm1.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.rcvComment.scrollToPosition(0)
                binding.edtComment.text = null
                if (uriImage != null) {
                    uriImage = null
                }
                binding.edtComment.clearFocus()
                binding.imgComment.setImageDrawable(null)
                binding.relativeLayoutImage.visibility = View.GONE
            } else if (isButtonMode && !isInsertReplyMode && !isEditComment) {
                val replyComment = binding.edtComment.text.toString().trim()
                viewModel.informationUserComment.observe(this) { user ->
                    val reply = Reply().apply {
                        replyId = System.currentTimeMillis()
                        commentId = selectComment?.idComment ?: 0
                        userIdComment = viewModel.getIdUserPreferences()
                        userName = user.nameUser
                        postIdComment = post?.idPost
                        contentReply = replyComment
                        if (uriImage != null) {
                            imageReply = uriImage.toString()
                        }
                        timestamp = System.currentTimeMillis()
                    }
                    selectComment?.replies?.add(reply)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyDataSetChanged()
                }
                val imm2 =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.edtComment.text = null
                isButtonMode = false
                if (uriImage != null) {
                    uriImage = null
                }
                binding.edtComment.clearFocus()
            } else if (isInsertReplyMode) {
                val replyLine = binding.edtComment.text.toString().trim()
                viewModel.informationUserComment.observe(this) { user ->
                    val replyComment = Reply().apply {
                        replyId = System.currentTimeMillis()
                        commentId = selectReply?.idComment ?: 0
                        userIdComment = viewModel.getIdUserPreferences()
                        userName = user.nameUser
                        postIdComment = post?.idPost
                        contentReply = replyLine
                        if (uriImage != null) {
                            imageReply = uriImage.toString()
                        }
                        timestamp = System.currentTimeMillis()
                    }
                    selectReply?.replies?.add(replyComment)
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyDataSetChanged()
                }
                val imm2 =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.edtComment.text = null
                isInsertReplyMode = false
                if (uriImage != null) {
                    uriImage = null
                }
                binding.edtComment.clearFocus()
            } else if (isEditComment && !isEditReply) {
                val editComment = binding.edtComment.text.toString().trim()
                post?.comments?.let { comments ->
                    if (positions in comments.indices) {
                        comments[positions ?: 0].contentComment = editComment
                        viewModel.updatePost(post ?: Post())
                        viewModel.saveDataByPost.observe(this) {
                            it.savePost = post
                            viewModel.updateSaveFavoritePost(it)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                }
                val imm2 =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.edtComment.text = null
                isEditComment = false
                binding.edtComment.clearFocus()
            } else if (isEditReply && !isEditComment) {
                val editReply = binding.edtComment.text.toString().trim()
                if (editReply.isNotEmpty() && selectEditReply?.idComment == replys?.commentId) {
                    replys?.contentReply = editReply
                    selectEditReply!!.replies[positionReply ?: 0] = replys ?: Reply()
                    viewModel.updatePost(post ?: Post())
                    viewModel.saveDataByPost.observe(this) {
                        it.savePost = post
                        viewModel.updateSaveFavoritePost(it)
                    }
                    adapter?.notifyDataSetChanged()
                }
                val imm2 =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(view?.windowToken, 0)
                binding.edtComment.text = null
                isEditReply = false
                binding.edtComment.clearFocus()
            }
        }
    }

    private fun getFormattedDateTime(dateTime: Long?): String {
        dateTime?.let {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - dateTime

            return when {
                elapsedTime < 60 * 1000 -> "Vừa xong"
                elapsedTime < 60 * 60 * 1000 -> "${elapsedTime / (60 * 1000)} phút trước"
                elapsedTime < 24 * 60 * 60 * 1000 -> "${elapsedTime / (60 * 60 * 1000)} giờ trước"
                elapsedTime < 7 * 24 * 60 * 60 * 1000 -> "${elapsedTime / (24 * 60 * 60 * 1000)} ngày trước"
                elapsedTime < 30 * 24 * 60 * 60 * 1000 -> "${elapsedTime / (7 * 24 * 60 * 60 * 1000)} tuần trước"
                elapsedTime < 365 * 24 * 60 * 60 * 1000 -> "${elapsedTime / (30 * 24 * 60 * 60 * 1000)} tháng trước"
                else -> "${elapsedTime / (365 * 24 * 60 * 60 * 1000)} năm trước"
            }
        }
        return ""
    }
}



