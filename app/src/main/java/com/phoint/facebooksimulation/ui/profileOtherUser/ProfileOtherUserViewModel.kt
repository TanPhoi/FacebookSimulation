package com.phoint.facebooksimulation.ui.profileOtherUser

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileOtherUserViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var ownerPost = MutableLiveData<User>()
    var postList = MutableLiveData<List<Post>>()
    var userList = MutableLiveData<List<User>>()
    val doneUpdate = MutableLiveData<Boolean>()
    val doneUpdateFriendRequest = MutableLiveData<Boolean>()
    val doneDeleteFriendRequest = MutableLiveData<Boolean>()
    val userId = MutableLiveData<User>()
    var savePosts = MutableLiveData<List<SaveFavoritePost>>()
    var privacyPublic = MutableLiveData<List<Post>>()
    var saveFavoritePostUser = MutableLiveData<SaveFavoritePost>()
    var reportPost = MutableLiveData<Report>()
    var friendRequest = MutableLiveData<FriendRequest>()
    var sender = MutableLiveData<FriendRequest>()
    var receiver = MutableLiveData<FriendRequest>()
    var friendRequestOwnerUser = MutableLiveData<FriendRequest>()
    var notificationById = MutableLiveData<Notification>()
    var friendList = MutableLiveData<List<FriendRequest>>()
    var postAllList = MutableLiveData<List<Post>>()

    init {
        getAllUsers()
        getAllSavePosts()
        getAllPosts()
    }

    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getAllPosts()
            if (post != null) {
                postAllList.postValue(post)
            }
        }
    }

    fun insertPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPost(post)
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

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteNotification(notification)
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

    fun insertFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertFriendRequest(friendRequest)
        }
    }

    fun updateFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateFriendRequest(friendRequest)
            doneUpdateFriendRequest.postValue(true)
        }
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteFriendRequest(friendRequest)
            doneDeleteFriendRequest.postValue(true)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getFriendRequestById(senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getFriendRequestById(senderId, receiverId)
            if (friend != null) {
                friendRequest.postValue(friend)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getSenderId(senderId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val senderId = localRepository.getSenderId(senderId)
            if (senderId != null) {
                sender.postValue(senderId)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getReceiverId(receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val receiverId = localRepository.getReceiverId(receiverId)
            if (receiverId != null) {
                receiver.postValue(receiverId)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getFriendRequest(senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getFriendRequest(senderId, receiverId)
            if (friend != null) {
                friendRequestOwnerUser.postValue(friend)
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

    fun insertReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertReport(report)
        }
    }

    fun updateReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateReport(report)
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

    fun getAllSaveFavoritePost(userOwnerId: Long, postOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveFavoritePost = localRepository.getAllSaveFavoritePost(userOwnerId, postOwnerId)
            if (saveFavoritePost != null && saveFavoritePost.userOwnerId == userOwnerId && saveFavoritePost.postOwnerId == postOwnerId) {
                saveFavoritePostUser.postValue(saveFavoritePost)
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

    fun updateSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateSaveFavoritePost(saveFavoritePost)
        }
    }

    fun deleteSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteSaveFavoritePost(saveFavoritePost)
        }
    }

    fun insertSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertSaveFavoritePost(saveFavoritePost)
        }
    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getInformationUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                userId.postValue(user)
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
            userList.postValue(user)
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
            doneUpdate.postValue(true)
        }
    }

    fun getJoinDataPostPrivacy(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val privacy = localRepository.getJoinDataPostPrivacy(userId)
            if (privacy != null) {
                privacyPublic.postValue(privacy)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
        }
    }
}
