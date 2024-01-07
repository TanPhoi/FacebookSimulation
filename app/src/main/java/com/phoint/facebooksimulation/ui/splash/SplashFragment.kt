package com.phoint.facebooksimulation.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSplashBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    override fun layoutRes(): Int = R.layout.fragment_splash

    override fun viewModelClass(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun initView() {
        val isLoggedIn = viewModel.getLoginStatus()
        val userId = viewModel.getUserId()

        if (isLoggedIn) {
            viewModel.setId(userId)
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_userNavigationFragment)
            }, 3000)
        } else {
            viewModel.logout()
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }, 3000)
        }

    }
}