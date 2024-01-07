package com.phoint.facebooksimulation.ui.permission

import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PermissionViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : BaseViewModel() {

    init {

    }

    fun updatePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updatePost(post)
        }
    }

}