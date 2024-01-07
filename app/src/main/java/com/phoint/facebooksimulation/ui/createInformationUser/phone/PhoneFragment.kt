package com.phoint.facebooksimulation.ui.createInformationUser.phone

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentPhoneBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class PhoneFragment : BaseFragment<FragmentPhoneBinding, PhoneViewModel>() {
    private var isObservingUserID = false
    override fun layoutRes(): Int {
        return R.layout.fragment_phone
    }

    override fun viewModelClass(): Class<PhoneViewModel> {
        return PhoneViewModel::class.java
    }

    override fun initView() {
        viewModel.getIdUser(viewModel.getId())

        binding.edtPhone.addTextChangedListener {
            val phone = binding.edtPhone.text.toString().trim()
            if (it?.isNotEmpty()!! && android.util.Patterns.PHONE.matcher(phone).matches()) {
                binding.tvShowError.text = ""
                binding.edtPhone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
            } else if (it.isEmpty()) {
                binding.edtPhone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
                binding.tvShowError.text = ""
            } else {
                binding.edtPhone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "\tSố điện thoại không hợp lệ!"
            }
        }

        binding.btnSignGmail.setOnSingClickListener {
            findNavController().navigate(R.id.action_phoneFragment_to_emailFragment)
        }

        binding.actionBar.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().navigate(R.id.action_phoneFragment_to_loginFragment)
        }
    }

    private fun handlePhone() {
        binding.btnNext.setOnSingClickListener {
            val phone = binding.edtPhone.text.toString().trim()
            if (phone.isNotEmpty() && android.util.Patterns.PHONE.matcher(phone).matches()) {
                viewModel.userList.observe(this) { users ->
                    var isPhoneUsed = false
                    for (user in users) {
                        if (user.phoneUser == phone && user.idUser != viewModel.getId()) {
                            Toast.makeText(
                                requireContext(),
                                "Số điện thoại đã được sử dụng",
                                Toast.LENGTH_SHORT
                            ).show()
                            isPhoneUsed = true
                            break
                        }
                    }

                    if (!isPhoneUsed) {
                        if (!isObservingUserID) {
                            viewModel.userId.observe(this) {
                                it.phoneUser = phone
                                viewModel.updateUser(it)
                                findNavController().navigate(R.id.action_phoneFragment_to_passwordFragment)
                            }
                            isObservingUserID = true
                        }
                    }
                }
            } else {
                binding.edtPhone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "\tVui lòng nhập số điện thoại!"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.userId.removeObservers(this)
            viewModel.userList.removeObservers(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isObservingUserID = false
        handlePhone()
    }

}
