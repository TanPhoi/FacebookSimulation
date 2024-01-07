package com.phoint.facebooksimulation.ui.saveFavoritePosts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveFavoritePostsViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    var savePostList = MutableLiveData<List<SaveFavoritePost>>()
    var userList = MutableLiveData<List<User>>()
    var postList = MutableLiveData<List<Post>>()

    init {
        getAllUsers()
        getAllPosts()
    }

    fun getSavePostDataByUser(userOwnerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val savePost = localRepository.getSavePostDataByUser(userOwnerId)
            if (savePost != null) {
                savePostList.postValue(savePost)
            }
        }
    }

    fun getIdUser(): Long {
        return appPreferences.getId()
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            userList.postValue(user)
        }
    }


    private fun getAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getAllPost()
            postList.postValue(post)
        }
    }
}