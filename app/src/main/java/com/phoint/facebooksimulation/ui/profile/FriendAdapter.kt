package com.phoint.facebooksimulation.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemFriendBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class FriendAdapter(private var friendList: List<User>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    private var currentUserId: Long? = null

    inner class FriendViewHolder(val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
        }
    }

    fun setFriendList(newFriendList: List<User>) {
        friendList = newFriendList
        notifyDataSetChanged()
    }

    fun setCurrentUserId(userId: Long) {
        currentUserId = userId
        notifyDataSetChanged() // Cập nhật danh sách sau khi thiết lập ID người dùng
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    var onClickItem: ((User) -> Unit)? = null
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendRequest = friendList[position]
        holder.bind(friendRequest)

        holder.itemView.setOnSingClickListener {
            onClickItem?.invoke(friendRequest)
        }
    }

    override fun getItemCount(): Int {
        return if (friendList.size > 6) 6 else friendList.size
    }
}