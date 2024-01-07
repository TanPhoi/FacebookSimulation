package com.phoint.facebooksimulation.ui.createInformationUser.relationship

import android.app.AlertDialog
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Relationship
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.BottomDialogRelationshipBinding
import com.phoint.facebooksimulation.databinding.DialogDeleteBinding
import com.phoint.facebooksimulation.databinding.FragmentRelationshipBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class RelationshipFragment : BaseFragment<FragmentRelationshipBinding, RelationshipViewModel>() {
    private var dialog: BottomSheetDialog? = null
    private var bindingDialog: BottomDialogRelationshipBinding? = null
    private var bindingDialogCenter: DialogDeleteBinding? = null
    private var bindingPermission: BottomDialogPermissionBinding? = null

    companion object {
        const val SEND_INFORMATION = "SEND_INFORMATION"
        const val EDIT_INFORMATION = "EDIT_INFORMATION"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_relationship
    }

    override fun viewModelClass(): Class<RelationshipViewModel> {
        return RelationshipViewModel::class.java
    }

    override fun initView() {
        viewModel.startLoading()
        viewModel.isFunctionalityVisible.observe(this) {
            if (it == true) {
                binding.llRelationship.visibility = View.VISIBLE
            } else {
                binding.llRelationship.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.getInformationRelationship(viewModel.getIdPreferences())
        viewModel.informationUser.observe(this) {
            if (it.relationship != null) {
                binding.btnRelationShip.text = it.relationship?.nameRelationship
            }
        }

        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        binding.btnRelationShip.setOnSingClickListener {
            dialog?.show()
            createDialog()
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

        binding.abRelationship.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    private fun updateAndDelete() {
        binding.btnDelete.visibility = View.VISIBLE
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        bindingDialogCenter = DialogDeleteBinding.inflate(layoutInflater)
        builder.setView(bindingDialogCenter?.root)

        val alertDialog = builder.create()
        if (alertDialog != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            alertDialog.window?.attributes = layoutParams
        }

        binding.btnDelete.setOnSingClickListener {
            alertDialog.show()
        }

        bindingDialogCenter?.btnCancel?.setOnSingClickListener {
            alertDialog.dismiss()
        }

        bindingDialogCenter?.btnDelete?.setOnSingClickListener {
            viewModel.deleteRelationship(viewModel.getIdPreferences())
            alertDialog.dismiss()
            findNavController().popBackStack()
        }

        binding.btnSave.setOnSingClickListener { view ->
            viewModel.informationUser.observe(this) {
                if (it != null) {
                    val relationship = Relationship().apply {
                        relationshipId = System.currentTimeMillis()
                        nameRelationship = binding.btnRelationShip.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    it.relationship = relationship
                    viewModel.updateRelationship(it)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun editPermission() {
        binding.btnDelete.visibility = View.GONE
        binding.btnSave.setOnSingClickListener {
            val relationship = binding.btnRelationShip.text.toString().trim()
            if (relationship.isNotEmpty()) {
                viewModel.informationUser.observe(this) {
                    val relationship = Relationship().apply {
                        relationshipId = System.currentTimeMillis()
                        nameRelationship = binding.btnRelationShip.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    it.relationship = relationship
                    viewModel.updateRelationship(it)
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
        binding.btnDelete.visibility = View.GONE
        binding.btnSave.setOnSingClickListener {
            val relationship = binding.btnRelationShip.text.toString().trim()
            if (relationship.isNotEmpty()) {
                viewModel.informationUser.observe(this) {
                    val relationship = Relationship().apply {
                        relationshipId = System.currentTimeMillis()
                        nameRelationship = binding.btnRelationShip.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    it.relationship = relationship
                    viewModel.updateRelationship(it)
                }
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }
    }

    private fun createDialog() {
        bindingDialog = BottomDialogRelationshipBinding.inflate(layoutInflater)

        bindingDialog?.tvAlone?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvAlone?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvDating?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvDating?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvEngaged?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvEngaged?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvMarried?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvMarried?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvRegisteredCohabitation?.setOnSingClickListener {
            binding.btnRelationShip.text =
                bindingDialog?.tvRegisteredCohabitation?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvLiveTogether?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvLiveTogether?.text.toString().trim()
            dialog?.dismiss()
        }


        bindingDialog?.tvLearning?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvLearning?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvComplicated?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvComplicated?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvSeparated?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvSeparated?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvDivorced?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvDivorced?.text.toString().trim()
            dialog?.dismiss()
        }

        bindingDialog?.tvWow?.setOnSingClickListener {
            binding.btnRelationShip.text = bindingDialog?.tvWow?.text.toString().trim()
            dialog?.dismiss()
        }

        dialog?.setContentView(bindingDialog?.root!!)
    }
}
