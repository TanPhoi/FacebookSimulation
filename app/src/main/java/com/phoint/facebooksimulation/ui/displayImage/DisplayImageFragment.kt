package com.phoint.facebooksimulation.ui.displayImage

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.databinding.FragmentDisplayImageBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class DisplayImageFragment : BaseFragment<FragmentDisplayImageBinding, DisplayImageViewModel>() {
    private var post: Post? = null
    private var isLiked = false
    private val PUBLIC = "Công khai"
    private val FRIENDS = "Bạn bè"
    private val PRIVATE = "Chỉ mình tôi"

    override fun layoutRes(): Int {
        return R.layout.fragment_display_image
    }

    override fun viewModelClass(): Class<DisplayImageViewModel> {
        return DisplayImageViewModel::class.java
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        post = arguments?.getParcelable("post") as? Post
        if (post?.sharedFromPostId != null) {
            viewModel.getPostById(post?.sharedFromPostId!!)
        }

        // Lưu trữ vị trí ban đầu của ImageView
        var initialX = binding.imgUser.x
        var initialY = binding.imgUser.y
        if (post != null) {
            val formattedDateTime = getFormattedDateTime(post?.dateTimePost)
            binding.tvDateTime.text = formattedDateTime

            binding.imgUser.adjustViewBounds = true
            if (post?.sharedFromPostId != null) {
                viewModel.isPostById.observe(this) {
                    Glide.with(requireContext()).load(it?.srcPost).into(binding.imgUser)
                }
            } else {
                Glide.with(requireContext()).load(post?.srcPost).into(binding.imgUser)
            }

            viewModel.getInformationUser(post?.userIdPost!!)
            viewModel.informationUser.observe(this) {
                if (it.coverImageUser != null && it.nameUser != null) {
                    binding.tvName.text = it.nameUser
                }
            }

            if (post?.contentPost != null) {
                binding.tvContent.visibility = View.VISIBLE
                binding.tvContent.text = post?.contentPost!!
            } else {
                binding.tvContent.visibility = View.GONE
            }

        }

        binding.imgUser.setOnTouchListener { v, event ->
            val extraDistance = 100 // Khoảng cách bổ sung
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Lưu trữ tọa độ chạm ban đầu
                    v.tag = event.rawX to event.rawY
                    initialX = v.x
                    initialY = v.y
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    // Lấy tọa độ chạm ban đầu từ tag
                    val (startX, startY) = v.tag as Pair<Float, Float>

                    // Tính toán khoảng cách di chuyển
                    val dx = event.rawX - startX
                    val dy = event.rawY - startY

                    // Cập nhật vị trí ImageView
                    v.x = initialX + dx
                    v.y = initialY + dy

                    // Giới hạn vị trí mới của ImageView
                    val actionBarHeight = binding.abDisplay.height ?: 0
                    val maxPosY = (binding.root.height - actionBarHeight - extraDistance).toFloat()
                    val minPosY = -v.height.toFloat()
                    v.y = v.y.coerceIn(minPosY, maxPosY)

                    binding.abDisplay.visibility = View.GONE
                    binding.relativeLayout.visibility = View.GONE

                    if (v.y >= maxPosY) {
                        findNavController().navigateUp()
                    }

                    true
                }

                MotionEvent.ACTION_UP -> {
                    // Quay trở lại vị trí ban đầu
                    v.animate()
                        .x(initialX)
                        .y(initialY)
                        .setDuration(300)
                        .withEndAction {
                            // Hiển thị lại ActionBar và phần layout
                            binding.abDisplay.visibility = View.VISIBLE
                            binding.relativeLayout.visibility = View.VISIBLE
                        }
                        .start()
                    true
                }

                else -> false
            }
        }

        binding.abDisplay.setExitCurrentScreen {
            findNavController().popBackStack()
        }

        handleClickLikePost()
        handleLikePost()
        handleClickComment()
        getComments()
        handlePermission()
    }

    private fun handlePermission() {
        when (post?.privacyPost) {
            PUBLIC -> {
                binding.imgPermission.setImageResource(R.drawable.ic_permission_public)
            }

            FRIENDS -> {
                binding.imgPermission.setImageResource(R.drawable.ic_permission_friend)
            }

            PRIVATE -> {
                binding.imgPermission.setImageResource(R.drawable.ic_permission_padlock)
            }

            else -> {
                binding.imgPermission.setImageResource(R.drawable.ic_permission_friend_except)
            }
        }
    }

    private fun getComments() {
        val comments = post?.comments
        var totalComments = 0
        comments?.forEach { comment ->
            totalComments++
            totalComments += comment.replies.size
        }

        if (totalComments > 0) {
            binding.tvShowComment.text = "$totalComments bình luận"
            binding.tvShowComment.visibility = View.VISIBLE
        } else {
            binding.tvShowComment.visibility = View.GONE
        }
    }

    private fun handleClickComment() {
        binding.btnComment.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_displayImageFragment_to_otherPeopleCommentFragment, bundleOf(
                    Pair("post", post)
                )
            )
        }
    }

    private fun handleClickLikePost() {
        val currentIdUser = viewModel.getIdPreferences()
        viewModel.getInformationUser(currentIdUser)
        binding.btnLike.setOnSingClickListener {
            viewModel.informationUser.observe(this) {
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
                        senderId = viewModel.getIdPreferences()
                        postId = post?.idPost
                        notificationType = "${it.nameUser} thích ảnh của bạn: ${post?.contentPost}"
                        profilePicture = R.drawable.ic_like_blue
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
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

                    viewModel.getNotificationById(currentIdUser, post?.userIdPost!!, post?.idPost!!)
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
        val currentIdUser = viewModel.getIdPreferences()
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
