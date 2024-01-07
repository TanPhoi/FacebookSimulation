package com.phoint.facebooksimulation.ui.userOfFriend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemUserOfFriendBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class FriendOfUserAdapter(
    private var userList: ArrayList<User>,
    private var isFriendOfUser: Boolean
) : RecyclerView.Adapter<FriendOfUserAdapter.FriendOfUserViewHolder>() {
    private var currentUserId: Long? = null
    private var currentUser: User? = null
    private var friendRequestList: List<FriendRequest>? = null

    private val FRIEND_REQUEST_PENDING = 0
    private val FRIEND_REQUEST_ACCEPTED = 1
    private val FRIEND_REQUEST_REJECTED = 2

    inner class FriendOfUserViewHolder(val binding: ItemUserOfFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
        }
    }

    fun removeItem(user: User) {
        val position = userList.indexOf(user)
        if (position != -1) {
            userList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setFriendList(user: List<User>) {
        userList = user as ArrayList<User>
        notifyDataSetChanged()
    }

    fun setFriendRequestLists(friendRequest: List<FriendRequest>) {
        friendRequestList = friendRequest as ArrayList<FriendRequest>
        notifyDataSetChanged()
    }

    fun setCurrentUserId(userId: Long) {
        currentUserId = userId
        notifyDataSetChanged() // Cập nhật danh sách sau khi thiết lập ID người dùng
    }

    fun setCurrentUser(user: User) {
        currentUser = user
        notifyDataSetChanged() // Cập nhật danh sách sau khi thiết lập ID người dùng
    }

    fun getCommonFriendsCount(currentUser: User, user: User): Int {
        val acceptedRequests = friendRequestList?.filter { request ->
            request.status == FRIEND_REQUEST_ACCEPTED
        }

        val currentUserFriends = acceptedRequests?.filter { request ->
            request.senderId == currentUser.idUser || request.receiverId == currentUser.idUser
        }

        val userFriends = acceptedRequests?.filter { request ->
            request.senderId == user.idUser || request.receiverId == user.idUser
        }

        // Tạo một biến đếm để đếm số bạn bè chung
        var commonFriendsCount = -1

        // Duyệt qua danh sách bạn bè của người dùng hiện tại
        for (currentUserFriend in currentUserFriends!!) {
            // Duyệt qua danh sách bạn bè của người dùng khác
            for (userFriend in userFriends!!) {
                // Kiểm tra xem có sự trùng lặp trong danh sách bạn bè không
                if (currentUserFriend.senderId == userFriend.senderId || currentUserFriend.senderId == userFriend.receiverId ||
                    currentUserFriend.receiverId == userFriend.senderId || currentUserFriend.receiverId == userFriend.receiverId
                ) {
                    // Nếu có sự trùng lặp, tăng biến đếm lên
                    commonFriendsCount++
                }
            }
        }

        return commonFriendsCount
    }

    var onClickMenu: ((User) -> Unit)? = null
    var onClickItem: ((User) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendOfUserViewHolder {
        val binding =
            ItemUserOfFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendOfUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendOfUserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        if (isFriendOfUser) {
            holder.binding.btnChange.visibility = View.VISIBLE
        } else {
            holder.binding.btnChange.visibility = View.GONE
        }

        holder.binding.btnChange.setOnSingClickListener {
            onClickMenu?.invoke(user)
        }

        holder.itemView.setOnSingClickListener {
            onClickItem?.invoke(user)
        }

        val commonFriendsCount = getCommonFriendsCount(currentUser!!, user)
        if (commonFriendsCount == 0) {
            holder.binding.tvFriends.visibility = View.GONE
        } else {
            holder.binding.tvFriends.visibility = View.VISIBLE
            holder.binding.tvFriends.text = "$commonFriendsCount bạn chung"
        }
    }

    override fun getItemCount(): Int = if (userList.isNotEmpty()) userList.size else 0
}