package com.phoint.facebooksimulation.ui.createInformationUser.homeTown

import android.app.AlertDialog
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.HomeTown
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.DialogDeleteBinding
import com.phoint.facebooksimulation.databinding.FragmentHomeTownBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class HomeTownFragment : BaseFragment<FragmentHomeTownBinding, HomeTownViewModel>() {
    private var bindingDialog: DialogDeleteBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingPermission: BottomDialogPermissionBinding? = null

    companion object {
        const val SEND_INFORMATION = "SEND_INFORMATION"
        const val EDIT_INFORMATION = "EDIT_INFORMATION"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_home_town
    }

    override fun viewModelClass(): Class<HomeTownViewModel> {
        return HomeTownViewModel::class.java
    }

    override fun initView() {
        viewModel.startLoading()
        viewModel.isFunctionalityVisible.observe(this) {
            if (it == true) {
                binding.llHomeTown.visibility = View.VISIBLE
            } else {
                binding.llHomeTown.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.getInformationHomeTown(viewModel.getIdPreferences())

        viewModel.informationUser.observe(this) {
            if (it.homeTown != null) {
                binding.edtHomeTown.setText(it.homeTown?.nameHomeTown)
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

        binding.abHomeTown.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    private fun updateAndDelete() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        bindingDialog = DialogDeleteBinding.inflate(layoutInflater)
        builder.setView(bindingDialog?.root)
        val alertDialog = builder.create()
        if (alertDialog != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window?.attributes = layoutParams
        }

        binding.edtHomeTown.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.btnSave.setOnSingClickListener {
                    alertDialog.show()
                    bindingDialog?.btnCancel?.setOnSingClickListener {
                        alertDialog.dismiss()
                    }

                    bindingDialog?.btnDelete?.setOnSingClickListener {
                        viewModel.deleteHomeTown(viewModel.getIdPreferences())
                        alertDialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
            } else {
                binding.btnSave.setOnSingClickListener { view ->
                    viewModel.informationUser.observe(this) { user ->
                        if (it.isNotEmpty()) {
                            val homeTown = HomeTown().apply {
                                homeTownId = System.currentTimeMillis()
                                nameHomeTown = binding.edtHomeTown.text.toString().trim()
                                privacy = binding.btnPermission.text.toString().trim()
                            }
                            user.homeTown = homeTown
                            viewModel.updateHomeTown(user)
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Vui lòng nhập thông tin",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun editPermission() {
        binding.btnSave.setOnSingClickListener {
            val homeTown = binding.edtHomeTown.text.toString().trim()
            if (homeTown.isNotEmpty()) {
                viewModel.informationUser.observe(this) { user ->
                    val homeTown = HomeTown().apply {
                        homeTownId = System.currentTimeMillis()
                        nameHomeTown = binding.edtHomeTown.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    user.homeTown = homeTown
                    viewModel.updateHomeTown(user)
                }
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập thông tin", Toast.LENGTH_SHORT)
                    .show()
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
            val homeTown = binding.edtHomeTown.text.toString().trim()
            if (homeTown.isNotEmpty()) {
                viewModel.informationUser.observe(this) { user ->
                    val homeTown = HomeTown().apply {
                        homeTownId = System.currentTimeMillis()
                        nameHomeTown = binding.edtHomeTown.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    user.homeTown = homeTown
                    viewModel.updateHomeTown(user)
                }
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập thông tin", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }
    }

}
