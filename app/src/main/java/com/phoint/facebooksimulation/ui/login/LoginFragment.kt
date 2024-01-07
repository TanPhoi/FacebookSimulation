package com.phoint.facebooksimulation.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentLoginBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun viewModelClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override fun initView() {
        binding.btnCreateAccount.setOnSingClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createUserNameFragment)
        }

        binding.btnForgotPassword.setOnSingClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_findAccountFragment)
        }

        binding.btnLogin.setOnSingClickListener {
            val user = binding.edtUserName.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (user.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(user, password)
            } else {
                val builder = AlertDialog.Builder(context)
                    .setTitle("Cần có email hoặc số điện thoại")
                    .setMessage("Nhập email hoặc số điện thoại di động của bạn")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()
            }
        }

        viewModel.loginUser.observe(this) {
            viewModel.setId(it.idUser!!)
            viewModel.getCurrentUserId(it.idUser!!)
        }

        viewModel.doneUser.observe(this) {
            if (it == true) {
                viewModel.currentUserId.observe(this) { user ->
                    if (user.emailUser != null) {
                        viewModel.saveLoginStatus(true, user.idUser!!)
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
                                        findNavController().navigate(R.id.action_loginFragment_to_userNavigationFragment)
                                    }, 2000)
                                }
                            })
                            (activity as BaseActivity<*, *>).showLoading()
                            start()
                        }
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_emailRequiredFragment)
                    }
                }
            } else {
                val builder = AlertDialog.Builder(requireContext())
                    .setTitle("Thông báo")
                    .setMessage("Mật khẩu hoặc tài khoản sai")
                    .setPositiveButton("Thử lại") { dialog, _ ->
                        dialog.dismiss()
                    }
                val dialog = builder.create()
                dialog.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.let {
            if (it is ViewDataBinding) {
                it.unbind()
            }
        }
        viewModel.loginUser.removeObservers(this)
        viewModel.doneUser.removeObservers(this)
        viewModel.currentUserId.removeObservers(this)
    }
}
