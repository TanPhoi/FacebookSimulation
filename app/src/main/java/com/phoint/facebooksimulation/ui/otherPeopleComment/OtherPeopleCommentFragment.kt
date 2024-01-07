package com.phoint.facebooksimulation.ui.otherPeopleComment

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.Sticker
import com.phoint.facebooksimulation.databinding.BottomDialogDeleteCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogMenuOtherUserBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonRepostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonsForReportingBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReplyCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReportCommentBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReportPostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogSharePostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogUndoNewspaperBinding
import com.phoint.facebooksimulation.databinding.BottomReportSentBinding
import com.phoint.facebooksimulation.databinding.FragmentOtherPeopleCommentBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.seeComment.CommentAdapter
import com.phoint.facebooksimulation.ui.seeComment.SeeCommentsFragment
import com.phoint.facebooksimulation.ui.showImagePhoneApp.ImageAdapter
import com.phoint.facebooksimulation.util.setOnSingClickListener

class OtherPeopleCommentFragment :
    BaseFragment<FragmentOtherPeopleCommentBinding, OtherPeopleCommentViewModel>() {
    private var dialog: BottomSheetDialog? = null
    private var bindingMenu: BottomDialogMenuOtherUserBinding? = null
    private var bindingShare: BottomDialogSharePostBinding? = null
    private var bindingDeleteComment: BottomDialogDeleteCommentBinding? = null
    private var bindingReplyComment: BottomDialogReplyCommentBinding? = null
    private var bindingReportComment: BottomDialogReportCommentBinding? = null
    private var bindingReportSent: BottomReportSentBinding? = null
    private var bindingReportPost: BottomDialogReportPostBinding? = null
    private var bindingUnDoNewspaper: BottomDialogUndoNewspaperBinding? = null
    private var bindingReasonReport: BottomDialogReasonRepostBinding? = null
    private var bindingReasonsForReporting: BottomDialogReasonsForReportingBinding? = null
    private var post: Post? = null
    private var adapter: CommentAdapter? = null
    private var adapterSticker: StickersAdapter? = null
    private var commentList = ArrayList<Comment>()
    private var stickerList = ArrayList<Sticker>()
    private var isButtonMode = false
    private var isInsertReplyMode = false
    private var isEditComment = false
    private var isEditReply = false
    private var selectComment: Comment? = null
    private var selectReply: Comment? = null
    private var selectEditReply: Comment? = null
    private var positions: Int? = null
    private var positionReply: Int? = null
    private var replys: Reply? = null
    private var isLiked = false
    private var adapterImage: ImageAdapter? = null
    private var isclicked = false
    private var uriImage: String? = null
    private var isClickedSticker = false
    private var isSavePost = false
    private var isReply: Reply? = null

    companion object {
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
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

    override fun layoutRes(): Int {
        return R.layout.fragment_other_people_comment
    }

    override fun viewModelClass(): Class<OtherPeopleCommentViewModel> {
        return OtherPeopleCommentViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun initView() {
        post = arguments?.getParcelable("post") as? Post
        viewModel.getAllDataByPost(post?.idPost ?: 0)
        handleShowPhoneKeyboard()
        handleDialogMenu()
        handleDialogShare()
        handleGetDataUser()
        handleEditText()
        handleAdapterComment()
        handleComment()
        handleCommentAdapter()
        handleReplyComment()
        handleLikePost()
        handleClickLikePost()
        handleCamera()
        handleStickerAdapter()

        binding.abComment.setExitCurrentScreen {
            findNavController().popBackStack()
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
                binding.btnSticker.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling)
                binding.btnImage.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                isClickedSticker = true
                isclicked = false
            } else {
                isClickedSticker = false
                binding.rcvSticker.visibility = View.GONE
                binding.btnSticker.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
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

    private fun requestPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permission),
            READ_EXTERNAL_STORAGE_REQUEST_CODE
        )
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

    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
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
                    showImageGalleryAdapter()
                    adapterImage?.setShowImage(true)
                    binding.btnImage.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography_blue)
                    binding.btnSticker.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_feeling_while)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    isclicked = true
                    isClickedSticker = false
                } else {
                    isclicked = false
                    binding.rcvShowImage.visibility = View.GONE
                    binding.btnImage.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_photography)
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
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

                    val newNotification = Notification().apply {
                        userId = post?.userIdPost
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        notificationType = "${it.nameUser} thích ảnh của bạn: ${post?.contentPost}"
                        profilePicture = R.drawable.ic_like_blue
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)

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

                    viewModel.getNotificationById(
                        viewModel.getIdUserPreferences(),
                        post?.userIdPost!!,
                        post?.idPost!!
                    )
                    viewModel.notificationById.observe(this) {
                        viewModel.deleteNotification(it)
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
                    viewModel.getNotificationById(
                        viewModel.getIdUserPreferences(),
                        reply.userIdComment!!,
                        post?.idPost!!
                    )
                    viewModel.notificationById.observe(this) {
                        viewModel.deleteNotification(it)
                    }
                }
            } else {
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                viewModel.informationUser.observe(this) {
                    like.nameUser = it.nameUser
                }
                clickedPost.likes.add(like)

                viewModel.informationUserComment.observe(this) { user ->
                    val newNotification = Notification().apply {
                        userId = reply.userIdComment
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        commentId = clickedPost.replyId
                        notificationType =
                            "${user.nameUser} đã thích bình luận của bạn: ${clickedPost.contentReply}."
                        profilePicture = R.drawable.ic_like_blue
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)
                }

            }
            viewModel.updatePost(post ?: Post())
            viewModel.saveDataByPost.observe(this) {
                it.savePost = post
                viewModel.updateSaveFavoritePost(it)
            }
            adapter?.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleLikeClicked(comment: Comment?) {
        comment?.let { clickedPost ->
            val currentUser = viewModel.getIdUserPreferences()

            val isLikedByCurrentUser = clickedPost.likes.any { it.likeUserId == currentUser }

            if (isLikedByCurrentUser) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    clickedPost.likes.removeIf { it.likeUserId == currentUser }
                    clickedPost.liked = false
                    viewModel.getNotificationById(
                        viewModel.getIdUserPreferences(),
                        comment.userIdComment!!,
                        post?.idPost!!
                    )
                    viewModel.notificationById.observe(this) {
                        viewModel.deleteNotification(it)
                    }
                }
            } else {
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                viewModel.informationUser.observe(this) {
                    like.nameUser = it.nameUser
                }
                clickedPost.likes.add(like)

                viewModel.informationUserComment.observe(this) { user ->
                    val newNotification = Notification().apply {
                        userId = comment.userIdComment
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        commentId = clickedPost.idComment
                        notificationType =
                            "${user.nameUser} đã thích bình luận của bạn: ${clickedPost.contentComment}."
                        profilePicture = R.drawable.ic_like_blue
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)
                }

            }
            viewModel.updatePost(post ?: Post())
            viewModel.saveDataByPost.observe(this) {
                it.savePost = post
                viewModel.updateSaveFavoritePost(it)
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleShowPhoneKeyboard() {
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

    @SuppressLint("NotifyDataSetChanged", "LogNotTimber")
    private fun handleAdapterComment() {
        bindingDeleteComment = BottomDialogDeleteCommentBinding.inflate(layoutInflater)
        bindingReplyComment = BottomDialogReplyCommentBinding.inflate(layoutInflater)
        bindingReportComment = BottomDialogReportCommentBinding.inflate(layoutInflater)
        bindingReportSent = BottomReportSentBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        adapter = CommentAdapter(post?.comments!!, viewModel.getIdUserPreferences())
        binding.rcvComment.adapter = adapter
        binding.rcvComment.isNestedScrollingEnabled = false

        viewModel.getJoinDataComment(post?.idPost ?: 0)

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

    private fun handleReplyComment() {
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
            isReply = reply
            binding.edtComment.text = spannableStringBuilder
            binding.edtComment.setSelection(nameUsers.length)
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.edtComment, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun handleCommentAdapter() {
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

                    val newNotification = Notification().apply {
                        userId = post?.userIdPost
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        commentId = comment.idComment
                        notificationType = "${user.nameUser} đã bình luận về ảnh của bạn"
                        content = "•\"${comment.contentComment}\""
                        profilePicture = R.drawable.ic_chat_bubble_talk_sms
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)

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

                    val newNotification = Notification().apply {
                        userId = selectComment?.userIdComment ?: 0
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        commentId = reply.replyId
                        notificationType =
                            "${user.nameUser} đã nhắc đến bạn trong bình luận của bạn ấy."
                        profilePicture = R.drawable.ic_chat_bubble_talk_sms
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)

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

                    val newNotification = Notification().apply {
                        userId = isReply?.userIdComment ?: 0
                        senderId = viewModel.getIdUserPreferences()
                        postId = post?.idPost
                        commentId = replyComment.replyId
                        notificationType =
                            "${user.nameUser} đã nhắc đến bạn trong bình luận của bạn ấy."
                        profilePicture = R.drawable.ic_chat_bubble_talk_sms
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        isNotification = false
                        viewed = false
                    }

                    viewModel.insertNotification(newNotification)

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

    private fun handleGetDataUser() {
        viewModel.getInformationUserComment(viewModel.getIdUserPreferences())
        viewModel.getInformationUser(post?.userIdPost ?: 0)

        viewModel.informationUser.observe(this) {
            binding.abComment.setTextUser(it.nameUser ?: "")
            if (it.avatarUser != null) {
                binding.abComment.setImageUser(it.avatarUser ?: "")
            }
        }

        val formattedDateTime = getFormattedDateTime(post?.dateTimePost)
        binding.abComment.setDateTime(formattedDateTime)

        if (post?.srcPost != null) {
            binding.imgStatus.visibility = View.VISIBLE
            binding.imgStatus.adjustViewBounds = true
            Glide.with(requireContext()).load(post?.srcPost).into(binding.imgStatus)
        } else {
            binding.imgStatus.visibility = View.GONE
        }


        if (post?.contentPost != null) {
            binding.tvContent.visibility = View.VISIBLE
            binding.tvContent.text = post?.contentPost
        } else {
            binding.tvContent.visibility = View.GONE
        }

        when (post?.privacyPost) {
            SeeCommentsFragment.PUBLIC -> {
                binding.abComment.setPermissionPost(R.drawable.ic_permission_public)
            }

            SeeCommentsFragment.PRIVATE -> {
                binding.abComment.setPermissionPost(R.drawable.ic_permission_padlock)
            }

            SeeCommentsFragment.FRIENDS -> {
                binding.abComment.setPermissionPost(R.drawable.ic_permission_friend)
            }

            else -> {
                binding.abComment.setPermissionPost(R.drawable.ic_permission_public)
            }
        }
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

    private fun handleDialogMenu() {
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        bindingMenu = BottomDialogMenuOtherUserBinding.inflate(layoutInflater)
        bindingReportPost = BottomDialogReportPostBinding.inflate(layoutInflater)
        bindingUnDoNewspaper = BottomDialogUndoNewspaperBinding.inflate(layoutInflater)
        bindingReasonReport = BottomDialogReasonRepostBinding.inflate(layoutInflater)
        bindingReasonsForReporting = BottomDialogReasonsForReportingBinding.inflate(layoutInflater)

        viewModel.savePosts.observe(this) { savePostList ->
            val postId = post?.idPost ?: 0
            val isPostSaved =
                savePostList.any { it.savePost?.idPost == postId }
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

            bindingMenu?.btnReportPost?.setOnSingClickListener {
                viewModel.informationUser.observe(this) {
                    bindingReportPost?.tvBlock?.text = "Chặn trang cá nhân của ${it.nameUser}"
                    bindingReportPost?.tvHideAll?.text = "Ẩn tất cả từ ${it.nameUser}"
                }

                dialog?.show()
                val reportPost = Report().apply {
                    id = System.currentTimeMillis()
                    userId = post?.userIdPost
                    postId = post?.idPost
                    content = "${post?.contentPost} and ${post?.srcPost}"
                    reason = "Bài đăng này có vấn đề"
                    timestamp = System.currentTimeMillis()
                }
                viewModel.insertReport(reportPost)

                // Hoan tac bao cao
                bindingReportPost?.btnUndoNewspaper?.setOnSingClickListener {
                    viewModel.getAllReportPost(
                        reportPost.id ?: 0,
                        post?.idPost ?: 0,
                        post?.userIdPost ?: 0
                    )
                    viewModel.reportPost.observe(this) {
                        viewModel.deleteReport(it)
                    }

                    viewModel.informationUser.observe(this) {
                        bindingUnDoNewspaper?.tvBlock?.text =
                            "Chặn trang cá nhân của ${it.nameUser}"
                        bindingUnDoNewspaper?.tvHideAll?.text = "Ẩn tất cả từ ${it.nameUser}"
                    }
                    dialog?.show()

                    dialog?.setContentView(bindingUnDoNewspaper?.root!!)
                }

                // reason report
                bindingReportPost?.btnMoreReason?.setOnSingClickListener {
                    dialog?.show()

                    //back
                    bindingReasonReport?.btnBack?.setOnSingClickListener {
                        dialog?.show()

                        dialog?.setContentView(bindingReportPost?.root!!)
                    }

                    // nude
                    bindingReasonReport?.btnNudePhoto?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Ảnh khỏa thân"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Họat động tình dục" +
                                    "\n\n• Bán hoặc mua dâm" +
                                    "\n\n• Nhũ hoa (trừ trường hợp đang cho con bú, liên quan đến sử khỏa và hành động phản đối)" +
                                    "\n\n• Ảnh khỏa thân hiển thị bộ phận sinh dục" +
                                    "\n\n• Ngôn ngữ khiêu dâm"
                        dialog?.show()
                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    //Violence
                    bindingReasonReport?.btnViolence?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Bạo lực"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Đe dọa sử dụng bạo lực \n\t Ví dụ : nhắm mục tiêu một người và nhắc để vũ khí cụ thể" +
                                    "\n\n• Cá nhân hoặc tổ chức nguy hiểm \n\t  Ví dụ: Chủ nghĩa khủng bố hoặc một tổ chức tội phạm" +
                                    "\n\n• Hình ảnh cực kỳ bạo lực \n\t  Ví dụ: tôn vinh bạo lực hoặc tán dương sự đau khổ" +
                                    "\n\n• Một loại bạo lực khác \n\t  Ví dụ: Hình ảnh hoặc nội dung khác gây khó chịu"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }

                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnIllegalSales?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Chất cấm, chất gây nghiện"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi không cho phép:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Mua bán đồ uống có cồn, thuốc lá, cần sa hoặc thuốc để điều trị" +
                                    "\n\n  • Mua hoặc bán các loại thuốc không phải để điều trị (ví dụ: cocaine hoặc heroin)" +
                                    "\n\n  • Sử dụng, chế tạo hoặc quảng cáo thuốc không phải điều trị" +
                                    "\n\nChúng tôi cho phép:" +
                                    "\n\n  • Sử dụng đồ uổng có cồn, thuốc lá hoặc cần sa" +
                                    "\n\n  • Thảo luận về cách phục hồi trong trường hợp nghiện hoặc lạm dụng thuốc"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Bán hàng trái phép"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnSpam?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Spam"
                        bindingReasonsForReporting?.tvContent?.text = "Chúng tôi cho phép:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Mua, bán hay tặng tài khoản, vai trò hoặc quyền" +
                                    "\n\n  • Khuyền khích mọi người tương tác với nội dung sai sự thật" +
                                    "\n\n  • Dùng liên kết gây hiểu nhầm đề chuyển mọi người từ Facebook đến nơi khác"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnTrouble?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Nội dung này có vi phạm Tiêu chuẩn cộng đồng của chúng tôi không?"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Các tiêu chuẩn của chúng tôi giải thích về những gì chúng tôi cho phép và không cho phép hiển thị trên Facebook. Với sự hỗ trợ của các chuyên gia, chúng tôi thường xuyên xem xét và cập nhật các tiêu chuẩn mình đề ra"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Quấy rối"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnTerrorism?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Khủng bố"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi chỉ gỡ nội dung vi phạm Tiêu chuẩn cộng đồng của mình.\nChúng tôi gỡ nội dung về mọi cá nhân hoặc nhóm phi chính phủ tham gia hay ủng hộ các hành vi bạo lực có kế hoạch phục vụ mục đích chính trị, tôn giáo hoặc lí tưởng"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnHateSpeech?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Ngôn từ gây thù ghét"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi chỉ gỡ nội dung trực tiếp công kích mọi người dựa trên một số đặc điểm được bảo vệ, chẳng hạn như:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Ngôn từ bạo lực hoặc xúc phạm nhân phẩm\nVí dụ: so sánh tất cả những người thuộc một chủng tộc nào đó với côn trùng hoặc động vật" +
                                    "\n\n • Lời lẽ hạ thấp, khinh miệt hoặc coi thường người khác\nVí dụ: gợi ý rằng tất cả những người thuộc giới tính nào đó là ghê tởm" +
                                    "\n\n • Từ ngữ phỉ báng\nVí dụ: dùng các định kiến rập khuôn có hại về chủng tộc để lăng mạ ai đó" +
                                    "\n\n • Lời kêu gọi bài xích hoặc cô lập\nVí dụ: nói rằng những người thuộc tôn giáo nào đó không được phép bỏ phiếu"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnMisinformation?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Nội dung này có vi phạm Tiêu chuẩn cộng đồng của chúng tôi không?"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Các tiêu chuẩn của chúng tôi giải thích về những gì chúng tôi cho phép và không cho phép hiển thị trên Facebook. Với sự hỗ trợ của các chuyên gia, chúng tôi thường xuyên xem xét và cập nhật các tiêu chuẩn mình đề ra"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Thông tin sai sự thật"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    dialog?.setContentView(bindingReasonReport?.root!!)
                }

                dialog?.setContentView(bindingReportPost?.root!!)
            }

            dialog?.setContentView(bindingMenu?.root!!)
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

