package com.phoint.facebooksimulation.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.data.local.model.Notification

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.PUT_NOTIFICATION") {
            val notifications = intent.getParcelableArrayListExtra<Notification>("notifications")

            val notificationList = mutableListOf<Notification>()
            if (notifications != null) {
                for (notification in notifications) {
                    notificationList.add(notification)
                }

                val serviceIntent = Intent("com.example.SERVICE_PUT_NOTIFICATION")
                serviceIntent.putParcelableArrayListExtra(
                    "notifications",
                    ArrayList(notificationList)
                )
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(serviceIntent)
            }
        }


        if (intent?.action == "com.example.SERVICE_RECEIVE_NOTIFICATION") {
            val notifications = intent.getParcelableExtra<Notification>("notification")
            if (notifications != null) {
                Log.d(
                    "SERVICE",
                    "Broadcast :  ${notifications.notificationId} _ ${notifications.isNotification}"
                )
                val updateIntent = Intent("com.example.ACTION_UPDATE_NOTIFICATION")
                updateIntent.putExtra("notification", notifications)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(updateIntent)
            }
        }
    }
}