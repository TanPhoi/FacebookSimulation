package com.phoint.facebooksimulation.ui.changeUserName

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentChangeUserNameBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ChangeUserNameFragment :
    BaseFragment<FragmentChangeUserNameBinding, ChangeUserNameViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_change_user_name
    }

    override fun viewModelClass(): Class<ChangeUserNameViewModel> {
        return ChangeUserNameViewModel::class.java
    }

    override fun initView() {
        val currentUserId = viewModel.getCurrentUserId()
        viewModel.getCurrentUserById(currentUserId)
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                binding.edtName.setText("${user.nameUser}")
            }
        }

        binding.abChangeUserName.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnExit.setOnSingClickListener {
            findNavController().popBackStack()
        }

        binding.btnConfirm.setOnSingClickListener {
            viewModel.currentUser.observe(this) { user ->
                user.nameUser = binding.edtName.text.toString().trim()
                viewModel.updateUser(user)
                findNavController().popBackStack()
            }
        }

    }
}