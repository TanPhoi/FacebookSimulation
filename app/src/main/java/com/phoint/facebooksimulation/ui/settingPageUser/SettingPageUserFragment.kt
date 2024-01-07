package com.phoint.facebooksimulation.ui.settingPageUser

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSettingPageUserBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class SettingPageUserFragment :
    BaseFragment<FragmentSettingPageUserBinding, SettingPageUserViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_setting_page_user
    }

    override fun viewModelClass(): Class<SettingPageUserViewModel> {
        return SettingPageUserViewModel::class.java
    }

    override fun initView() {
        binding.abSettingUser.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

}