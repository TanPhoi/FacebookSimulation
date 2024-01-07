package com.phoint.facebooksimulation.ui.userNavigation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentUserNavigationBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class UserNavigationFragment :
    BaseFragment<FragmentUserNavigationBinding, UserNavigationViewModel>() {
    private lateinit var receiverAddFriend: BroadcastReceiver
    private lateinit var receiverDeleteFriend: BroadcastReceiver
    private lateinit var receiverIsNotificationFriend: BroadcastReceiver
    private lateinit var receiverIsNotification: BroadcastReceiver
    val friendList = mutableListOf<FriendRequest>()
    val userList = mutableListOf<User>()
    private var adapter: ViewPageAdapter? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_user_navigation
    }

    override fun viewModelClass(): Class<UserNavigationViewModel> {
        return UserNavigationViewModel::class.java
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã cấp quyền truy cập ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã từ chối quyền truy cập",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun initView() {
        requestNotificationPermission()
        checkPermission()
        val fragmentManager = requireFragmentManager()
        val currentFragment = this
        val transaction = fragmentManager.beginTransaction()

        transaction.detach(currentFragment)
        transaction.attach(currentFragment)
        transaction.commit()

        val userId = viewModel.getIdUser()
        viewModel.getReceiverId(userId)
        val friendList = mutableListOf<FriendRequest>()
        val userList = mutableListOf<User>()

        viewModel.receiver.observe(this) { friendLists ->
            viewModel.currentUser.observe(this) { userLists ->
                for (friend in friendLists) {
                    friendList.add(friend)
                    for (user in userLists) {
                        if (friend.senderId == user.idUser && friend.status == 0 && friend.isNotification == false) {
                            userList.add(user)
                        }
                    }
                }
                val intent = Intent("com.example.MY_ACTION")
                intent.putParcelableArrayListExtra("friends", ArrayList(friendList))
                intent.putExtra("userId", userId)
                intent.putParcelableArrayListExtra("users", ArrayList(userList))
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
        }
        friendList.clear()
        userList.clear()

        val notificationList = mutableListOf<Notification>()
        viewModel.getAllNotificationById(userId, userId)
        viewModel.notificationList.observe(this) { notifications ->
            viewModel.currentUser.observe(this) { users ->
                for (notification in notifications) {
                    if (notification.isNotification == false) {
                        notificationList.add(notification)
                    }
                }
                val intent = Intent("com.example.PUT_NOTIFICATION")
                intent.putParcelableArrayListExtra("notifications", ArrayList(notificationList))
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
        }
        notificationList.clear()

        handleBottomNavigation()

        receiverAddFriend = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedFriendRequest =
                    intent?.getParcelableExtra<FriendRequest>("friendRequest")

                if (receivedFriendRequest != null) {
                    viewModel.updateFriendRequest(receivedFriendRequest)
                }
            }
        }
        val filter = IntentFilter("com.example.ACTION_UPDATE_FRIEND_REQUEST")
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiverAddFriend, filter)

        receiverDeleteFriend = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedFriendRequest =
                    intent?.getParcelableExtra<FriendRequest>("friendRequest")
                if (receivedFriendRequest != null) {
                    viewModel.deleteFriendRequest(receivedFriendRequest)
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                receiverDeleteFriend,
                IntentFilter("com.example.ACTION_FRIEND_DELETE")
            )

        receiverIsNotificationFriend = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedFriendRequest =
                    intent?.getParcelableExtra<FriendRequest>("friendRequest")
                if (receivedFriendRequest != null) {
                    viewModel.updateFriendRequest(receivedFriendRequest)
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            receiverIsNotificationFriend,
            IntentFilter("com.example.ACTION_UPDATE_FRIEND_NOTIFICATION")
        )

        receiverIsNotification = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val receivedNotificationRequest =
                    intent?.getParcelableExtra<Notification>("notification")
                if (receivedNotificationRequest != null) {
                    viewModel.updateNotification(receivedNotificationRequest)
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            receiverIsNotification,
            IntentFilter("com.example.ACTION_UPDATE_NOTIFICATION")
        )
    }

    private fun requestNotificationPermission() {
        val notificationPermission = Manifest.permission.RECEIVE_BOOT_COMPLETED
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                notificationPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            requestPermissionLauncher.launch(notificationPermission)
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun handleBottomNavigation() {
        adapter = ViewPageAdapter(requireActivity())
        binding.vpData.adapter = adapter
        binding.vpData.isUserInputEnabled = false

        binding.nvbFacebook.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.mnuHome -> binding.vpData.currentItem = 0
                R.id.mnuFriend -> binding.vpData.currentItem = 1
                R.id.mnuNotification -> binding.vpData.currentItem = 2
                R.id.mnuMenu -> binding.vpData.currentItem = 3
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverAddFriend)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiverDeleteFriend)
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(receiverIsNotification)
    }
}
