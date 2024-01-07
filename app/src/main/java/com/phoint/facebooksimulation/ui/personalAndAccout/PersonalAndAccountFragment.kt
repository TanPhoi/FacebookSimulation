package com.phoint.facebooksimulation.ui.personalAndAccout

import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentPersonalAndAccountBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PersonalAndAccountFragment :
    BaseFragment<FragmentPersonalAndAccountBinding, PersonalAndAccountViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_personal_and_account
    }

    override fun viewModelClass(): Class<PersonalAndAccountViewModel> {
        return PersonalAndAccountViewModel::class.java
    }

    override fun initView() {
        val currentUserId = viewModel.getCurrentUserId()

        viewModel.getCurrentUserById(currentUserId)
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                binding.tvName.text = "${user.nameUser}"
            }
        }

        binding.abPersonalAndAccount.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnChangeUserName.setOnSingClickListener {
            findNavController().navigate(R.id.action_personalAndAccountFragment_to_changeUserNameFragment)
        }

        binding.btnContactInfo.setOnSingClickListener {
            findNavController().navigate(R.id.action_personalAndAccountFragment_to_contactInfoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.let {
            if (it is ViewDataBinding) {
                it.unbind()
            }
        }
    }
}
