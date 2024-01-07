package com.phoint.facebooksimulation.ui.posts

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

class PostsAnArticleViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var informationUser = MutableLiveData<User>()
    var done = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()

    init {

    }

    fun setIdPost(id: Long) {
        appPreferences.saveIdPost(id)
    }

    fun getIdPost(): Long {
        return appPreferences.getIdPost()
    }

    fun getIdPreferences(): Long {
        return appPreferences.getId()
    }

    fun getInformationUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            var user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                informationUser.postValue(user)
            }
        }
    }

    fun insertPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPost(post)
            done.postValue(true)
        }
    }
}