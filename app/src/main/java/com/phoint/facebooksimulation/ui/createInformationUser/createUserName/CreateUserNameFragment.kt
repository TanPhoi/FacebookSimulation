package com.phoint.facebooksimulation.ui.createInformationUser.createUserName

import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentCreateUserNameBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class CreateUserNameFragment :
    BaseFragment<FragmentCreateUserNameBinding, CreateUserNameViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_create_user_name
    }

    override fun viewModelClass(): Class<CreateUserNameViewModel> {
        return CreateUserNameViewModel::class.java
    }

    override fun initView() {
        binding.btnNext.setOnSingClickListener {
            if (isNotEmpty()) {
                val surname = binding.edtSurname.text.toString().trim()
                val name = binding.edtName.text.toString().trim()

                val user = User().apply {
                    idUser = System.currentTimeMillis()
                    nameUser = "$surname $name"
                }
                viewModel.insertUser(user)
                viewModel.setId(user.idUser!!)

                findNavController().navigate(R.id.action_createUserNameFragment_to_dataOfBirthFragment)
            } else {
                setIsNotEmpty()
            }
        }

        binding.actionBar.setOnBackClick {
            findNavController().navigateUp()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().popBackStack()
        }
    }

    private fun isNotEmpty(): Boolean {
        val surname = binding.edtSurname.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        return surname.isNotEmpty() && name.isNotEmpty()
    }

    private fun setIsNotEmpty() {
        isNotEmpty()
        val surname = binding.edtSurname.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        if (surname.isEmpty() && name.isEmpty()) {
            binding.edtSurname.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
            binding.edtName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
            binding.tvShowError.text = "Vui lòng nhập họ và tên của bạn"
        } else if (surname.isEmpty()) {
            binding.edtSurname.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
            binding.tvShowError.text = "Vui lòng nhập họ của bạn"
        } else {
            binding.edtName.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
            binding.edtSurname.error
            binding.tvShowError.text = "Vui lòng nhập tên của bạn"
        }
    }
}