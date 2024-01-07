package com.phoint.facebooksimulation.ui.searchHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchHistoryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var saveOtherUserList = MutableLiveData<List<SaveOtherUser>>()
    var userList = MutableLiveData<List<User>>()

    init {
        getAllUsers()
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            if (user != null) {
                userList.postValue(user)
            }
        }
    }

    fun getCurrentIdUser(): Long {
        return appPreferences.getId()
    }

    fun getAllSaveOtherOwnerId(userOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val saveOtherUser = localRepository.getAllSaveOtherOwnerId(userOwnerId)
            if (saveOtherUser != null) {
                saveOtherUserList.postValue(saveOtherUser)
            }
        }
    }

    fun deleteSaveOtherUser(saveOtherUser: SaveOtherUser) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteSaveOtherUser(saveOtherUser)
        }
    }
}