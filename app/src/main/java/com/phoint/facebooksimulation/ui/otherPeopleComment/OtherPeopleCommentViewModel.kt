package com.phoint.facebooksimulation.ui.otherPeopleComment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class OtherPeopleCommentViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    var commentList = MutableLiveData<List<Comment>>()
    var comments = MutableLiveData<List<Comment>>()
    var userList = MutableLiveData<List<User>>()
    var reportComment = MutableLiveData<Report>()
    var reportPost = MutableLiveData<Report>()
    var postList = MutableLiveData<List<Post>>()
    var saveFavoritePostUser = MutableLiveData<SaveFavoritePost>()
    var saveDataByPost = MutableLiveData<SaveFavoritePost>()
    var savePosts = MutableLiveData<List<SaveFavoritePost>>()
    var informationUserComment = MutableLiveData<User>()
    var doneUpdate = MutableLiveData<Boolean>()
    var userById = MutableLiveData<User>()
    var profileUser = MutableLiveData<User>()
    var notificationById = MutableLiveData<Notification>()
    var notificationCommentById = MutableLiveData<Notification>()

    init {
        getAllComment()
        getAllPosts()
        getAllUsers()
        getAllSavePosts()
    }


    fun getNotificationById(senderId: Long, userId: Long, postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = localRepository.getNotificationById(senderId, userId, postId)
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

    private fun getAllComment() {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = localRepository.getAllComment()
            if (comment != null) {
                comments.postValue(comment)
            }
        }
    }

    fun insertSaveFavoritePost(saveFavoritePost: SaveFavoritePost) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertSaveFavoritePost(saveFavoritePost)
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

    fun getAllSaveFavoritePost(userOwnerId: Long, postOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveFavoritePost = localRepository.getAllSaveFavoritePost(userOwnerId, postOwnerId)
            if (saveFavoritePost != null && saveFavoritePost.userOwnerId == userOwnerId && saveFavoritePost.postOwnerId == postOwnerId) {
                saveFavoritePostUser.postValue(saveFavoritePost)
            }
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

    private fun getAllSavePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val savePost = localRepository.getAllSavePosts()
            if (savePost != null) {
                savePosts.postValue(savePost)
            }
        }
    }

    fun getAllReportComment(commentId: Long, userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val report = localRepository.getAllReportComment(commentId, userId)
            if (report != null && report.commentId == commentId && report.userId == userId) {
                reportComment.postValue(report)
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

    fun deleteReport(report: Report) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteReport(report)
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
            doneUpdate.postValue(true)
        }
    }


    fun getInformationUserComment(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUserComment.postValue(user)
            }
        }
    }

    fun getJoinDataComment(postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = localRepository.getJoinDataComment(postId)
            if (comment != null) {
                commentList.postValue(comment)
            }
        }
    }

    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getAllPost()
            postList.postValue(post)
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
        }
    }

    fun getIdUserPreferences(): Long {
        return appPreferences.getId()
    }

    fun getInformationUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }
}