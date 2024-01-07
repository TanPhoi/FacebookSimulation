package com.phoint.facebooksimulation.ui.editPost

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditPostViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    var doneUpdatePost = MutableLiveData<Boolean>()

    init {

    }

    fun getIdUserPreference(): Long {
        return appPreferences.getId()
    }

    fun getInformationUser(userId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(userId)
            if (user != null && user.idUser == userId) {
                informationUser.postValue(user)
            }
        }
    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
            doneUpdatePost.postValue(true)
        }
    }
}