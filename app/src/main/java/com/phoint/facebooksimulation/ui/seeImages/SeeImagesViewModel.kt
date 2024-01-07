package com.phoint.facebooksimulation.ui.seeImages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeeImagesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {

    private val _postUserById = MutableLiveData<List<Post>>()
    val postUserById: LiveData<List<Post>> get() = _postUserById

    init {

    }

    fun getCurrentUserId(): Long {
        return appPreferences.getId()
    }

    fun getJoinDataPost(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = localRepository.getJoinDataPost(id)
            if (post != null) {
                _postUserById.postValue(post)
            }
        }
    }
}