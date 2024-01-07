package com.phoint.facebooksimulation.ui.contactInfo

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentContactInfoBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class ContactInfoFragment : BaseFragment<FragmentContactInfoBinding, ContactInfoViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_contact_info
    }

    override fun viewModelClass(): Class<ContactInfoViewModel> {
        return ContactInfoViewModel::class.java
    }

    override fun initView() {
        val currentUserId = viewModel.getCurrentUserId()

        viewModel.getCurrentUserById(currentUserId)
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                binding.tvPhone.text = "${user.phoneUser}"
                if (user.emailUser != null) {
                    binding.tvEmail.text = "${user.emailUser}"
                } else {
                    binding.tvEmail.text = "Chưa có"
                }
            }
        }

        binding.abContactInfo.setOnBackClick {
            findNavController().popBackStack()
        }
    }

}