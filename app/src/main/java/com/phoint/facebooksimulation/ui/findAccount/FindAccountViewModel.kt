package com.phoint.facebooksimulation.ui.findAccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FindAccountViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : BaseViewModel() {
    var findAccountUser = MutableLiveData<User>()
    var doneUser = MutableLiveData<Boolean>()
    var verificationCode = MutableLiveData<String>()
    var resetEmailSent = MutableLiveData<Boolean>()
    var errorMessage = MutableLiveData<String>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var resetPasswordMessage = MutableLiveData<String>()

    init {

    }

    fun getFindAccount(s: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = localRepository.getFindAccount(s)
            if (user != null) {
                findAccountUser.postValue(user)
                doneUser.postValue(true)
            } else {
                doneUser.postValue(false)
            }
        }
    }

//    fun sendVerificationCode(email: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Step 1: Xác minh email
//                auth.createUserWithEmailAndPassword(email, "password").await()
//
//                // Step 2: Gửi email xác minh
//                val user = auth.currentUser
//                user?.sendEmailVerification()?.await()
//
//                // Step 3: Lấy mã xác minh từ email (đây chỉ là một ví dụ, bạn cần phải phân tích nội dung email để lấy mã)
//                val verificationCodes = extractVerificationCodeFromEmail(email)
//
//                // Step 4: Cập nhật LiveData với mã xác minh
//                verificationCodes?.let {
//                    verificationCode.postValue(it)
//                } ?: doneUser.postValue(false)
//
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                doneUser.postValue(false)
//            }
//        }
//    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Email đặt lại mật khẩu đã được gửi thành công
                        resetEmailSent.postValue(true)
                        resetPasswordMessage.postValue("Mật khẩu đã được đặt lại. Vui lòng kiểm tra email của bạn.")
                    } else {
                        // Gặp lỗi khi gửi email đặt lại mật khẩu
                        errorMessage.postValue(task.exception?.message ?: "Unknown error")
                    }
                }
        }
    }

    private fun extractVerificationCodeFromEmail(email: String): String? {
        return "123456" // Đổi thành hàm thực tế của bạn để trích xuất mã xác minh
    }
}