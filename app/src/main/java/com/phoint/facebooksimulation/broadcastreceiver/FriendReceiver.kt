package com.phoint.facebooksimulation.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User

class FriendReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.MY_ACTION") {
            val friends = intent.getParcelableArrayListExtra<FriendRequest>("friends")
            val users = intent.getParcelableArrayListExtra<User>("users")

            val friendList = mutableListOf<FriendRequest>()
            val userList = mutableListOf<User>()
            if (friends != null && users != null) {
                for (friend in friends) {
                    if (friend.isNotification == false && friend.status == 0) {
                        friendList.add(friend)
                    }
                }
                for (user in users) {
                    userList.add(user)
                }

                val serviceIntent = Intent("com.example.SERVICE_ACTION")
                serviceIntent.putParcelableArrayListExtra("friends", ArrayList(friendList))
                serviceIntent.putParcelableArrayListExtra("users", ArrayList(userList))
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(serviceIntent)
            }
        }

        if (intent?.action == "com.example.ACTION_FRIEND_NOTIFICATION") {
            val friendRequest = intent.getParcelableExtra<FriendRequest>("friendRequest")
            if (friendRequest != null) {
                Log.e("friend", friendRequest?.isNotification.toString())
                val updateIntent = Intent("com.example.ACTION_UPDATE_FRIEND_NOTIFICATION")
                updateIntent.putExtra("friendRequest", friendRequest)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(updateIntent)
            }
        }

        if (intent?.action == "com.example.ACTION_FRIEND_ACCEPT") {
            val friendRequest = intent.getParcelableExtra<FriendRequest>("friendRequest")
            if (friendRequest != null) {
                Log.e("friend", friendRequest?.status.toString())
                val updateIntent = Intent("com.example.ACTION_UPDATE_FRIEND_REQUEST")
                updateIntent.putExtra("friendRequest", friendRequest)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(updateIntent)
            }
        }

        if (intent?.action == "com.example.ACTION_FRIEND_DELETE") {
            val friendRequest = intent.getParcelableExtra<FriendRequest>("friendRequest")
            if (friendRequest != null) {
                Log.e("friend", friendRequest?.status.toString())
                val deleteIntent = Intent("com.example.ACTION_DELETE_FRIEND_REQUEST")
                deleteIntent.putExtra("friendRequest", friendRequest)
                LocalBroadcastManager.getInstance(context!!).sendBroadcast(deleteIntent)
            }
        }
    }
}