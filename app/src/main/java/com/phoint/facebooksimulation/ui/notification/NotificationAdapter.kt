package com.phoint.facebooksimulation.ui.notification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemNotificationBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class NotificationAdapter(private val notificationList: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var userList: List<User>? = null

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification, user: User?) {
            binding.notification = notification
            binding.user = user
        }
    }

    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        notificationList as ArrayList<Notification>
        notificationList.removeAt(position)
        notifyItemRemoved(position)
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

    private fun getUserForPost(notification: Notification): User? {
        return userList?.find { it.idUser == notification.senderId }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    var onItemClick: ((Notification) -> Unit)? = null
    var onClickMenu: ((Notification) -> Unit)? = null
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        val user = getUserForPost(notification)
        holder.bind(notification, user)

        notification.profilePicture.let {
            if (it != null) {
                holder.binding.imgIcon.setImageResource(it)
            }
        }

        val formattedDateTime = getFormattedDateTime(notification.timestamp)
        holder.binding.tvTimestamp.text = formattedDateTime

        holder.itemView.setOnSingClickListener {
            // Change the viewed status immediately
            notification.viewed = true
            // Notify the adapter of the data change
            notifyDataSetChanged()
            onItemClick?.invoke(notification)
        }

        holder.binding.btnMenu.setOnSingClickListener {
            onClickMenu?.invoke(notification)
        }

        val backgroundColor = if (notification.viewed == true) {
            Color.parseColor("#1B1B1B") // Change this to the desired color for unviewed notifications
        } else {
            Color.parseColor("#ED022745") // Change this to the desired color for viewed notifications
        }
        holder.binding.rlBackground.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount(): Int =
        if (notificationList.isNotEmpty()) notificationList.size else 0
}