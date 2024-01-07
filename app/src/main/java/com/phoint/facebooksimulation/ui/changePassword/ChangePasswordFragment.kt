package com.phoint.facebooksimulation.ui.changePassword

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentChangePasswordBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>() {
    private val uppercaseRegex = Regex("[A-Z]+")
    private val lowercaseRegex = Regex("[a-z]+")
    private var isCurrentPassword = ""
    private var isNewPassword = ""
    private var isConfirmNewPassword = ""
    override fun layoutRes(): Int {
        return R.layout.fragment_change_password
    }

    override fun viewModelClass(): Class<ChangePasswordViewModel> {
        return ChangePasswordViewModel::class.java
    }

    override fun initView() {
        val currentUserId = viewModel.getCurrentUserId()

        binding.abChangePassword.setOnSingClickLeft {
            findNavController().popBackStack()
        }

        binding.edtCurrentPassword.addTextChangedListener {
            isCurrentPassword = it.toString()
        }

        binding.edtNewPassword.addTextChangedListener {
            isNewPassword = it.toString()
        }

        binding.edtConfirmNewPassword.addTextChangedListener {
            isConfirmNewPassword = it.toString()
        }

        binding.btnConfirmPassword.setOnSingClickListener {
            if (isCurrentPassword.isNotEmpty() && isNewPassword.isNotEmpty() && isConfirmNewPassword.isNotEmpty()) {
                if (isNewPassword.length < 10 && !isNewPassword.contains(uppercaseRegex) && !isNewPassword.contains(
                        lowercaseRegex
                    )
                ) {
                    binding.edtNewPassword.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.cardview_error_border
                    )
                    Toast.makeText(
                        requireContext(),
                        "Mật khẩu phải chứa ít nhất một chữ hoa và một chữ thường",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.edtNewPassword.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.background_true)

                    if (isNewPassword == isConfirmNewPassword) {
                        viewModel.changePasswordById(
                            currentUserId,
                            binding.edtCurrentPassword.text.toString().trim()
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Mật khẩu không hợp lệ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(requireContext(), "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.successfulPassword.observe(this) {
            if (it == false) {
                Toast.makeText(
                    requireContext(),
                    "Mật khẩu hiện tại không hợp lệ",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.passwordUser.observe(this) {
                    it.passwordUser = binding.edtNewPassword.text.toString().trim()
                    viewModel.updateUser(it)
                    findNavController().popBackStack()
                }
            }
        }

        binding.btnExit.setOnSingClickListener {
            findNavController().popBackStack()
        }
    }

}