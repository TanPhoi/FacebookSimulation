package com.phoint.facebooksimulation.ui.seeComment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemCommentBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class CommentAdapter(private var commentList: List<Comment>, private val currentUserId: Long) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private var userList: List<User>? = null
    private var postList: List<Post>? = null
    private var replyList: List<Reply>? = null
    private var replyAdapter: ReplyAdapter? = null

    inner class CommentViewHolder(var binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, user: User?, reply: Reply?) {
            binding.comment = comment
            binding.user = user
            replyAdapter = ReplyAdapter(comment.replies, currentUserId)
            binding.rcvReply.adapter = replyAdapter
            binding.rcvReply.isNestedScrollingEnabled = false
            userList?.let { replyAdapter!!.setUsers(it) }
            postList?.let { replyAdapter!!.setPosts(it) }
        }
    }

    fun isLikedByCurrentUser(comment: Comment): Boolean {
        val currentUser = currentUserId
        return comment.likes.any { it.likeUserId == currentUser }
    }

    fun isHideByCurrentUser(comment: Comment): Boolean {
        val currentUser = currentUserId
        return comment.hiddenUsers.any { it.idUser == currentUser }
    }


    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    fun setPosts(posts: List<Post>) {
        postList = posts
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    var onClickReplyComment: ((Comment) -> Unit)? = null
    var onClickDeleteComment: ((Comment, Int) -> Unit)? = null
    var onClickReply: ((Comment, Reply) -> Unit)? = null
    var onClickDeleteReply: ((Comment, Reply, Int) -> Unit)? = null
    var onClickLikeComment: ((Comment) -> Unit)? = null
    var onClickLikeReply: ((Comment, Reply) -> Unit)? = null
    var onClickProfileUser: ((Comment) -> Unit)? = null
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        val post = postList?.find { it.idPost == comment.postIdComment }
        val user = userList?.find { it.idUser == comment.userIdComment }
        val reply = replyList?.find { it.commentId == comment.idComment }

        holder.bind(comment, user, reply)
        val formattedDateTime = getFormattedDateTime(comment.timestamp)
        holder.binding.tvTime.text = formattedDateTime

        holder.binding.tvContentComment.text = "${comment.contentComment}"

        comment.imageComment.let {
            try {
                holder.binding.imgComment.setImageResource(it?.toInt()!!)
            } catch (ex: Exception) {
                Glide.with(holder.itemView.context).load(it).into(holder.binding.imgComment)
            }
        }

        if (post?.userIdPost == user?.idUser) {
            holder.binding.tvAuthor.visibility = View.VISIBLE
        } else {
            holder.binding.tvAuthor.visibility = View.GONE
        }

        if (comment.contentComment != null) {
            holder.binding.tvContentComment.visibility = View.VISIBLE
        } else {
            holder.binding.tvContentComment.visibility = View.GONE
        }

        if (comment.imageComment != null) {
            holder.binding.imgComment.visibility = View.VISIBLE
        } else {
            holder.binding.imgComment.visibility = View.GONE
        }

        val likes = comment.likes.map { it.likeUserId }
        //val currentUserId = currentUserId
        //val isCurrentUserLiked = likes.contains(currentUserId)
        val likeCount = likes.size
        val isLikedByCurrentUser = isLikedByCurrentUser(comment)

        if (likeCount > 0) {
            holder.binding.imgLike.visibility = View.VISIBLE
            holder.binding.tvSumLike.visibility = View.VISIBLE
            holder.binding.tvSumLike.text = likeCount.toString()
        } else {
            holder.binding.tvSumLike.visibility = View.GONE
        }

        if (isLikedByCurrentUser) {
            holder.binding.imgLike.visibility = View.VISIBLE
            holder.binding.btnLike.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.blue
                )
            )
        } else {
            if (likeCount <= 0) {
                holder.binding.imgLike.visibility = View.GONE
            } else {
                holder.binding.imgLike.visibility = View.VISIBLE
            }
            holder.binding.btnLike.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
        }

        val isHideByCurrentUser = isHideByCurrentUser(comment)
        if (isHideByCurrentUser) {
            val alphaValue = 0.5f // Giá trị alpha tùy chọn, ví dụ 0.5f cho mờ mờ
            val colorCode = "#636363" // Mã màu sắc tùy chọn, ví dụ "#636363"

            val alphaColor = Color.parseColor(colorCode)
            val alphaColorWithAlpha =
                ColorUtils.setAlphaComponent(alphaColor, (255 * alphaValue).toInt())
            val drawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_pen_min)
            drawable?.mutate()?.alpha = (255 * alphaValue).toInt()
            holder.binding.tvAuthor.setCompoundDrawablesWithIntrinsicBounds(
                drawable,
                null,
                null,
                null
            )
