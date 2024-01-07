package com.phoint.facebooksimulation.ui.splash

import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val appPreferences: AppPreferences
) : BaseViewModel() {
    init {

    }

    fun logout() {
        appPreferences.logout()
    }

    fun getLoginStatus(): Boolean {
        return appPreferences.getLoginStatus()
    }

    fun getUserId(): Long {
        return appPreferences.getUserId()
    }

    fun setId(id: Long) {
        appPreferences.saveId(id)
    }
}