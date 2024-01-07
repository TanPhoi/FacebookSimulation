package com.phoint.facebooksimulation.ui.saveFavoritePosts

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemSaveFavoritePostBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SaveFavoritePostAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<SaveFavoritePostAdapter.SaveFavoritePostViewHolder>() {
    private var userList: List<User>? = null
    private var saveFavoritePost: List<SaveFavoritePost>? = null
    private var showAllItems = false

    inner class SaveFavoritePostViewHolder(val binding: ItemSaveFavoritePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, user: User?) {
            binding.post = post
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveFavoritePostViewHolder {
        val binding = ItemSaveFavoritePostBinding.inflate(LayoutInflater.from(parent.context))
        return SaveFavoritePostViewHolder(binding)
    }

    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    fun saveFavoritePost(favoritePost: List<SaveFavoritePost>) {
        saveFavoritePost = favoritePost
        notifyDataSetChanged()
    }

    fun showAllItems() {
        showAllItems = true
        notifyDataSetChanged()
    }

    fun showLimitedItems() {
        showAllItems = false
        notifyDataSetChanged()
    }

    private fun getFormattedDateTime(dateTime: Long?): String {
        dateTime?.let {
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - dateTime

            return when {
                elapsedTime < 60 * 1000 -> "Vừa lưu"
                elapsedTime < 60 * 60 * 1000 -> "Đã lưu ${elapsedTime / (60 * 1000)} phút trước"
                elapsedTime < 24 * 60 * 60 * 1000 -> "Đã lưu ${elapsedTime / (60 * 60 * 1000)} giờ trước"
                elapsedTime < 7 * 24 * 60 * 60 * 1000 -> "Đã lưu ${elapsedTime / (24 * 60 * 60 * 1000)} ngày trước"
                elapsedTime < 30 * 24 * 60 * 60 * 1000 -> "Đã lưu ${elapsedTime / (7 * 24 * 60 * 60 * 1000)} tuần trước"
                elapsedTime < 365 * 24 * 60 * 60 * 1000 -> "Đã lưu ${elapsedTime / (30 * 24 * 60 * 60 * 1000)} tháng trước"
                else -> "Đã lưu ${elapsedTime / (365 * 24 * 60 * 60 * 1000)} năm trước"
            }
        }
        return ""
    }

    var setOnItemClick: ((Post) -> Unit)? = null

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: SaveFavoritePostViewHolder, position: Int) {
        val post = postList[position]
        val user = userList?.find { it.idUser == post.userIdPost }
        val saveFavoritePost = saveFavoritePost?.find { it.postOwnerId == post.idPost }
        holder.bind(post, user)

        val formattedDateTime = getFormattedDateTime(saveFavoritePost?.timestamp)
        holder.binding.tvTime.text = formattedDateTime


        val drawableLeft = ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_dot)
        holder.binding.tvTime.setCompoundDrawablesWithIntrinsicBounds(
            drawableLeft,
            null,
            null,
            null
        )

        holder.binding.tvNameUserPost.text = "• Bài viết • ${user?.nameUser}"

        if (post.contentPost == null) {
            holder.binding.tvContentStatus.text = "1 Ảnh"
        } else {
            holder.binding.tvContentStatus.text = "${post.contentPost}"
        }

        holder.itemView.setOnSingClickListener {
            setOnItemClick?.invoke(post)
        }
    }

    override fun getItemCount(): Int {
        return if (showAllItems) {
            postList.size
        } else {
            if (postList.size < 6) {
                postList.size
            } else {
                6
            }
        }
    }
}