package com.phoint.facebooksimulation.ui.searchOtherUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchOtherUserViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    private var searchQueryResult = MutableLiveData<List<User>>()

    init {

    }

    fun getCurrentIdUser(): Long {
        return appPreferences.getId()
    }

    fun insertSaveOtherUser(saveOtherUser: SaveOtherUser) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingUser =
                getSaveOtherUserByName(saveOtherUser.userOwnerId!!, saveOtherUser.idUserOther!!)
            if (existingUser == null) {
                localRepository.insertSaveOtherUser(saveOtherUser)
            }
        }
    }

    private suspend fun getSaveOtherUserByName(
        idUserCurrent: Long,
        idUserOther: Long
    ): SaveOtherUser {
        return localRepository.getSaveOtherUserByName(idUserCurrent, idUserOther)
    }

    fun searchNameUser(query: String) {
        if (query.length >= 2) {
            viewModelScope.launch(Dispatchers.IO) {
                val searchResult = localRepository.searchNameUser(query)
                searchQueryResult.postValue(searchResult)
            }
        }
    }

    fun getSearchResult(query: String): LiveData<List<User>> {
        return Transformations.map(searchQueryResult) { userList ->
            userList.filter { user ->
                user.nameUser!!.contains(
                    query,
                    ignoreCase = true
                ) && countCommonChars(user.nameUser!!, query) >= 2
            }
        }
    }

    private fun countCommonChars(str1: String, str2: String): Int {
        var count = 0
        for (char in str1) {
            if (str2.contains(char, ignoreCase = true)) {
                count++
            }
        }
        return count
    }
}