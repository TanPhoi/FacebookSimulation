package com.phoint.facebooksimulation.ui.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemInvitationFriendBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class InvitationFriendAdapter(
    private var userList: ArrayList<User>,
    private var currentUserId: Long
) :
    RecyclerView.Adapter<InvitationFriendAdapter.InvitationFriendViewHolder>() {
    private var isFriend: Boolean = false
    private var receiverId: Long? = null
    private var friendRequestList: List<FriendRequest>? = null

    inner class InvitationFriendViewHolder(val binding: ItemInvitationFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, friendRequest: FriendRequest?) {
            binding.user = user
            binding.friendRequest = friendRequest
            binding.executePendingBindings()
        }
    }

    fun setFriend(friends: List<FriendRequest>) {
        friendRequestList = friends
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

    fun isFriendBoolean(user: User, b: Boolean) {
        val position = userList.indexOf(user)
        if (position != -1) {
            isFriend = b
            notifyItemChanged(position)
        }
    }

    fun setFriendList(user: List<User>) {
        userList = user as ArrayList<User>
        notifyDataSetChanged()
    }

    fun setCurrentUserId(receiverId: Long) {
        this.receiverId = receiverId
        notifyDataSetChanged()
    }

    fun deleteItem(user: User) {
        val position = userList.indexOf(user)
        if (position != -1) {
            userList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationFriendViewHolder {
        val binding =
            ItemInvitationFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvitationFriendViewHolder(binding)
    }

    var onClickFriendConfirmation: ((User) -> Unit)? = null
    var onItemClick: ((User) -> Unit)? = null
    var onClickDeleteInvitationFriend: ((User) -> Unit)? = null
    override fun onBindViewHolder(holder: InvitationFriendViewHolder, position: Int) {
        val user = userList[position]
        val friend = friendRequestList?.find { it.receiverId == currentUserId }
        holder.bind(user, friend)

        val formattedDateTime = getFormattedDateTime(friend?.time)
        holder.binding.tvTime.text = formattedDateTime

        holder.binding.btnAddFriend.setOnSingClickListener {
            onClickFriendConfirmation?.invoke(user)
        }

        holder.binding.btnDelete.setOnSingClickListener {
            onClickDeleteInvitationFriend?.invoke(user)
        }

        holder.itemView.setOnSingClickListener {
            onItemClick?.invoke(user)
        }

        if (isFriend) {
            holder.binding.tvNotification.visibility = View.VISIBLE
            holder.binding.tvHello.visibility = View.VISIBLE
            holder.binding.layoutButton.visibility = View.GONE
            holder.binding.tvNotification.text = "Bạn đã đồng ý kết bạn"
        } else {
            holder.binding.tvNotification.visibility = View.GONE
            holder.binding.tvHello.visibility = View.GONE
            holder.binding.layoutButton.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = if (userList.isNotEmpty()) userList.size else 0

}