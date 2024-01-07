package com.phoint.facebooksimulation.ui.createInformationUser.password

import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentPasswordBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PasswordFragment : BaseFragment<FragmentPasswordBinding, PasswordViewModel>() {
    private val uppercaseRegex = Regex("[A-Z]+")
    private val lowercaseRegex = Regex("[a-z]+")
    private var isObservingUserID = false
    override fun layoutRes(): Int {
        return R.layout.fragment_password
    }

    override fun viewModelClass(): Class<PasswordViewModel> {
        return PasswordViewModel::class.java
    }

    override fun initView() {
        viewModel.getIdUser(viewModel.getId())
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                if (password.isNotEmpty()) {
                    if (password.length < 10 && !password.contains(uppercaseRegex) && !password.contains(
                            lowercaseRegex
                        )
                    ) {
                        binding.edtPassword.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.cardview_error_border
                        )
                        binding.tvShowError.text =
                            "Mật khẩu phải chứa ít nhất một chữ hoa và một chữ thường"
                    } else {
                        binding.edtPassword.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
                        binding.tvShowError.text = ""
                    }
                } else {
                    binding.edtPassword.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.cardview_error_border
                    )
                    binding.tvShowError.text = "Xin vui lòng nhập mật khẩu"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        handlePassword()

        binding.actionBar.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().navigate(R.id.action_passwordFragment_to_loginFragment)
        }
    }

    private fun handlePassword() {
        binding.btnNext.setOnSingClickListener {
            val password = binding.edtPassword.text.toString().trim()
            if (password.length < 10 && password.isNotEmpty()) {
                if (!password.contains(uppercaseRegex) && !password.contains(lowercaseRegex)) {
                    binding.edtPassword.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.cardview_error_border
                    )
                    binding.tvShowError.text =
                        "Mật khẩu phải chứa ít nhất một chữ hoa và một chữ thường"
                } else {
                    binding.edtPassword.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
                    binding.tvShowError.text = ""

                    if (!isObservingUserID) {
                        viewModel.userId.observe(this) {
                            it.passwordUser = password
                            viewModel.updateUser(it)
                            findNavController().navigate(R.id.action_passwordFragment_to_loginFragmentDone)
                        }
                        isObservingUserID = true
                    }
                }
            } else {
                binding.edtPassword.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "Xin vui lòng nhập mật khẩu"
            }
        }
    }


    override fun onResume() {
        super.onResume()
        isObservingUserID = false
        handlePassword()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.userId.removeObservers(this)
        }
    }
}
