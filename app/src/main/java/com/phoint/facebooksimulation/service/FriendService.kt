package com.phoint.facebooksimulation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.main.MainActivity

class FriendService : Service() {
    private val friendReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val friends = intent?.getParcelableArrayListExtra<FriendRequest>("friends")
            val users = intent?.getParcelableArrayListExtra<User>("users")
            val userId = intent?.getLongExtra("userId", 0)
            if (friends != null && users != null) {
                showNotificationForFriendRequests(friends, users)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter("com.example.SERVICE_ACTION")
        LocalBroadcastManager.getInstance(this).registerReceiver(friendReceiver, intentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            friendReceiver, IntentFilter("com.example.ACTION_FRIEND_ACCEPT")
        )
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(friendReceiver, IntentFilter("com.example.ACTION_FRIEND_DELETE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            friendReceiver,
            IntentFilter("com.example.ACTION_FRIEND_NOTIFICATION")
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val friendRequest = intent?.getParcelableExtra<FriendRequest>("friendRequest")

        val notificationId = friendRequest?.time?.toInt() ?: 0
        if (action == "accept" && friendRequest != null) {
            val broadcastIntent = Intent("com.example.ACTION_FRIEND_ACCEPT")
            friendRequest.status = 1
            broadcastIntent.putExtra("friendRequest", friendRequest)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        } else if (action == "cancel" && friendRequest != null) {
            val broadcastIntent = Intent("com.example.ACTION_FRIEND_DELETE")
            broadcastIntent.putExtra("friendRequest", friendRequest)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        return START_STICKY
    }

    private fun showNotificationForFriendRequests(friends: List<FriendRequest>, users: List<User>) {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val minSize = minOf(friends.size, users.size)
        for (i in 0 until minSize) {
            val friend = friends[i]
            val user = users[i]

            val broadcastIntent = Intent("com.example.ACTION_FRIEND_NOTIFICATION")
            friend.isNotification = true
            broadcastIntent.putExtra("friendRequest", friend)
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

            val notificationId = friend.time?.toInt() ?: 0

            val acceptIntent = Intent(this, FriendService::class.java).apply {
                action = "accept"
                putExtra("friendRequest", friend)
            }

            val acceptPendingIntent = PendingIntent.getService(
                this,
                notificationId,
                acceptIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val cancelIntent = Intent(this, FriendService::class.java).apply {
                action = "cancel"
                putExtra("friendRequest", friend)
            }
            val cancelPendingIntent = PendingIntent.getService(
                this,
                notificationId,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Facebook")
                .setContentText("${user.nameUser} gửi yêu cầu kết bạn")
                .setSmallIcon(R.drawable.ic_friend)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getActivityPendingIntent())
                .setDefaults(Notification.DEFAULT_SOUND)
                .addAction(R.drawable.ic_add_friend, "Đồng ý kết bạn", acceptPendingIntent)
                .addAction(R.drawable.ic_delete, "Hủy kết bạn", cancelPendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Yêu cầu kết bạn",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    private fun getActivityPendingIntent(): PendingIntent {
        val intent = Intent(
            this,
            MainActivity::class.java
        ) // Thay YourMainActivity bằng Activity chính của bạn
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}