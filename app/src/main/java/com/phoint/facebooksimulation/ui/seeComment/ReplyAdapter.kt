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
import com.phoint.facebooksimulation.databinding.ItemReplyCommentBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ReplyAdapter(var replies: List<Reply>, private val currentUserId: Long) :
    RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {
    private var userList: List<User>? = null
    private var postList: List<Post>? = null
    private var showAllReply = false
    var commentList: List<Comment>? = null

    inner class ReplyViewHolder(var binding: ItemReplyCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply, user: User?, post: Post?, comment: Comment?) {
            binding.reply = reply
            binding.user = user
        }
    }

    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    fun setPosts(posts: List<Post>) {
        postList = posts
        notifyDataSetChanged()
    }

    fun isHideByCurrentUser(reply: Reply): Boolean {
        val currentUser = currentUserId
        return reply.hiddenUsers.any { it.idUser == currentUser }
    }

    fun isLikedByCurrentUser(reply: Reply): Boolean {
        val currentUser = currentUserId
        return reply.likes.any { it.likeUserId == currentUser }
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

    var onClickReply: ((Reply) -> Unit)? = null
    var onClickDeleteReply: ((Reply, Int) -> Unit)? = null
    var onClickLikeReply: ((Reply) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val binding =
            ItemReplyCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        val user = userList?.find { it.idUser == reply.userIdComment }
        val post = postList?.find { it.idPost == reply.postIdComment }
        val comment = commentList?.find { it.idComment == reply.commentId }
        holder.bind(reply, user, post, comment)
        val formattedDateTime = getFormattedDateTime(reply.timestamp)
        holder.binding.tvTime.text = formattedDateTime

//        val nameUser = user?.nameUser
//        val contentReply = reply.contentReply
//
//        val spannableString = SpannableString("$nameUser $contentReply")
//        spannableString.setSpan(
//            ForegroundColorSpan(Color.parseColor("#3FA6F8")), // Màu xanh dương
//            0, // Vị trí bắt đầu của phần nameUser
//            nameUser?.length ?: 0, // Vị trí kết thúc của phần nameUser
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Cờ chỉnh sửa không được chia sẻ
//        )
//
//        holder.binding.tvReply.text = spannableString

        reply.imageReply.let {
            try {
                holder.binding.imgReply.setImageResource(it?.toInt()!!)
            } catch (ex: Exception) {
                Glide.with(holder.itemView.context).load(it).into(holder.binding.imgReply)
            }
        }

        if (reply.imageReply != null) {
            holder.binding.imgReply.visibility = View.VISIBLE
        } else {
            holder.binding.imgReply.visibility = View.GONE
        }

        holder.binding.tvContentReply.text = "${reply.contentReply}"

        if (post?.userIdPost == user?.idUser) {
            holder.binding.tvAuthor.visibility = View.VISIBLE
        } else {
            holder.binding.tvAuthor.visibility = View.GONE
        }

        val likes = reply.likes.map { it.likeUserId }
        //val currentUserId = currentUserId
        //val isCurrentUserLiked = likes.contains(currentUserId)
        val likeCount = likes.size
        val isLikedByCurrentUser = isLikedByCurrentUser(reply)

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

        val isHideByCurrentUser = isHideByCurrentUser(reply)
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
            holder.binding.tvContentReply.text = "Đã ẩn bình luận này."
            holder.binding.tvContentReply.setTextColor(alphaColorWithAlpha)
            holder.binding.tvName.setTextColor(alphaColorWithAlpha)
            holder.binding.tvTime.setTextColor(alphaColorWithAlpha)
            holder.binding.tvAuthor.setTextColor(alphaColorWithAlpha)
            holder.binding.tvSumLike.setTextColor(alphaColorWithAlpha)
            holder.binding.btnLike.visibility = View.GONE
            holder.binding.btnReply.visibility = View.GONE
            holder.binding.imgUserOther.alpha = alphaValue
            holder.binding.imgLike.alpha = alphaValue
        } else {
            val alphaValue = 1.0f // Giá trị alpha tùy chọn, ví dụ 0.5f cho mờ mờ
            val colorCode = "#CACACA" // Mã màu sắc tùy chọn, ví dụ "#636363"
            val alphaColor = Color.parseColor(colorCode)
            val alphaColorWithAlpha =
                ColorUtils.setAlphaComponent(alphaColor, (255 * alphaValue).toInt())

            holder.binding.tvContentReply.text = "${reply.contentReply}"
            holder.binding.tvContentReply.setTextColor(alphaColorWithAlpha)
            holder.binding.tvName.setTextColor(alphaColorWithAlpha)
            holder.binding.tvTime.setTextColor(alphaColorWithAlpha)
            holder.binding.tvAuthor.setTextColor(alphaColorWithAlpha)
            holder.binding.tvSumLike.setTextColor(alphaColorWithAlpha)
            holder.binding.imgUserOther.alpha = alphaValue
            holder.binding.imgLike.alpha = alphaValue
            holder.binding.btnLike.visibility = View.VISIBLE
            holder.binding.btnReply.visibility = View.VISIBLE
        }

        holder.binding.btnReply.setOnSingClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClickReply?.invoke(reply)
                notifyDataSetChanged()
            }
        }

        holder.itemView.setOnLongClickListener {
            val positions = holder.adapterPosition
            if (positions != RecyclerView.NO_POSITION) {
                onClickDeleteReply?.invoke(reply, position)
                notifyDataSetChanged()
            }
            return@setOnLongClickListener true
        }

        holder.binding.btnLike.setOnSingClickListener {
            val positions = holder.adapterPosition
            if (positions != RecyclerView.NO_POSITION) {
                onClickLikeReply?.invoke(reply)
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int {
//        return if (showAllReply) {
//            replies.size
//        } else {
//            if (replies.isNotEmpty()) 1 else 0
//        }
        return if (replies.isNotEmpty()) replies.size else 0
    }
}
