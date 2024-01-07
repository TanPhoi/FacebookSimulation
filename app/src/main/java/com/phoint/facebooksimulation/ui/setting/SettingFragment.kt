package com.phoint.facebooksimulation.ui.setting

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSettingBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_setting
    }

    override fun viewModelClass(): Class<SettingViewModel> {
        return SettingViewModel::class.java
    }

    override fun initView() {
        binding.btnPersonalAccount.setOnSingClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_personalAndAccountFragment)
        }

        binding.btnPasswordAndPermission.setOnSingClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_securityManagementFragment)
        }

        binding.abSetting.setBackCurrentScreen {
            findNavController().popBackStack()
        }
    }

}