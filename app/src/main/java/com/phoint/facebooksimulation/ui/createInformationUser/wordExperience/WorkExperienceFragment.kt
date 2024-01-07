package com.phoint.facebooksimulation.ui.createInformationUser.wordExperience

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Work
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.DialogDeleteBinding
import com.phoint.facebooksimulation.databinding.FragmentWorkExperienceBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class WorkExperienceFragment :
    BaseFragment<FragmentWorkExperienceBinding, WorkExperienceViewModel>() {
    private var bindingDialog: DialogDeleteBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingPermission: BottomDialogPermissionBinding? = null

    companion object {
        const val SEND_INFORMATION = "SEND_INFORMATION"
        const val EDIT_INFORMATION = "EDIT_INFORMATION"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_work_experience
    }

    override fun viewModelClass(): Class<WorkExperienceViewModel> {
        return WorkExperienceViewModel::class.java
    }

    override fun initView() {
        viewModel.getInformationUser(viewModel.getIdPreferences())

        viewModel.startLoading()
        viewModel.isFunctionalityVisible.observe(this) {
            if (it == true) {
                binding.lnlWork.visibility = View.VISIBLE
            } else {
                binding.lnlWork.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.informationUser.observe(this) {
            if (it.workExperience?.nameWork != null) {
                binding.edtWork.setText(it.workExperience?.nameWork)
            }
        }

        val sendInformation = arguments?.getString("SEND_INFORMATION")
        val editInformation = arguments?.getString("EDIT_INFORMATION")
        if (editInformation == EDIT_INFORMATION) {
            updateAndDelete()
        } else if (sendInformation == SEND_INFORMATION) {
            editInformation()
        } else {
            editPermission()
        }

        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        binding.btnPermission.setOnSingClickListener {
            bindingPermission = BottomDialogPermissionBinding.inflate(layoutInflater)
            dialog?.show()

            bindingPermission?.btnPermissionPublic?.setOnSingClickListener {
                binding.btnPermission.text = bindingPermission?.tvPublic?.text.toString().trim()
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_public)
                val drawableRight =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                binding.btnPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
                dialog?.dismiss()
            }

            bindingPermission?.btnPermissionFriend?.setOnSingClickListener {
                binding.btnPermission.text = bindingPermission?.tvFriend?.text.toString().trim()
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_friend)
                val drawableRight =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                binding.btnPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
                dialog?.dismiss()
            }

            bindingPermission?.btnPermissionPrivate?.setOnSingClickListener {
                binding.btnPermission.text = bindingPermission?.tvPrivate?.text.toString().trim()
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_padlock)
                val drawableRight =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                binding.btnPermission.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    drawableRight,
                    null
                )
                dialog?.dismiss()
            }

            bindingPermission?.btnCancel?.setOnSingClickListener {
                dialog?.dismiss()
            }
            dialog?.setContentView(bindingPermission?.root!!)
        }

        binding.abWork.setOnSingClickLeft {
            findNavController().popBackStack()
        }

    }

    private fun updateAndDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        bindingDialog = DialogDeleteBinding.inflate(layoutInflater)
        builder.setView(bindingDialog?.root)
        val alertDialog = builder.create()

        binding.edtWork.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.btnSave.setOnSingClickListener {
                    alertDialog.show()
                    bindingDialog?.btnCancel?.setOnSingClickListener {
                        alertDialog.dismiss()
                    }

                    bindingDialog?.btnDelete?.setOnSingClickListener {
                        viewModel.deleteWork(viewModel.getIdPreferences())
                        alertDialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
            } else {
                binding.btnSave.setOnSingClickListener { view ->
                    val workExperience = binding.edtWork.text.toString().trim()
                    viewModel.informationUser.observe(this) { user ->
                        if (it.isNotEmpty()) {
                            val work = Work().apply {
                                workId = System.currentTimeMillis()
                                workUserId = viewModel.getIdPreferences()
                                nameWork = workExperience
                                privacy = binding.btnPermission.text.toString().trim()
                            }
                            user.workExperience = work
                            viewModel.updateUser(user)
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun editPermission() {
        binding.btnSave.setOnSingClickListener {
            viewModel.informationUser.observe(this) {
                val workExperience = binding.edtWork.text.toString().trim()
                if (workExperience.isNotEmpty()) {
                    val work = Work().apply {
                        workId = System.currentTimeMillis()
                        nameWork = workExperience
                        workUserId = viewModel.getIdPreferences()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    it.workExperience = work
                    viewModel.updateUser(it)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập thông tin", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }
    }

    private fun editInformation() {
        binding.btnSave.setOnSingClickListener {
            viewModel.informationUser.observe(this) {
                val workExperience = binding.edtWork.text.toString().trim()
                if (workExperience.isNotEmpty()) {
                    val work = Work().apply {
                        workId = System.currentTimeMillis()
                        workUserId = viewModel.getIdPreferences()
                        nameWork = workExperience
                        privacy = binding.btnPermission.text.toString().trim()
                    }

                    it.workExperience = work
                    viewModel.updateUser(it)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập thông tin", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }
    }

}
