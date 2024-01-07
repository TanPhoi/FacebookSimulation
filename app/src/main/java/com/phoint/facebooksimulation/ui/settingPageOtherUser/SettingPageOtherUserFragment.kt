package com.phoint.facebooksimulation.ui.settingPageOtherUser

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSettingPageOtherUserBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class SettingPageOtherUserFragment :
    BaseFragment<FragmentSettingPageOtherUserBinding, SettingPageOtherUserViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_setting_page_other_user
    }

    override fun viewModelClass(): Class<SettingPageOtherUserViewModel> {
        return SettingPageOtherUserViewModel::class.java
    }

    override fun initView() {
        binding.abSettingUser.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

}