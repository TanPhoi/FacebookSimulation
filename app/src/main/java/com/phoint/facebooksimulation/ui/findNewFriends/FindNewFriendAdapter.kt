package com.phoint.facebooksimulation.ui.findNewFriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemFindNewFriendBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class FindNewFriendAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<FindNewFriendAdapter.FindNewFriendViewHolder>() {
    private var currentUserId: Long? = null
    private val friendRequestStatusMaps = mutableMapOf<Long, Boolean>()
    // private val removedUserIds = mutableListOf<Long>()

    inner class FindNewFriendViewHolder(val binding: ItemFindNewFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindNewFriendViewHolder {
        val binding =
            ItemFindNewFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FindNewFriendViewHolder(binding)
    }

    fun updateFriendRequestStatus(userId: Long, isSent: Boolean) {
        friendRequestStatusMaps[userId] = isSent
        notifyDataSetChanged()
    }

    fun setCurrentUserId(userId: Long) {
        currentUserId = userId
        notifyDataSetChanged() // Cập nhật danh sách sau khi thiết lập ID người dùng
    }

    fun removeItem(user: User) {
        val position = userList.indexOf(user)
        if (position != -1) {
            userList as ArrayList
            userList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    var onItemClick: ((User) -> Unit)? = null
    var onClickAddFriend: ((User) -> Unit)? = null
    var onClickRemove: ((User) -> Unit)? = null
    var onClickRemoveFriend: ((User) -> Unit)? = null
    override fun onBindViewHolder(holder: FindNewFriendViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        holder.itemView.setOnSingClickListener {
            onItemClick?.invoke(user)
        }

        holder.binding.btnAddFriend.setOnSingClickListener {
            onClickAddFriend?.invoke(user)
        }

        holder.binding.btnRemove.setOnSingClickListener {
            if (holder.binding.btnRemove.text == "Hủy") {
                onClickRemoveFriend?.invoke(user)
            } else {
                onClickRemove?.invoke(user)
            }
        }

        val isFriendRequestSent = friendRequestStatusMaps[user.idUser] ?: false

        // Kiểm tra trạng thái của người bạn và cập nhật giao diện
        if (isFriendRequestSent) {
            holder.binding.btnAddFriend.visibility = View.GONE
            holder.binding.tvNotification.visibility = View.VISIBLE
            holder.binding.btnRemove.text = "Hủy"
        } else {
            holder.binding.btnAddFriend.visibility = View.VISIBLE
            holder.binding.tvNotification.visibility = View.GONE
            holder.binding.btnRemove.text = "Gỡ"
        }

        if (user.workExperience != null) {
            user.workExperience?.nameWork.let {
                holder.binding.tvWork.text = "Làm việc tại $it"
            }
            holder.binding.tvWork.visibility = View.VISIBLE
        } else {
            holder.binding.tvWork.visibility = View.GONE
        }

        if (user.colleges != null) {
            user.colleges?.nameColleges.let {
                holder.binding.tvColleges.text = "$it"
            }
            holder.binding.tvColleges.visibility = View.VISIBLE
            holder.binding.linearLayout2.visibility = View.VISIBLE
        } else {
            holder.binding.tvColleges.visibility = View.GONE
        }

        if (user.highSchool != null) {
            user.highSchool?.nameHighSchool.let {
                holder.binding.tvHighSchool.text = "• $it"
            }
            holder.binding.tvHighSchool.visibility = View.VISIBLE
        } else {
            holder.binding.tvHighSchool.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = if (userList.isNotEmpty()) userList.size else 0

}