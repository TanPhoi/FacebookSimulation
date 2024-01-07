package com.phoint.facebooksimulation.ui.findNewFriends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.RemoveUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FindNewFriendsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var userList = MutableLiveData<List<User>>()
    val userId = MutableLiveData<User>()
    var friendRequest = MutableLiveData<FriendRequest>()

    init {
        // getAllUsers()
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

    fun getUsersWithFriendRequests(currentUserId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getUsersWithFriendRequests(currentUserId)
            userList.postValue(user)
        }
    }

    fun insertFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertFriendRequest(friendRequest)
        }
    }

    fun getFriendRequestById(senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getFriendRequestById(senderId, receiverId)
            if (friend != null) {
                friendRequest.postValue(friend)
            }
        }
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteFriendRequest(friendRequest)
        }
    }

    fun insertRemoveUser(removeUser: RemoveUser) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertRemoveUser(removeUser)
        }
    }
}