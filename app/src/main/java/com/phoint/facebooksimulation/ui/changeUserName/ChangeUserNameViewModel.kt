package com.phoint.facebooksimulation.ui.changeUserName

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangeUserNameViewModel @Inject constructor(

    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var currentUser = MutableLiveData<User>()

    init {

    }

    fun getCurrentUserId(): Long {
        return appPreferences.getId()
    }

    fun getCurrentUserById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null) {
                currentUser.postValue(user)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
        }
    }
}