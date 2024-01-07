package com.phoint.facebooksimulation.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var loginUser = MutableLiveData<User>()
    var doneUser = MutableLiveData<Boolean>()
    var currentUserId = MutableLiveData<User>()

    init {

    }

    fun setId(id: Long) {
        appPreferences.saveId(id)
    }

    fun saveLoginStatus(isLoggedIn: Boolean, userId: Long) {
        appPreferences.saveLoginStatus(isLoggedIn, userId)
    }

    fun login(phone: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.login(phone, password)
            if (user != null && user.passwordUser == password) {
                loginUser.postValue(user)
                doneUser.postValue(true)
            } else {
                doneUser.postValue(false)
            }
        }
    }


    fun getCurrentUserId(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                currentUserId.postValue(user)
            }
        }
    }
}