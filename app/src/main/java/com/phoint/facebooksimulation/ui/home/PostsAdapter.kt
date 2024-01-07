package com.phoint.facebooksimulation.ui.home

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemAllPostBinding
import com.phoint.facebooksimulation.databinding.ItemSharePostBinding
import com.phoint.facebooksimulation.databinding.ItemUpdateAvatarBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PostsAdapter(private var postList: ArrayList<Post>, private val currentUserId: Long) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_POST = 0
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_SHARE = 2
    private var userList: List<User>? = null
    private var postListShare: List<Post>? = null
    private var pinnedPosition: Int = 0
    private var likedByUsername = ""
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 10000L
    private val PUBLIC = "Công khai"
    private val FRIENDS = "Bạn bè"
    private val PRIVATE = "Chỉ mình tôi"
    private var lastPosition = -1

    inner class PostsViewHolder(val binding: ItemAllPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, user: User?) {
            binding.post = post
            binding.user = user
        }

        init {
//            startUpdatingTime()
            binding.imgStatus.adjustViewBounds = true
        }
    }

    inner class UpdateAvatarViewHolder(val binding: ItemUpdateAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binds(post: Post, user: User?) {
            binding.post = post
            binding.user = user
        }

        init {
            // startUpdatingTime()
            binding.imgStatus.adjustViewBounds = true
        }
    }

    inner class SharePostViewHolder(val binding: ItemSharePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindShare(post: Post, user: User?, userShare: User?, postShare: Post?) {
            binding.post = post
            binding.user = user
            binding.userShare = userShare
            binding.postShare = postShare
        }

        init {
            // startUpdatingTime()
            //binding.imgStatus.adjustViewBounds = true
        }
    }

//    private val timeUpdateRunnable = object : Runnable {
//        override fun run() {
//            notifyDataSetChanged()
//            handler.postDelayed(this, updateInterval)
//        }
//    }

//    private fun startUpdatingTime() {
//        handler.postDelayed(timeUpdateRunnable, updateInterval)
//    }
//
//    private fun stopUpdatingTime() {
//        handler.removeCallbacks(timeUpdateRunnable)
//    }

