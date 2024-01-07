package com.phoint.facebooksimulation.ui.seeComment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Comment
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Reply
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeeCommentsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var saveDataByPost = MutableLiveData<SaveFavoritePost>()
    var postDataByUser = MutableLiveData<Post>()
    var informationUser = MutableLiveData<User>()
    var informationUserComment = MutableLiveData<User>()
    var commentList = MutableLiveData<List<Comment>>()
    var userList = MutableLiveData<List<User>>()
    var postList = MutableLiveData<List<Post>>()
    var replyList = MutableLiveData<List<Reply>>()
    var isLoading = MutableLiveData<Boolean>()
    var saveFavoritePostUser = MutableLiveData<SaveFavoritePost>()
    var savePosts = MutableLiveData<List<SaveFavoritePost>>()
    var doneUpdate = MutableLiveData<Boolean>()
    var userById = MutableLiveData<User>()
    var reportComment = MutableLiveData<Report>()

    init {
        getAllUsers()
        getAllPosts()
        getAllReply()
        getAllSavePosts()
    }

    fun getUserByIdPost(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                userById.postValue(user)
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

    fun getAllDataByPost(postOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveFavoritePost = localRepository.getAllDataByPost(postOwnerId)
            if (saveFavoritePost != null && saveFavoritePost.postOwnerId == postOwnerId) {
                saveDataByPost.postValue(saveFavoritePost)
            }
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
            doneUpdate.postValue(true)
        }
    }

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deletePost(post)
        }
    }

    fun getIdUserPreferences(): Long {
        return appPreferences.getId()
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
        }
    }

    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getAllPost()
            postList.postValue(post)
        }
    }

    private fun getAllReply() {
        viewModelScope.launch(Dispatchers.IO) {
            val reply = localRepository.getAllReply()
            replyList.postValue(reply)
        }
    }

    fun getPostDataByUser(userId: Long, postId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getPostDataByUser(userId, postId)
            if (post != null && post.idPost == postId && post.userIdPost == userId) {
                postDataByUser.postValue(post)
            }
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

    fun startLoading() {
        viewModelScope.launch {
            delay(1600)
            isLoading.postValue(false)
        }
    }
}