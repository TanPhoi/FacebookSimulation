package com.phoint.facebooksimulation.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var receiver = MutableLiveData<FriendRequest>()

    init {

    }

    fun getLoginStatus(): Boolean {
        return appPreferences.getLoginStatus()
    }

    fun setId(id: Long) {
        appPreferences.saveId(id)
    }

    fun getUserId(): Long {
        return appPreferences.getUserId()
    }

    fun getReceiverId(receiverId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val receiverId = localRepository.getReceiverId(receiverId)
            if (receiverId != null) {
                receiver.postValue(receiverId)
            }
        }
    }


}