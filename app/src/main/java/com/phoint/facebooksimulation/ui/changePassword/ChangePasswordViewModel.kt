package com.phoint.facebooksimulation.ui.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangePasswordViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    val passwordUser = MutableLiveData<User>()
    val successfulPassword = MutableLiveData<Boolean>()

    init {

    }

    fun getCurrentUserId(): Long {
        return appPreferences.getId()
    }

    fun changePasswordById(id: Long, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.changePasswordById(id, password)
            if (user != null && user.passwordUser == password) {
                passwordUser.postValue(user)
                successfulPassword.postValue(true)
            } else {
                successfulPassword.postValue(false)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
        }
    }
}