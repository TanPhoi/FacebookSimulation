package com.phoint.facebooksimulation.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var currentUserId = MutableLiveData<User>()

    init {

    }

    fun logout() {
        appPreferences.logout()
    }

    fun getCurrentUserIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getUserById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null) {
                currentUserId.postValue(user)
            }
        }
    }
}