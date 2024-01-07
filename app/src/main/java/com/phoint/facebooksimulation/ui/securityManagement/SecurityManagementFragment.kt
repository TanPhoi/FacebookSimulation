package com.phoint.facebooksimulation.ui.securityManagement

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSecurityManagementBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SecurityManagementFragment :
    BaseFragment<FragmentSecurityManagementBinding, SecurityManagementViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_security_management
    }

    override fun viewModelClass(): Class<SecurityManagementViewModel> {
        return SecurityManagementViewModel::class.java
    }

    override fun initView() {
        binding.abChangePassword.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnChangePassword.setOnSingClickListener {
            findNavController().navigate(R.id.action_securityManagementFragment_to_changePasswordFragment)
        }
    }
}