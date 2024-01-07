package com.phoint.facebooksimulation.ui.findAccount

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentFindAccountBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener
import java.util.concurrent.TimeUnit

class FindAccountFragment : BaseFragment<FragmentFindAccountBinding, FindAccountViewModel>() {
    private var isObservingUserID = false
    private val auth = FirebaseAuth.getInstance()
    private var verificationCode = ""
    private var phoneAuthProvider: PhoneAuthProvider.ForceResendingToken? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Bạn đã cấp quyền truy cập", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bạn đã từ chối quyền truy cập",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    override fun layoutRes(): Int {
        return R.layout.fragment_find_account
    }

    override fun viewModelClass(): Class<FindAccountViewModel> {
        return FindAccountViewModel::class.java
    }

    override fun initView() {

        handleFindAccount()

        binding.abFindAccount.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    private fun handleFindAccount() {
        binding.btnNext.setOnSingClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            val permission = Manifest.permission.SEND_SMS
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (phone.isNotEmpty() && android.util.Patterns.PHONE.matcher(phone).matches()) {
                    viewModel.getFindAccount(phone)
                    if (!isObservingUserID) {
                        viewModel.findAccountUser.observe(this) { user ->
                            if (user != null) {
                                val options = PhoneAuthOptions.newBuilder(auth)
                                    .setPhoneNumber("+84${user.phoneUser!!}") // Phone number to verify
                                    .setTimeout(5L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(requireActivity()) // Activity (for callback binding)
                                    .setCallbacks(object :
                                        PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                        }

                                        override fun onVerificationFailed(p0: FirebaseException) {
                                            Toast.makeText(
                                                requireContext(),
                                                "OTP không thành công",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                        override fun onCodeSent(
                                            p0: String,
                                            p1: PhoneAuthProvider.ForceResendingToken
                                        ) {
                                            super.onCodeSent(p0, p1)

                                            verificationCode = p0
                                            phoneAuthProvider = p1
                                            Toast.makeText(
                                                requireContext(),
                                                "Gửi OTP thành công",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(
                                                R.id.action_findAccountFragment_to_enterOTPFragment,
                                                bundleOf(
                                                    Pair("verificationCode", verificationCode),
                                                    Pair("user", user)
                                                )
                                            )
                                        }
                                    })
                                    .build()
                                PhoneAuthProvider.verifyPhoneNumber(options)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Số điện thoại bạn nhập không đúng!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        isObservingUserID = true
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Số điện thoại chưa được nhập hoặc không hợp lệ!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.findAccountUser.removeObservers(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isObservingUserID = false
        handleFindAccount()
    }

}