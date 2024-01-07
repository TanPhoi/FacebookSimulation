package com.phoint.facebooksimulation.ui.createInformationUser.highSchool

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

class HighSchoolViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    val done = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()
    val isFunctionalityVisible = MutableLiveData<Boolean>()

    init {

    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getInformationHighSchool(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }

    fun updateHighSchool(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
            done.postValue(true)
        }
    }

    fun deleteHighSchool(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deleteHighSchool(id)
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