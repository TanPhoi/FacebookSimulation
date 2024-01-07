package com.phoint.facebooksimulation.ui.photoPrewiew

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoPreviewViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    val done = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    init {

    }

    fun getIdPreferences(): Long {
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

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            if (user != null) {
                localRepository.updateUser(user)
                done.postValue(true)
            } else {
                done.postValue(true)
            }
        }
    }
}