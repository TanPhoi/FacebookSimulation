package com.phoint.facebooksimulation.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var notificationList = MutableLiveData<List<Notification>>()
    var informationUserIdList = MutableLiveData<List<User>>()
    var currentUser = MutableLiveData<User>()
    var isPost = MutableLiveData<Post>()

    init {
        getAllUsers()
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteNotification(notification)
        }
    }

    fun updateNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateNotification(notification)
        }
    }


    fun getPostById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getPostById(id)
            if (post != null) {
                isPost.postValue(post)
            }
        }
    }

    fun getCurrentUserId(): Long {
        return appPreferences.getId()
    }

    fun getCurrentUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null) {
                currentUser.postValue(user)
            }
        }
    }

    fun getAllNotificationById(userId: Long, currentUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = localRepository.getAllNotificationById(userId, currentUserId)
            if (notification != null) {
                notificationList.postValue(notification)
            }
        }
    }


    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            if (user != null) {
                informationUserIdList.postValue(user)
            }
        }
    }
}