//    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView)
//        stopUpdatingTime()
//    }

    fun updateUserAvatar(userId: Long, newAvatar: String) {
        userList?.find { it.idUser == userId }?.avatarUser = newAvatar
        notifyDataSetChanged()
    }

    fun updatePostList(newPostList: ArrayList<Post>) {
        postList = newPostList
        notifyDataSetChanged()
    }

    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    fun setPostShares(posts: List<Post>) {
        postListShare = posts
        notifyDataSetChanged()
    }

    fun isLikedByCurrentUser(post: Post): Boolean {
        val currentUser = currentUserId
        return post.likes.any { it.likeUserId == currentUser }
    }

    fun removeItem(position: Int) {
        postList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removePost(post: Post) {
        val position = postList.indexOf(post)
        if (position != -1) {
            postList.removeAt(position)
            notifyItemRemoved(position)
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

    private fun getUserForPost(post: Post): User? {
        return userList?.find { it.idUser == post.userIdPost }
    }

    private fun getUserForPostShare(post: Post): User? {
        return userList?.find { it.idUser == post.userIdPostShare }
    }

    private fun getPostShare(post: Post): Post? {
        return postListShare?.find { it.idPost == post.sharedFromPostId }
    }
//    override fun getItemViewType(position: Int): Int {
//        if (position < postList.size) {
//            val post = postList[position]
//            if (!post.isAvatar) {
//                return VIEW_TYPE_POST
//            }
//        }
//        return VIEW_TYPE_USER
//    }

    override fun getItemViewType(position: Int): Int {
        if (position < postList.size) {
            val post = postList[position]
            if (!post.isAvatar) {
                return VIEW_TYPE_POST
            } else if (post.isShared) {
                return VIEW_TYPE_SHARE
            }
        }
        return VIEW_TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_POST -> {
                val binding =
                    ItemAllPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostsViewHolder(binding)
            }

            VIEW_TYPE_USER -> {
                val binding = ItemUpdateAvatarBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UpdateAvatarViewHolder(binding)
            }

            VIEW_TYPE_SHARE -> {
                val binding =
                    ItemSharePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SharePostViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    var onClickLike: ((Post) -> Unit)? = null
    var onClickItem: ((Post) -> Unit)? = null
    var onClickMenu: ((Post, Int) -> Unit)? = null
    var onClickShare: ((Post) -> Unit)? = null
    var onClickComment: ((Post) -> Unit)? = null
    var onClickDelete: ((Post) -> Unit)? = null
    var onClickProfileUser: ((Post) -> Unit)? = null
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (holder is PostsViewHolder) {
            val post = postList[position]
            val user = getUserForPost(post)
            holder.bind(post, user)

            val formattedDateTime = getFormattedDateTime(post.dateTimePost)
            holder.binding.tvTime.text = formattedDateTime

            if (post.srcPost != null) {
                holder.binding.imgStatus.visibility = View.VISIBLE
            } else {
                holder.binding.imgStatus.visibility = View.GONE
            }

            val comments = post.comments
            var totalComments = 0
            comments.forEach { comment ->
                totalComments++
                totalComments += comment.replies.size
            }

            if (totalComments > 0) {
                holder.binding.tvShowComment.text = "$totalComments bình luận"
                holder.binding.tvShowComment.visibility = View.VISIBLE
            } else {
                holder.binding.tvShowComment.visibility = View.GONE
            }

            // Kiểm tra trạng thái like của bài post
            val isLikedByCurrentUser = isLikedByCurrentUser(post)

            if (isLikedByCurrentUser) {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like_blue_tv)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue
                    )
                )
            } else {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey
                    )
                )
            }

            val likes = post.likes.map { it.likeUserId }
            val currentUserId = currentUserId
            val isCurrentUserLiked = likes.contains(currentUserId)
            val otherLikedUserIds = likes.filter { it != currentUserId }
            val likeCount = likes.size

            if (likeCount > 0) {
                holder.binding.imgShowLike.visibility = View.VISIBLE
            } else {
                holder.binding.imgShowLike.visibility = View.GONE
            }

            // Kiểm tra nếu số lượng like lớn hơn 2, nghĩa là có ít nhất 3 người đã thích bài viết.
            val likeText = if (likeCount > 3) {
                //Kiểm tra nếu người dùng hiện tại đã thích bài viết.
                if (isCurrentUserLiked) {
                    val firstLikeUser = otherLikedUserIds.firstOrNull()?.let { userId ->
                        userList?.find { it.idUser == userId }
                    }
                    val otherLikeCount = likeCount - 2
                    "Bạn và ${firstLikeUser?.nameUser} ${otherLikeCount} người khác"
                } else {
                    val otherLikeCount = likeCount - 1
                    "${otherLikeCount} người đã thích"
                }
            } else {
                if (isCurrentUserLiked) {
                    if (isCurrentUserLiked) {
                        if (likeCount == 1) {
                            "Bạn"
                        } else {
                            val otherLikeCount = likeCount - 1
                            "Bạn và ${otherLikeCount} người khác"
                        }
                    } else {
                        val otherLikedUsers = otherLikedUserIds.mapNotNull { userId ->
                            userList?.find { it.idUser == userId }?.nameUser
                        }
                        val likeUserText = otherLikedUsers.joinToString(", ")
                        "Bạn, ${likeUserText} và người khác"
                    }
                } else {
                    if (likeCount == 0) {
                        holder.binding.tvPeopleLike.visibility = View.GONE
                        ""
                    } else {
                        val otherLikeCount = likeCount
                        "${otherLikeCount}"
                    }
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            when (post.privacyPost) {
                PUBLIC -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_public)
                }

                FRIENDS -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend)
                }

                PRIVATE -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_padlock)
                }

                else -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend_except)
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            if (post.isPinned && position != 0) {
                pinnedPosition = position
                handler.postDelayed({
                    // Di chuyển post lên đầu danh sách
                    postList.removeAt(pinnedPosition)
                    postList.add(0, post)
                    // Cập nhật giao diện ngay lập tức
                    notifyItemMoved(pinnedPosition, 0)
                    holder.binding.tvPinPost.visibility = View.VISIBLE
                }, 100)
            }

            holder.binding.btnLike.setOnSingClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickLike?.invoke(post)
                }
            }

            holder.binding.imgStatus.setOnSingClickListener {
                onClickItem?.invoke(post)
            }

            holder.binding.btnMenuPost.setOnSingClickListener {
                onClickMenu?.invoke(post, position)
            }

            holder.binding.btnShare.setOnSingClickListener {
                onClickShare?.invoke(post)
            }

            holder.binding.btnComment.setOnSingClickListener {
                onClickComment?.invoke(post)
            }

            holder.binding.btnDeletePost.setOnSingClickListener {
                onClickDelete?.invoke(post)
            }

            holder.binding.imgUser.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }

            holder.binding.tvName.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }
        } else if (holder is UpdateAvatarViewHolder) {
            val post = postList[position]
            val user = getUserForPost(post)
            holder.binds(post, user)

            val formattedDateTime = getFormattedDateTime(post.dateTimePost)
            holder.binding.tvTime.text = formattedDateTime

            if (post.srcPost != null) {
                holder.binding.imgStatus.visibility = View.VISIBLE
            } else {
                holder.binding.imgStatus.visibility = View.GONE
            }

            val comments = post.comments
            var totalComments = 0
            comments.forEach { comment ->
                totalComments++
                totalComments += comment.replies.size
            }

            if (totalComments > 0) {
                holder.binding.tvShowComment.text = "$totalComments bình luận"
                holder.binding.tvShowComment.visibility = View.VISIBLE
            } else {
                holder.binding.tvShowComment.visibility = View.GONE
            }

            // Kiểm tra trạng thái like của bài post
            val isLikedByCurrentUser = isLikedByCurrentUser(post)

            if (isLikedByCurrentUser) {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like_blue_tv)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue
                    )
                )
            } else {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey
                    )
                )
            }

            val likes = post.likes.map { it.likeUserId }
            val currentUserId = currentUserId
            val isCurrentUserLiked = likes.contains(currentUserId)
            val otherLikedUserIds = likes.filter { it != currentUserId }
            val likeCount = likes.size

            if (likeCount > 0) {
                holder.binding.imgShowLike.visibility = View.VISIBLE
            } else {
                holder.binding.imgShowLike.visibility = View.GONE
            }

            // Kiểm tra nếu số lượng like lớn hơn 2, nghĩa là có ít nhất 3 người đã thích bài viết.
            val likeText = if (likeCount > 3) {
                //Kiểm tra nếu người dùng hiện tại đã thích bài viết.
                if (isCurrentUserLiked) {
                    val firstLikeUser = otherLikedUserIds.firstOrNull()?.let { userId ->
                        userList?.find { it.idUser == userId }
                    }
                    val otherLikeCount = likeCount - 2
                    "Bạn và ${firstLikeUser?.nameUser} ${otherLikeCount} người khác"
                } else {
                    val otherLikeCount = likeCount - 1
                    "${otherLikeCount} người đã thích"
                }
            } else {
                if (isCurrentUserLiked) {
                    if (isCurrentUserLiked) {
                        if (likeCount == 1) {
                            "Bạn"
                        } else {
                            val otherLikeCount = likeCount - 1
                            "Bạn và ${otherLikeCount} người khác"
                        }
                    } else {
                        val otherLikedUsers = otherLikedUserIds.mapNotNull { userId ->
                            userList?.find { it.idUser == userId }?.nameUser
                        }
                        val likeUserText = otherLikedUsers.joinToString(", ")
                        "Bạn, ${likeUserText} và người khác"
                    }
                } else {
                    if (likeCount == 0) {
                        holder.binding.tvPeopleLike.visibility = View.GONE
                        ""
                    } else {
                        val otherLikeCount = likeCount
                        "${otherLikeCount}"
                    }
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            when (post.privacyPost) {
                PUBLIC -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_public)
                }

                FRIENDS -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend)
                }

                PRIVATE -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_padlock)
                }

                else -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend_except)
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            if (post.isPinned && position != 0) {
                pinnedPosition = position
                handler.postDelayed({
                    // Di chuyển post lên đầu danh sách
                    postList.removeAt(pinnedPosition)
                    postList.add(0, post)
                    // Cập nhật giao diện ngay lập tức
                    notifyItemMoved(pinnedPosition, 0)
                    holder.binding.tvPinPost.visibility = View.VISIBLE
                }, 100)
            }

            holder.binding.btnLike.setOnSingClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickLike?.invoke(post)
                }
            }

            holder.itemView.setOnSingClickListener {
                onClickItem?.invoke(post)
            }

            holder.binding.btnMenuPost.setOnSingClickListener {
                onClickMenu?.invoke(post, position)
            }

            holder.binding.btnShare.setOnSingClickListener {
                onClickShare?.invoke(post)
            }

            holder.binding.btnComment.setOnSingClickListener {
                onClickComment?.invoke(post)
            }

            holder.binding.btnDeletePost.setOnSingClickListener {
                onClickDelete?.invoke(post)
            }

            holder.binding.imgUser.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }

            holder.binding.tvName.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }
        } else if (holder is SharePostViewHolder) {
            val post = postList[position]
            val user = getUserForPost(post)
            val userShare = getUserForPostShare(post)
            val postShare = getPostShare(post)
            holder.bindShare(post, user, userShare, postShare)

            val formattedDateTimeShare = getFormattedDateTime(postShare?.dateTimePost)
            holder.binding.tvTimeShare.text = formattedDateTimeShare

            val formattedDateTime = getFormattedDateTime(post.dateTimePost)
            holder.binding.tvTime.text = formattedDateTime

            if (postShare?.srcPost != null) {
                holder.binding.imgStatusShare.visibility = View.VISIBLE
            } else {
                holder.binding.imgStatusShare.visibility = View.GONE
            }

            val comments = post.comments
            var totalComments = 0
            comments.forEach { comment ->
                totalComments++
                totalComments += comment.replies.size
            }

            if (totalComments > 0) {
                holder.binding.tvShowComment.text = "$totalComments bình luận"
                holder.binding.tvShowComment.visibility = View.VISIBLE
            } else {
                holder.binding.tvShowComment.visibility = View.GONE
            }

            // Kiểm tra trạng thái like của bài post
            val isLikedByCurrentUser = isLikedByCurrentUser(post)

            if (isLikedByCurrentUser) {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like_blue_tv)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.blue
                    )
                )
            } else {
                holder.binding.imgLike.setImageResource(R.drawable.ic_like)
                holder.binding.tvLike.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey
                    )
                )
            }

            val likes = post.likes.map { it.likeUserId }
            val currentUserId = currentUserId
            val isCurrentUserLiked = likes.contains(currentUserId)
            val otherLikedUserIds = likes.filter { it != currentUserId }
            val likeCount = likes.size

            if (likeCount > 0) {
                holder.binding.imgShowLike.visibility = View.VISIBLE
            } else {
                holder.binding.imgShowLike.visibility = View.GONE
            }

            // Kiểm tra nếu số lượng like lớn hơn 2, nghĩa là có ít nhất 3 người đã thích bài viết.
            val likeText = if (likeCount > 3) {
                //Kiểm tra nếu người dùng hiện tại đã thích bài viết.
                if (isCurrentUserLiked) {
                    val firstLikeUser = otherLikedUserIds.firstOrNull()?.let { userId ->
                        userList?.find { it.idUser == userId }
                    }
                    val otherLikeCount = likeCount - 2
                    "Bạn và ${firstLikeUser?.nameUser} ${otherLikeCount} người khác"
                } else {
                    val otherLikeCount = likeCount - 1
                    "${otherLikeCount} người đã thích"
                }
            } else {
                if (isCurrentUserLiked) {
                    if (isCurrentUserLiked) {
                        if (likeCount == 1) {
                            "Bạn"
                        } else {
                            val otherLikeCount = likeCount - 1
                            "Bạn và ${otherLikeCount} người khác"
                        }
                    } else {
                        val otherLikedUsers = otherLikedUserIds.mapNotNull { userId ->
                            userList?.find { it.idUser == userId }?.nameUser
                        }
                        val likeUserText = otherLikedUsers.joinToString(", ")
                        "Bạn, ${likeUserText} và người khác"
                    }
                } else {
                    if (likeCount == 0) {
                        holder.binding.tvPeopleLike.visibility = View.GONE
                        ""
                    } else {
                        val otherLikeCount = likeCount
                        "${otherLikeCount}"
                    }
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            when (post.privacyPost) {
                PUBLIC -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_public)
                }

                FRIENDS -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend)
                }

                PRIVATE -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_padlock)
                }

                else -> {
                    holder.binding.imgPermission.setImageResource(R.drawable.ic_permission_friend_except)
                }
            }

            when (postShare?.privacyPost) {
                PUBLIC -> {
                    holder.binding.imgPermissionShare.setImageResource(R.drawable.ic_permission_public)
                }

                FRIENDS -> {
                    holder.binding.imgPermissionShare.setImageResource(R.drawable.ic_permission_friend)
                }

                PRIVATE -> {
                    holder.binding.imgPermissionShare.setImageResource(R.drawable.ic_permission_padlock)
                }

                else -> {
                    holder.binding.imgPermissionShare.setImageResource(R.drawable.ic_permission_friend_except)
                }
            }

            if (likeText.isNotEmpty()) {
                holder.binding.tvPeopleLike.visibility = View.VISIBLE
                holder.binding.tvPeopleLike.text = likeText
            } else {
                holder.binding.tvPeopleLike.visibility = View.GONE
            }

            if (post.isPinned && position != 0) {
                pinnedPosition = position
                handler.postDelayed({
                    // Di chuyển post lên đầu danh sách
                    postList.removeAt(pinnedPosition)
                    postList.add(0, post)
                    // Cập nhật giao diện ngay lập tức
                    notifyItemMoved(pinnedPosition, 0)
                    holder.binding.tvPinPost.visibility = View.VISIBLE
                }, 100)
            }

            holder.binding.btnLike.setOnSingClickListener {
                val position = holder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickLike?.invoke(post)
                }
            }

            holder.itemView.setOnSingClickListener {
                onClickItem?.invoke(post)
            }

            holder.binding.btnMenuPost.setOnSingClickListener {
                onClickMenu?.invoke(post, position)
            }

            holder.binding.btnShare.setOnSingClickListener {
                onClickShare?.invoke(post)
            }

            holder.binding.btnComment.setOnSingClickListener {
                onClickComment?.invoke(post)
            }

            holder.binding.btnDeletePost.setOnSingClickListener {
                onClickDelete?.invoke(post)
            }

            holder.binding.imgUser.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }

            holder.binding.tvName.setOnSingClickListener {
                onClickProfileUser?.invoke(post)
            }
        }
    }

    override fun getItemCount(): Int = if (postList.isNotEmpty()) postList.size else 0
}
