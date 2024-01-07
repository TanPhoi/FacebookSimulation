package com.phoint.facebooksimulation.ui.createInformationUser.homeTown

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeTownViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    val informationUser = MutableLiveData<User>()
    val done = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isFunctionalityVisible = MutableLiveData<Boolean>()

    init {

    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getInformationHomeTown(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }

    fun updateHomeTown(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
            done.postValue(true)
        }
    }

    fun deleteHomeTown(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteHomeTown(id)
        }
    }

    fun startLoading() {
        isLoading.postValue(true)
        isFunctionalityVisible.value = false
        viewModelScope.launch {
            delay(2000)
            isLoading.postValue(false)
            isFunctionalityVisible.value = true
        }
    }
}