package com.phoint.facebooksimulation.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
    //private val dataSynchronizerViewModel: DataSynchronizerViewModel

) : BaseViewModel() {
    val filteredFriends = MutableLiveData<List<FriendRequest>>()
    var friendList = MutableLiveData<List<FriendRequest>>()
    var postAllList = MutableLiveData<List<Post>>()
    var postList = MutableLiveData<List<Post>>()
    var userList = MutableLiveData<List<User>>()
    var isPinned = MutableLiveData<Post>()
    private val dateTimePostLiveData = MutableLiveData<Long>()
    val userId = MutableLiveData<User>()

    init {
        getAllUsers()
        getIsPinned()
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

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deletePost(post)
        }
    }

    fun insertPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPost(post)
        }
    }

    fun updateFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateFriendRequest(friendRequest)
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

    private fun getIsPinned() {
        viewModelScope.launch(Dispatchers.IO) {
            val isPinnedPost = localRepository.getIsPinned()
            isPinned.postValue(isPinnedPost)
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
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

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
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

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
        }
    }

}