package com.phoint.facebooksimulation.ui.userNavigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserNavigationViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var receiver = MutableLiveData<List<FriendRequest>>()
    var currentUser = MutableLiveData<List<User>>()
    var notificationList = MutableLiveData<List<Notification>>()

    init {
        getCurrentUserId()
    }

    fun getIdUser(): Long {
        return appPreferences.getId()
    }

    fun getAllNotificationById(userId: Long, currentUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = localRepository.getAllNotificationById(userId, currentUserId)
            if (notification != null) {
                notificationList.postValue(notification)
            }
        }
    }

    fun getReceiverId(receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val receiverId = localRepository.getAllReceiverId(receiverId)
            if (receiverId != null) {
                receiver.postValue(receiverId)
            }
        }
    }

    fun updateFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateFriendRequest(friendRequest)
        }
    }

    fun updateNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateNotification(notification)
        }
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteFriendRequest(friendRequest)
        }
    }

    private fun getCurrentUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUserById()
            if (user != null) {
                currentUser.postValue(user)
            }
        }
    }

}