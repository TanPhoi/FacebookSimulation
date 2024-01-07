package com.phoint.facebooksimulation.ui.createInformationUser.createUserName

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateUserNameViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var done = MutableLiveData<Boolean>()

    init {

    }

    fun setId(id: Long) {
        appPreferences.saveId(id)
    }

    fun getId(): Long {
        return appPreferences.getId()
    }

    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertUser(user)
            done.postValue(true)
        }
    }
}