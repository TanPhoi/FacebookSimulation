package com.phoint.facebooksimulation.ui.changeForgetPassword

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentChangeForgetPasswordBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ChangeForgetPasswordFragment :
    BaseFragment<FragmentChangeForgetPasswordBinding, ChangeForgetPasswordViewModel>() {
    private val uppercaseRegex = Regex("[A-Z]+")
    private val lowercaseRegex = Regex("[a-z]+")
    private var isNewPassword = ""
    private var isConfirmNewPassword = ""

    override fun layoutRes(): Int {
        return R.layout.fragment_change_forget_password
    }

    override fun viewModelClass(): Class<ChangeForgetPasswordViewModel> {
        return ChangeForgetPasswordViewModel::class.java
    }

    override fun initView() {
        val user = arguments?.getParcelable("user") as? User

        binding.edtNewPassword.addTextChangedListener {
            isNewPassword = it.toString()
        }

        binding.edtConfirmNewPassword.addTextChangedListener {
            isConfirmNewPassword = it.toString()
        }

        binding.btnExit.setOnSingClickListener {
            findNavController().navigate(R.id.action_changeForgetPasswordFragment_to_loginFragment)
        }

        binding.abChangeForgetPassword.setOnSingClickLeft {
            findNavController().navigate(R.id.action_changeForgetPasswordFragment_to_loginFragment)
        }

        binding.btnConfirmPassword.setOnSingClickListener {
            if (isNewPassword.isNotEmpty() && isConfirmNewPassword.isNotEmpty()) {

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
                        viewModel.changePasswordByPhone(user?.phoneUser!!)
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
                viewModel.passwordUser.observe(this) { user ->
                    user.passwordUser = binding.edtNewPassword.text.toString().trim()
                    viewModel.updateUser(user)
                    val translationY = 100f
                    val animatorSet = AnimatorSet().apply {
                        playTogether(
                            ObjectAnimator.ofFloat(
                                binding.rootLayout,
                                "translationY",
                                translationY
                            ),
                            ObjectAnimator.ofFloat(binding.rootLayout, "alpha", 0f)
                        )
                        duration = 500
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(
                                animation: Animator,
                                isReverse: Boolean
                            ) {
                                hideKeyboard()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    findNavController().navigate(R.id.action_changeForgetPasswordFragment_to_loginFragment)
                                }, 2000)
                            }
                        })
                        (activity as BaseActivity<*, *>).showLoading()
                        start()
                    }
                }
            }
        }

    }

    @SuppressLint("ServiceCast")
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}