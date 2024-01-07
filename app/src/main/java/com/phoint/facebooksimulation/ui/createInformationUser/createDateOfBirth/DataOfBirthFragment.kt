package com.phoint.facebooksimulation.ui.createInformationUser.createDateOfBirth

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentDataOfBirthBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener
import java.util.Calendar

class DataOfBirthFragment : BaseFragment<FragmentDataOfBirthBinding, DataOfBirthViewModel>() {
    private var age: Int = 0
    private var dayOfMonth: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var presentTime: String? = null
    private var isObservingUserID = false
    private val calendar = Calendar.getInstance()
    private var mDataSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_data_of_birth
    }

    override fun viewModelClass(): Class<DataOfBirthViewModel> {
        return DataOfBirthViewModel::class.java
    }

    override fun initView() {
        createDialog()

        viewModel.getId(viewModel.getId())
        viewModel.userID.observe(this) {
            if (it.dateOfBirthUser != null) {
                binding.tvDateOfBirth.text = it.dateOfBirthUser
            }
        }

        binding.actionBar.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().navigate(R.id.action_dataOfBirthFragment_to_loginFragment)
        }

        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        presentTime = "Ngày $dayOfMonth tháng ${month + 1}, $year"
        binding.tvDateOfBirth.text = presentTime

        binding.btnDateOfBirth.setOnSingClickListener {
            val dialog = DatePickerDialog(
                requireContext(), R.style.MyDatePickerDialog,
                mDataSetListener,
                year, month, dayOfMonth
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
            dialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            dialog.show()
        }

        mDataSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = "Ngày $dayOfMonth tháng ${month + 1}, $year"
            binding.tvDateOfBirth.text = selectedDate

            val calendarSelected = Calendar.getInstance()
            calendarSelected.set(year, month, dayOfMonth)
            age = calculateAge(calendarSelected)
        }
    }

    @SuppressLint("ResourceType", "FragmentLiveDataObserve")
    private fun createDialog() {
        binding.btnNext.setOnSingClickListener {
            val dateOfBirth = binding.tvDateOfBirth.text.toString()
            if (dateOfBirth.isNotEmpty() && age > 1) {
                viewModel.getId(viewModel.getId())
                if (!isObservingUserID) {
                    viewModel.userID.observe(this) {
                        it.dateOfBirthUser = dateOfBirth
                        viewModel.updateUser(it)
                        findNavController().navigate(R.id.action_dataOfBirthFragment_to_yearOldFragment)
                    }
                    isObservingUserID = true
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn đúng ngày sinh của bạn",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun calculateAge(birthDate: Calendar): Int {
        val currentDate = Calendar.getInstance()
        var age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.userID.removeObservers(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isObservingUserID = false
    }
}
