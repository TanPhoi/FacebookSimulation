package com.phoint.facebooksimulation.ui.createInformationUser.createGender

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentGenderBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class GenderFragment : BaseFragment<FragmentGenderBinding, GenderViewModel>() {
    private var isObservingUserID = false
    override fun layoutRes(): Int {
        return R.layout.fragment_gender
    }

    override fun viewModelClass(): Class<GenderViewModel> {
        return GenderViewModel::class.java
    }

    override fun initView() {
        viewModel.getIdUser(viewModel.getId())

        handleGender()

        binding.actionBar.setOnBackClick {
            findNavController().popBackStack()
        }

        binding.btnBackLogin.setOnSingClickListener {
            findNavController().navigate(R.id.action_genderFragment_to_loginFragment)
        }
    }

    private fun handleGender() {
        binding.rdgGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdbGirl -> {
                    viewModel.boySelected.value = false
                    viewModel.girlSelected.value = true
                    viewModel.customSelected.value = false
                }

                R.id.rdbBoy -> {
                    viewModel.boySelected.value = true
                    viewModel.girlSelected.value = false
                    viewModel.customSelected.value = false
                }

                R.id.rdbCustom -> {
                    viewModel.boySelected.value = false
                    viewModel.girlSelected.value = false
                    viewModel.customSelected.value = true
                }

                else -> {
                    viewModel.boySelected.value = false
                    viewModel.girlSelected.value = false
                    viewModel.customSelected.value = false
                }
            }
        }

        binding.btnNext.setOnSingClickListener {
            val selectedId = binding.rdgGender.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Please select an option!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.girlSelected.observe(this) { bGirl ->
                    if (bGirl == true) {
                        binding.rdbGirl.text = "Nữ"
                        val girl = binding.rdbGirl.text.toString()
                        if (!isObservingUserID) {
                            viewModel.userId.observe(this) {
                                it.genderUser = girl
                                viewModel.updateUser(it)
                                findNavController().navigate(R.id.action_genderFragment_to_phoneFragment)
                            }
                            isObservingUserID = true
                        }
                    } else if (bGirl == false) {
                        viewModel.boySelected.observe(this) { bBoy ->
                            if (bBoy == true) {
                                binding.rdbBoy.text = "Nam"
                                val boy = binding.rdbBoy.text.toString()
                                if (!isObservingUserID) {
                                    viewModel.userId.observe(this) {
                                        it.genderUser = boy
                                        viewModel.updateUser(it)
                                        findNavController().navigate(R.id.action_genderFragment_to_phoneFragment)
                                    }
                                    isObservingUserID = true
                                }
                            } else if (bBoy == false) {
                                viewModel.customSelected.observe(this) { bCustom ->
                                    if (bCustom == true) {
                                        binding.rdbCustom.text = "Khác"
                                        val custom = binding.rdbCustom.text.toString()
                                        if (!isObservingUserID) {
                                            viewModel.userId.observe(this) {
                                                it.genderUser = custom
                                                viewModel.updateUser(it)
                                                findNavController().navigate(R.id.action_genderFragment_to_phoneFragment)
                                            }
                                            isObservingUserID = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isObservingUserID) {
            viewModel.userId.removeObservers(this)
            viewModel.girlSelected.removeObservers(this)
            viewModel.boySelected.removeObservers(this)
            viewModel.customSelected.removeObservers(this)
        }
    }

    override fun onResume() {
        super.onResume()
        isObservingUserID = false
        handleGender()
    }
}
