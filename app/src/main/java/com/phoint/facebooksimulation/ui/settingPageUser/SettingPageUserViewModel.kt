package com.phoint.facebooksimulation.ui.settingPageUser

import com.phoint.facebooksimulation.data.local.AppPreferences
import com.phoint.facebooksimulation.data.local.LocalRepository
import com.phoint.facebooksimulation.ui.base.BaseViewModel
import javax.inject.Inject

class SettingPageUserViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val localRepository: LocalRepository
) : BaseViewModel() {
    init {

    }
}