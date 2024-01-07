package com.phoint.facebooksimulation.ui.emailRequired

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentEmailRequiredBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class EmailRequiredFragment : BaseFragment<FragmentEmailRequiredBinding, EmailRequiredViewModel>() {
    private var isObservingUserID = false
    override fun layoutRes(): Int {
        return R.layout.fragment_email_required
    }

    override fun viewModelClass(): Class<EmailRequiredViewModel> {
        return EmailRequiredViewModel::class.java
    }

    override fun initView() {
        viewModel.getIdUser(viewModel.getId())
        binding.edtEmail.addTextChangedListener {
            val email = binding.edtEmail.text.toString().trim()
            if (it?.isNotEmpty()!! && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                binding.edtEmail.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
                binding.tvShowError.text = ""
            } else if (it.isEmpty()) {
                binding.edtEmail.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.background_true)
                binding.tvShowError.text = ""
            } else {
                binding.edtEmail.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "\tEmail không hợp lệ!"
            }
        }

        handleEmail()
    }

    private fun handleEmail() {
        binding.btnNext.setOnSingClickListener {
            val email = binding.edtEmail.text.toString().trim()
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {

                viewModel.userList.observe(this) { users ->
                    var isEmailUsed = false
                    for (user in users) {
                        if (user.emailUser == email && user.idUser != viewModel.getId()) {
                            Toast.makeText(
                                requireContext(),
                                "Email đã được sử dụng",
                                Toast.LENGTH_SHORT
                            ).show()
                            isEmailUsed = true
                            break
                        }
                    }

                    if (!isEmailUsed) {
                        viewModel.userId.observe(this) {
                            it.emailUser = email
                            viewModel.updateUser(it)
                            viewModel.sendVerificationCode(email)
                            binding.edtEmail.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.background_true
                                )
                            binding.tvShowError.text = ""
                            findNavController().navigate(R.id.action_emailRequiredFragment_to_userNavigationFragment)
                        }
                    }
                }
            } else {
                binding.edtEmail.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "\tVui lòng nhập email!"
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


}