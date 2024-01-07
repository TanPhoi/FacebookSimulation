package com.phoint.facebooksimulation.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.ViewCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.broadcastreceiver.FriendReceiver
import com.phoint.facebooksimulation.broadcastreceiver.NotificationReceiver
import com.phoint.facebooksimulation.databinding.ActivityMainBinding
import com.phoint.facebooksimulation.service.FriendService
import com.phoint.facebooksimulation.service.NotificationService
import com.phoint.facebooksimulation.ui.base.BaseActivity


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val friendReceiver = FriendReceiver()
    private val notificationReceiver = NotificationReceiver()
    override fun layoutRes(): Int {
        return R.layout.activity_main
    }

    override fun viewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(friendReceiver, IntentFilter("com.example.MY_ACTION"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(friendReceiver, IntentFilter("com.example.ACTION_FRIEND_DELETE"))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(friendReceiver, IntentFilter("com.example.ACTION_FRIEND_ACCEPT"))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            friendReceiver,
            IntentFilter("com.example.ACTION_FRIEND_NOTIFICATION")
        )

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(notificationReceiver, IntentFilter("com.example.PUT_NOTIFICATION"))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            notificationReceiver,
            IntentFilter("com.example.SERVICE_RECEIVE_NOTIFICATION")
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
            if (windowInsetsController != null) {
                windowInsetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                windowInsetsController.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.isAppearanceLightStatusBars = true
                windowInsetsController.isAppearanceLightNavigationBars = true
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun initView() {
        val serviceIntent = Intent(this, FriendService::class.java)
        startService(serviceIntent)

        val serviceNotificationIntent = Intent(this, NotificationService::class.java)
        startService(serviceNotificationIntent)
    }
}

