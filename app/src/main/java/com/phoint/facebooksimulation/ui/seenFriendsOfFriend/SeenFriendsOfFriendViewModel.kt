package com.phoint.facebooksimulation.ui.seenFriendsOfFriend

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeenFriendsOfFriendViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences

) : BaseViewModel() {
    var friendList = MutableLiveData<List<FriendRequest>>()
    var friendCurrentUserList = MutableLiveData<List<FriendRequest>>()
    var userList = MutableLiveData<List<User>>()
    var friendRequest = MutableLiveData<FriendRequest>()
    var currentUser = MutableLiveData<User>()

    init {
        getAllUsers()
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

    fun insertFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertFriendRequest(friendRequest)
        }
    }

    fun getFriendRequestByIdDelete(senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getFriendRequestByIdDelete(senderId, receiverId)
            if (friend != null) {
                friendRequest.postValue(friend)
            }
        }
    }

    fun getCurrentUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null) {
                currentUser.postValue(user)
            }
        }
    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getAllFriendOfUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getAllFriend(id)
            if (friend != null) {
                friendList.postValue(friend)
            }
        }
    }

    fun getAllFriendCurrentUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getAllFriendCurrentUser(id)
            if (friend != null) {
                friendCurrentUserList.postValue(friend)
            }
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
        }
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteFriendRequest(friendRequest)
        }
    }
}