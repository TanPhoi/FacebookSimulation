package com.phoint.facebooksimulation.ui.enterOTP

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentEnterOtpBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class EnterOTPFragment : BaseFragment<FragmentEnterOtpBinding, EnterOTPViewModel>() {
    private var verificationCodeSent = ""
    private val auth = FirebaseAuth.getInstance()
    private var user: User? = null
    override fun layoutRes(): Int {
        return R.layout.fragment_enter_otp
    }

    override fun viewModelClass(): Class<EnterOTPViewModel> {
        return EnterOTPViewModel::class.java
    }

    override fun initView() {
        user = arguments?.getParcelable("user") as? User
        verificationCodeSent = arguments?.getString("verificationCode") ?: ""
        binding.tvEmail.text = user?.phoneUser

        binding.abEmailDirections.setOnSingClickLeft {
            findNavController().popBackStack()
        }

        binding.btnVerify.setOnClickListener {
            val enteredCode = binding.edtVerificationCode.text.toString().trim()
            val credential = PhoneAuthProvider.getCredential(verificationCodeSent, enteredCode)
            signInWithPhoneAuthCredential(credential)
        }

        binding.btnResendCode.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    findNavController().navigate(
                        R.id.action_enterOTPFragment_to_changeForgetPasswordFragment, bundleOf(
                            Pair("user", user)
                        )
                    )
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            requireContext(),
                            "Mã OTP không đúng. Vui lòng kiểm tra lại.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}