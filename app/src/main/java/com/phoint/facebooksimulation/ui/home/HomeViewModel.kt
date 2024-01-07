package com.phoint.facebooksimulation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.HidePost
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var ownerPost = MutableLiveData<User>()
    var receiver = MutableLiveData<List<FriendRequest>>()
    var postList = MutableLiveData<List<Post>>()
    var postAllList = MutableLiveData<List<Post>>()
    var userList = MutableLiveData<List<User>>()
    var idHidePostList = MutableLiveData<List<Long>>()
    var friendList = MutableLiveData<List<FriendRequest>>()
    val userId = MutableLiveData<User>()
    val userById = MutableLiveData<User>()
    var savePosts = MutableLiveData<List<SaveFavoritePost>>()
    var saveFavoritePostUser = MutableLiveData<SaveFavoritePost>()
    var reportPost = MutableLiveData<Report>()
    var currentUserId = MutableLiveData<User>()
    var notificationById = MutableLiveData<Notification>()

    init {
        getAllSavePosts()
        getAllUsers()
        getAllPosts()
    }

    fun insertPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPost(post)
        }
    }

    fun getNotificationById(sender_id: Long, userId: Long, postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = localRepository.getNotificationById(sender_id, userId, postId)
            if (notification != null) {
                notificationById.postValue(notification)
            }
        }
    }

    fun insertNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertNotification(notification)
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteNotification(notification)
        }
    }

    fun insertHidePost(hidePost: HidePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertHidePost(hidePost)
        }
    }

    fun getHiddenPostIds(idUser: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val hidePost = localRepository.getHiddenPostIds(idUser)
            if (hidePost != null) {
                idHidePostList.postValue(hidePost)
            }
        }
    }

    fun getUserById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null) {
                currentUserId.postValue(user)
            }
        }
    }

    fun getAllReportPost(idReport: Long, postId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val report = localRepository.getAllReportPost(idReport, postId, userId)
            if (report != null && report.id == idReport && report.postId == postId && report.userId == userId) {
                reportPost.postValue(report)
            }
        }
    }

    fun deleteReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteReport(report)
        }
    }

    fun updateReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateReport(report)
        }
    }

    fun insertReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertReport(report)
        }
    }

    fun deleteSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteSaveFavoritePost(saveFavoritePost)
        }
    }

    fun getAllSaveFavoritePost(userOwnerId: Long, postOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveFavoritePost = localRepository.getAllSaveFavoritePost(userOwnerId, postOwnerId)
            if (saveFavoritePost != null && saveFavoritePost.userOwnerId == userOwnerId && saveFavoritePost.postOwnerId == postOwnerId) {
                saveFavoritePostUser.postValue(saveFavoritePost)
            }
        }
    }

    fun insertSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertSaveFavoritePost(saveFavoritePost)
        }
    }

    // lấy data user của bài post
    fun getOwnerPost(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                ownerPost.postValue(user)
            }
        }
    }

    private fun getAllSavePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val savePost = localRepository.getAllSavePosts()
            if (savePost != null) {
                savePosts.postValue(savePost)
            }
        }
    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
        }
    }

    fun getInformationUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                userId.postValue(user)
            }
        }
    }

    fun getUserByIdPost(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                userById.postValue(user)
            }
        }
    }

    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getAllPosts()
            if (post != null) {
                postAllList.postValue(post)
            }
        }
    }

    fun getReceiverId(receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val receiverId = localRepository.getFilteredFriends(receiverId)
            if (receiverId != null) {
                receiver.postValue(receiverId)
            }
        }
    }

    fun getAllFriend(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getAllFriend(id)
            if (friend != null) {
                friendList.postValue(friend)
            }
        }
    }

    fun getJoinDataPost(idUser: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getJoinDataPost(idUser)
            if (post != null) {
                postList.postValue(post)
            }
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            if (user != null) {
                userList.postValue(user)
            }
        }
    }

}