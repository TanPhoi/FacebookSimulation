package com.phoint.facebooksimulation.ui.createInformationUser.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PasswordViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var userId = MutableLiveData<User>()

    init {

    }

    fun getId(): Long {
        return appPreferences.getId()
    }

    fun getIdUser(id: Long) {
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
}