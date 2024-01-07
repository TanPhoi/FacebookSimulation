package com.phoint.facebooksimulation.ui.createInformationUser.createYearOld

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentYearOldBinding
import com.phoint.facebooksimulation.databinding.LayoutDialogYearOldBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class YearOldFragment : BaseFragment<FragmentYearOldBinding, YearOldViewModel>() {
    private var bindingDialog: LayoutDialogYearOldBinding? = null
    private var isObservingUserID = false
    override fun layoutRes(): Int {
        return R.layout.fragment_year_old
    }

    override fun viewModelClass(): Class<YearOldViewModel> {
        return YearOldViewModel::class.java
    }

    override fun initView() {

        viewModel.getIDUser(viewModel.getId())

        handleYearOld()

        binding.actionBar.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().navigate(R.id.action_yearOldFragment_to_loginFragment)
        }

    }

    private fun handleYearOld() {
        binding.btnNext.setOnSingClickListener {
            val yearOld = binding.edtYearOld.text.toString().trim()
            if (yearOld.isNotEmpty()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                bindingDialog = LayoutDialogYearOldBinding.inflate(layoutInflater)
                builder.setView(bindingDialog!!.root)

                val alertDialog = builder.create()
                viewModel.userId.observe(this) {
                    bindingDialog?.tvTitle?.text =
                        "Bạn đang đặt ngày sinh của mình thành ${it.dateOfBirthUser}"
                }

                bindingDialog?.btnCancel?.setOnSingClickListener {
                    alertDialog.dismiss()
                }

                bindingDialog?.btnOk?.setOnSingClickListener {
                    if (!isObservingUserID) {
                        viewModel.userId.observe(this) {
                            it.yearOldUser = yearOld
                            viewModel.updateUser(it)
                            findNavController().navigate(R.id.action_yearOldFragment_to_genderFragment)
                            alertDialog.dismiss()
                        }
                        isObservingUserID = true
                    }
                }

                if (alertDialog.window != null) {
                    alertDialog?.window?.setBackgroundDrawable(ColorDrawable(0))
                }
                alertDialog.show()
            } else {
                binding.edtYearOld.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.cardview_error_border)
                binding.tvShowError.text = "Vui lòng nhập thông tin!"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.userId.removeObservers(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isObservingUserID = false
        handleYearOld()
    }
}
