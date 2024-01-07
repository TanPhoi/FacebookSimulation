package com.phoint.facebooksimulation.service

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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.ui.main.MainActivity

class NotificationService : Service() {
    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val notifications = intent?.getParcelableArrayListExtra<Notification>("notifications")
            if (notifications != null) {
                showNotificationForFriendRequests(notifications)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filterPutNotification = IntentFilter("com.example.SERVICE_PUT_NOTIFICATION")
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(notificationReceiver, filterPutNotification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "onStartCommand is called")
        return START_STICKY
    }

    private fun showNotificationForFriendRequests(notifications: List<Notification>) {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val minSize = minOf(notifications.size)
        for (i in 0 until minSize) {
            val notification = notifications[i]
            val notificationId = notification.timestamp?.toInt() ?: 0
            if (notification.isNotification == true) {
                continue
            }

            val broadcastIntent = Intent("com.example.SERVICE_RECEIVE_NOTIFICATION")
            notification.isNotification = true
            broadcastIntent.putExtra("notification", notification)
            Log.d(
                "SERVICE",
                "service :  ${notification.notificationId} _ ${notification.isNotification}"
            )
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Facebook")
                .setContentText("${notification.notificationType}")
                .setSmallIcon(R.drawable.ic_notification_blue)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getActivityPendingIntent())
                .setDefaults(android.app.Notification.DEFAULT_SOUND)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Thông báo",
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
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}