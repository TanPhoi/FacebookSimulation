package com.phoint.facebooksimulation.ui.sendInformation

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentSendInformationBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SendInformationFragment :
    BaseFragment<FragmentSendInformationBinding, SendInformationViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_send_information
    }

    override fun viewModelClass(): Class<SendInformationViewModel> {
        return SendInformationViewModel::class.java
    }

    @SuppressLint("ResourceAsColor")
    override fun initView() {
        val id = viewModel.getIdPreferences()
        viewModel.getIdUser(id)

        viewModel.startLoading()
        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.userId.observe(this) {
            if (it.workExperience != null) {
                binding.btnWorkExperience.text = it.workExperience?.nameWork
                binding.btnWorkExperience.setTextColor(Color.WHITE)
            } else {
                binding.btnWorkExperience.text = "Thêm kinh nghiệm làm việc"
            }

            if (it.colleges != null) {
                binding.btnColleges.text = it.colleges?.nameColleges
                binding.btnColleges.setTextColor(Color.WHITE)
            } else {
                binding.btnColleges.text = "Thêm trường cao đẳng"
            }

            if (it.highSchool != null) {
                binding.btnHighSchool.text = it.highSchool?.nameHighSchool
                binding.btnHighSchool.setTextColor(Color.WHITE)
            } else {
                binding.btnHighSchool.text = "Thêm trường trung học"
            }

            if (it.currentCityAndProvince != null) {
                binding.btnCurrentCity.text = it.currentCityAndProvince?.nameCurrentProvinceOrCity
                binding.btnCurrentCity.setTextColor(Color.WHITE)
            } else {
                binding.btnCurrentCity.text = "Thêm tỉnh thành phố hiện tại"
            }

            if (it.homeTown != null) {
                binding.btnHomeTown.text = it.homeTown?.nameHomeTown
                binding.btnHomeTown.setTextColor(Color.WHITE)
            } else {
                binding.btnHomeTown.text = "Thêm quê quán"
            }

            if (it.relationship != null) {
                binding.btnRelationShip.text = it.relationship?.nameRelationship
                binding.btnRelationShip.setTextColor(Color.WHITE)
            } else {
                binding.btnRelationShip.text = "Thêm mối quan hệ"
            }

            if (it.emailUser != null) {
                binding.btnEmail.text = it.emailUser
                binding.btnEmail.setTextColor(Color.WHITE)
            } else {
                binding.btnEmail.text = "Thêm Email"
            }

            if (it.genderUser != null) {
                binding.btnGender.text = it.genderUser
            }

            if (it.dateOfBirthUser != null) {
                binding.btnDateOfBirth.text = it.dateOfBirthUser
            }

            if (it.phoneUser != null) {
                binding.btnPhone.text = it.phoneUser
            }
        }

        binding.btnWorkExperience.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_workExperienceFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.btnColleges.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_collegesFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.btnHighSchool.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_highSchoolFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.btnCurrentCity.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_currentCityProvinceFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.btnHomeTown.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_homeTownFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.btnRelationShip.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_sendInformationFragment_to_relationshipFragment, bundleOf(
                    Pair("SEND_INFORMATION", "SEND_INFORMATION")
                )
            )
        }

        binding.abSendInformation.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

}
