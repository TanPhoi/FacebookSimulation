package com.phoint.facebooksimulation.ui.userOfFriend

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

class UserOfFriendViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var friendList = MutableLiveData<List<FriendRequest>>()
    var userList = MutableLiveData<List<User>>()
    var friendRequest = MutableLiveData<FriendRequest>()
    var currentUser = MutableLiveData<User>()

    init {
        getAllUsers()
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