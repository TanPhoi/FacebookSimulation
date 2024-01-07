package com.phoint.facebooksimulation.ui.createInformationUser.currentCityAndProvince

import android.app.AlertDialog
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.CurrentProvinceOrCity
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.DialogDeleteBinding
import com.phoint.facebooksimulation.databinding.FragmentCurrentCityProvinceBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class CurrentCityProvinceFragment :
    BaseFragment<FragmentCurrentCityProvinceBinding, CurrentCityProvinceViewModel>() {
    private var bindingDialog: DialogDeleteBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingPermission: BottomDialogPermissionBinding? = null
    private var isDialogShowing = false

    companion object {
        const val SEND_INFORMATION = "SEND_INFORMATION"
        const val EDIT_INFORMATION = "EDIT_INFORMATION"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_current_city_province
    }

    override fun viewModelClass(): Class<CurrentCityProvinceViewModel> {
        return CurrentCityProvinceViewModel::class.java
    }

    override fun initView() {
        viewModel.startLoading()
        viewModel.isFunctionalityVisible.observe(this) {
            if (it == true) {
                binding.llCurrentCity.visibility = View.VISIBLE
            } else {
                binding.llCurrentCity.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }
        viewModel.getInformationCurrent(viewModel.getIdPreferences())
        viewModel.informationUser.observe(this) {
            binding.edtCurrentCity.setText(it.currentCityAndProvince?.nameCurrentProvinceOrCity)
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

        if (isDialogShowing) {
            val drawableRight = ContextCompat.getDrawable(requireContext(), R.drawable.ic_add)
            binding.btnPermission.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawableRight,
                null
            )
        } else {
            val drawableRight =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
            binding.btnPermission.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                drawableRight,
                null
            )
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

        binding.abCurrentCity.setOnSingClickLeft {
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

        binding.edtCurrentCity.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.btnSave.setOnSingClickListener {
                    alertDialog.show()
                    bindingDialog?.btnCancel?.setOnSingClickListener {
                        alertDialog.dismiss()
                    }
                    bindingDialog?.btnDelete?.setOnSingClickListener {
                        viewModel.deleteCurrentCity(viewModel.getIdPreferences())
                        alertDialog.dismiss()
                        findNavController().popBackStack()
                    }
                }
            } else {
                binding.btnSave.setOnSingClickListener { view ->
                    viewModel.informationUser.observe(this) { user ->
                        if (it.isNotEmpty()) {
                            val currentProvinceOrCity = CurrentProvinceOrCity().apply {
                                currentProvinceOrCityId = System.currentTimeMillis()
                                nameCurrentProvinceOrCity =
                                    binding.edtCurrentCity.text.toString().trim()
                                privacy = binding.btnPermission.text.toString().trim()
                            }
                            user.currentCityAndProvince = currentProvinceOrCity
                            viewModel.updateCurrentCity(user)
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
            val currentCity = binding.edtCurrentCity.text.toString().trim()
            if (currentCity.isNotEmpty()) {
                viewModel.informationUser.observe(this) { user ->
                    val currentProvinceOrCity = CurrentProvinceOrCity().apply {
                        currentProvinceOrCityId = System.currentTimeMillis()
                        nameCurrentProvinceOrCity = binding.edtCurrentCity.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    user.currentCityAndProvince = currentProvinceOrCity
                    viewModel.updateCurrentCity(user)
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
            val currentCity = binding.edtCurrentCity.text.toString().trim()
            if (currentCity.isNotEmpty()) {
                viewModel.informationUser.observe(this) { user ->
                    val currentProvinceOrCity = CurrentProvinceOrCity().apply {
                        currentProvinceOrCityId = System.currentTimeMillis()
                        nameCurrentProvinceOrCity = binding.edtCurrentCity.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    user.currentCityAndProvince = currentProvinceOrCity
                    viewModel.updateCurrentCity(user)
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
