package com.phoint.facebooksimulation.ui.friend

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

class FriendViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var friendList = MutableLiveData<List<FriendRequest>>()
    var friendRequest = MutableLiveData<FriendRequest>()
    var friendRequestByIdReceiver = MutableLiveData<FriendRequest>()
    val doneUpdateFriendRequest = MutableLiveData<Boolean>()
    var receiver = MutableLiveData<FriendRequest>()
    var userList = MutableLiveData<List<User>>()

    init {
        getAllUsers()
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

    fun getReceiverId(receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val receiverId = localRepository.getReceiverId(receiverId)
            if (receiverId != null) {
                receiver.postValue(receiverId)
            }
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

    fun getFriendRequestByIdReceiver(senderId: Long, receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val friend = localRepository.getFriendRequestByIdReceiver(senderId, receiverId)
            if (friend != null) {
                friendRequestByIdReceiver.postValue(friend)
            }
        }
    }

    fun deleteFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteFriendRequest(friendRequest)
        }
    }

    fun updateFriendRequest(friendRequest: FriendRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateFriendRequest(friendRequest)
            doneUpdateFriendRequest.postValue(true)
        }
    }
}