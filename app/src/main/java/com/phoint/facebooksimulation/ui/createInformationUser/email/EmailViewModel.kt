package com.phoint.facebooksimulation.ui.createInformationUser.email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var userId = MutableLiveData<User>()
    var userList = MutableLiveData<List<User>>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        getAllUsers()
    }

    fun getId(): Long {
        return appPreferences.getId()
    }

    fun getIdUser(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getId(id)
            if (user != null && user.idUser == id) {
                userId.postValue(user)
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.updateUser(user)
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getAllUsers()
            if (user != null) {
                userList.postValue(user)
            }
        }
    }

    fun sendVerificationCode(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val randomPassword = generateRandomPassword()
                auth.createUserWithEmailAndPassword(email, randomPassword)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

//            try {
//                auth.sendPasswordResetEmail(email)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            done.postValue(true)
//                        } else {
//                            done.postValue(false)
//                        }
//                    }
//
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                done.postValue(false)
//            }
        }
    }

    fun generateRandomPassword(): String {
        // Bạn có thể sử dụng thư viện hoặc logic phức tạp hơn để tạo mật khẩu ngẫu nhiên
        return "123456"
    }
}
