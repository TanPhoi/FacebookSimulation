package com.phoint.facebooksimulation.ui.informationUserOther

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemInfomationOtherUserBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class InformationUserOtherAdapter(var userList: ArrayList<User>) :
    RecyclerView.Adapter<InformationUserOtherAdapter.InformationUserOtherViewHolder>() {
    private var friendRequestList: List<FriendRequest>? = null
    private var currentUserId: Long? = null
    private var isFriendDelete: Boolean = false

    inner class InformationUserOtherViewHolder(val binding: ItemInfomationOtherUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userOther = user
        }
    }

    fun isFriendDelete(boolean: Boolean) {
        isFriendDelete = boolean
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

    fun setData(newList: List<User>) {
        if (userList != newList) {
            val diffResult = DiffUtil.calculateDiff(UserDiffCallback(userList, newList))
            userList.clear()
            userList.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    class UserDiffCallback(private val oldList: ArrayList<User>, private val newList: List<User>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].idUser == newList[newItemPosition].idUser
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InformationUserOtherViewHolder {
        val binding = ItemInfomationOtherUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InformationUserOtherViewHolder(binding)
    }

    var onItemClick: ((User) -> Unit)? = null
    var onClickAddFriend: ((User) -> Unit)? = null
    var onClickCancelFriendRequest: ((User) -> Unit)? = null
    var onClickDeleteFriendRequest: ((User) -> Unit)? = null
    var onClickFeedback: ((User) -> Unit)? = null
    override fun onBindViewHolder(holder: InformationUserOtherViewHolder, position: Int) {
        val userOther = userList[position]
        holder.bind(userOther)

        val friendRequests = friendRequestList?.filter { friend ->
            (friend.senderId == currentUserId && friend.receiverId == userOther.idUser)
        }

        val friendRequestsUserOther = friendRequestList?.filter { friend ->
            (friend.senderId == userOther.idUser && friend.receiverId == currentUserId)
        }

        if (userOther.idUser == currentUserId) {
            holder.binding.btnInformation.visibility = View.VISIBLE
            holder.binding.btnAddFriend.visibility = View.GONE
        } else {
            holder.binding.btnAddFriend.visibility = View.VISIBLE
            holder.binding.btnInformation.visibility = View.GONE

            holder.binding.btnAddFriend.text = when {
                !friendRequests.isNullOrEmpty() -> {
                    val firstFriendRequest = friendRequests.first()
                    when (firstFriendRequest.status) {
                        0 -> "Hủy"
                        1 -> "Bạn bè"
                        else -> "Thêm bạn bè"
                    }
                }

                !friendRequestsUserOther.isNullOrEmpty() -> {
                    val firstFriendRequest = friendRequestsUserOther.first()
                    when (firstFriendRequest.status) {
                        0 -> "Phản hồi"
                        1 -> "Bạn bè"
                        else -> "Thêm bạn bè"
                    }
                }

                else -> {
                    "Thêm bạn bè"
                }
            }
        }


        holder.binding.tvMe.visibility = when {
            !friendRequests.isNullOrEmpty() -> {
                val firstFriendRequest = friendRequests.first()
                when (firstFriendRequest.status) {
                    0 -> View.GONE
                    1 -> View.VISIBLE
                    else -> View.GONE
                }
            }

            else -> {
                View.GONE
            }
        }

        if (userOther.workExperience != null) {
            userOther.workExperience?.nameWork.let {
                holder.binding.tvWork.text = "Làm việc tại $it"
            }
            holder.binding.tvWork.visibility = View.VISIBLE
        } else {
            holder.binding.tvWork.visibility = View.GONE
        }

        if (userOther.colleges != null) {
            userOther.colleges?.nameColleges.let {
                holder.binding.tvColleges.text = "$it"
            }
            holder.binding.tvColleges.visibility = View.VISIBLE
            holder.binding.linearLayout2.visibility = View.VISIBLE
        } else {
            holder.binding.tvColleges.visibility = View.GONE
        }

        if (userOther.highSchool != null) {
            userOther.highSchool?.nameHighSchool.let {
                holder.binding.tvHighSchool.text = "• $it"
            }
            holder.binding.tvHighSchool.visibility = View.VISIBLE
        } else {
            holder.binding.tvHighSchool.visibility = View.GONE
        }

        holder.itemView.setOnSingClickListener {
            onItemClick?.invoke(userOther)
        }

        holder.binding.btnAddFriend.setOnSingClickListener {
            when (holder.binding.btnAddFriend.text) {
                "Thêm bạn bè" -> {
                    onClickAddFriend?.invoke(userOther)
                    holder.binding.btnAddFriend.text = "Hủy"
                }

                "Hủy" -> {
                    onClickCancelFriendRequest?.invoke(userOther)
                    holder.binding.btnAddFriend.text = "Thêm bạn bè"
                }

                "Phản hồi" -> {
                    onClickFeedback?.invoke(userOther)
                }

                else -> {
                    onClickDeleteFriendRequest?.invoke(userOther)
                    if (isFriendDelete) {
                        holder.binding.btnAddFriend.text = "Thêm bạn bè"
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = if (userList.isNotEmpty()) userList.size else 0
}
