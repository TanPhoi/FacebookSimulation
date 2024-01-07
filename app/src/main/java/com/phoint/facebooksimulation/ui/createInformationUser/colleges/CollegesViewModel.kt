package com.phoint.facebooksimulation.ui.createInformationUser.colleges

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

class CollegesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    val informationUser = MutableLiveData<User>()
    val done = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val isFunctionalityVisible = MutableLiveData<Boolean>()

    init {

    }

    fun getIDPreferences(): Long {
        return appPreferences.getId()
    }

    fun getIdUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }

    fun updateColleges(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
            done.postValue(true)
        }
    }

    fun deleteColleges(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteColleges(id)
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