//            comment.contentComment = "Đã ẩn bình luận này."
            holder.binding.tvContentComment.text = "Đã ẩn bình luận này."
            holder.binding.tvContentComment.setTextColor(alphaColorWithAlpha)
            holder.binding.tvName.setTextColor(alphaColorWithAlpha)
            holder.binding.tvTime.setTextColor(alphaColorWithAlpha)
            holder.binding.tvAuthor.setTextColor(alphaColorWithAlpha)
            holder.binding.tvSumLike.setTextColor(alphaColorWithAlpha)
            holder.binding.btnLike.visibility = View.GONE
            holder.binding.btnReplyComment.visibility = View.GONE
            holder.binding.imgUserOther.alpha = alphaValue
            holder.binding.imgLike.alpha = alphaValue
        } else {
            val alphaValue = 1.0f // Giá trị alpha tùy chọn, ví dụ 0.5f cho mờ mờ
            val colorCode = "#CACACA" // Mã màu sắc tùy chọn, ví dụ "#636363"
            val alphaColor = Color.parseColor(colorCode)
            val alphaColorWithAlpha =
                ColorUtils.setAlphaComponent(alphaColor, (255 * alphaValue).toInt())
            holder.binding.tvContentComment.setTextColor(alphaColorWithAlpha)
            holder.binding.tvName.setTextColor(alphaColorWithAlpha)
            holder.binding.tvTime.setTextColor(alphaColorWithAlpha)
            holder.binding.tvAuthor.setTextColor(alphaColorWithAlpha)
            holder.binding.tvSumLike.setTextColor(alphaColorWithAlpha)
            holder.binding.tvContentComment.text = "${comment.contentComment}"
            holder.binding.imgUserOther.alpha = alphaValue
            holder.binding.imgLike.alpha = alphaValue
            holder.binding.btnLike.visibility = View.VISIBLE
            holder.binding.btnReplyComment.visibility = View.VISIBLE
        }

        holder.binding.btnLike.setOnSingClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClickLikeComment?.invoke(comment)
            }
        }

        holder.binding.btnReplyComment.setOnSingClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClickReplyComment?.invoke(comment)
            }
        }

        holder.itemView.setOnLongClickListener {
            val positions = holder.adapterPosition
            if (positions != RecyclerView.NO_POSITION) {
                onClickDeleteComment?.invoke(comment, position)
            }
            return@setOnLongClickListener true
        }

        replyAdapter?.onClickLikeReply = {
            if (comment.idComment == it.commentId) {
                onClickLikeReply?.invoke(comment, it)
            }
        }

        replyAdapter?.onClickReply = {
            if (comment.idComment == it.commentId) {
                onClickReply?.invoke(comment, it)
            }
        }

        replyAdapter?.onClickDeleteReply = { reply, positions ->
            if (comment.idComment == reply.commentId) {
                onClickDeleteReply?.invoke(comment, reply, positions)
            }
        }

//        holder.binding.btnReplyComment.setOnSingClickListener {
//            onClickReply?.invoke(comment.replies)
//        }

        holder.binding.imgUserOther.setOnSingClickListener {
            onClickProfileUser?.invoke(comment)
        }

//        holder.binding.tvName.setOnSingClickListener {
//            onClickProfileUser?.invoke(comment)
//        }
    }

    override fun getItemCount(): Int {
        return if (commentList.isNotEmpty()) {
            commentList.size
        } else {
            0
        }
    }
}
