package com.phoint.facebooksimulation.ui.displayImage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DisplayImageViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    var isPostById = MutableLiveData<Post>()
    val done = MutableLiveData<Boolean>()
    val doneUpdate = MutableLiveData<Boolean>()
    var saveDataByPost = MutableLiveData<SaveFavoritePost>()
    var notificationById = MutableLiveData<Notification>()
    var userList = MutableLiveData<List<User>>()

    init {
        getAllUsers()
    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteNotification(notification)
        }
    }

    fun getNotificationById(senderId: Long, userId: Long, postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = localRepository.getNotificationById(senderId, userId, postId)
            if (notification != null) {
                notificationById.postValue(notification)
            }
        }
    }

    fun updateSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateSaveFavoritePost(saveFavoritePost)
        }
    }

    fun getAllDataByPost(postOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveFavoritePost = localRepository.getAllDataByPost(postOwnerId)
            if (saveFavoritePost != null && saveFavoritePost.postOwnerId == postOwnerId) {
                saveDataByPost.postValue(saveFavoritePost)
            }
        }
    }

    fun insertNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertNotification(notification)
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
            doneUpdate.postValue(true)
        }
    }

    fun getInformationUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }

    fun getPostById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getPostById(id)
            if (post != null) {
                isPostById.postValue(post)
            }
        }
    }
}