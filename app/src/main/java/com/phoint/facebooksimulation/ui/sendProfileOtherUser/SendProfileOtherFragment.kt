package com.phoint.facebooksimulation.ui.sendProfileOtherUser

import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentSendProfileOtherBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.profileOtherUser.ProfileOtherUserFragment

class SendProfileOtherFragment :
    BaseFragment<FragmentSendProfileOtherBinding, SendProfileOtherViewModel>() {
    private var otherUser: User? = null
    override fun layoutRes(): Int {
        return R.layout.fragment_send_profile_other
    }

    override fun viewModelClass(): Class<SendProfileOtherViewModel> {
        return SendProfileOtherViewModel::class.java
    }

    override fun initView() {
        otherUser = arguments?.getParcelable("otherUser") as? User

        handlingUserDatabase()
        binding.abSendInformation.setOnSingClickLeft {
            findNavController().popBackStack()
        }
    }

    private fun handlingUserDatabase() {

        if (otherUser != null && otherUser?.workExperience != null && otherUser?.workExperience?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.tvWorkExperience.text = " ${otherUser?.workExperience?.nameWork}"
        } else {
            binding.tvWorkExperience.text = " Không có công việc nào để hiển thị"
        }

        if (otherUser != null && otherUser?.colleges != null && otherUser?.colleges?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.tvColleges.text = " ${otherUser?.colleges?.nameColleges}"
        } else {
            binding.tvColleges.text = " Không có trường cao đẳng nào để hiển thị"
        }

        if (otherUser != null && otherUser?.highSchool != null && otherUser?.highSchool?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.btnHighSchool.text = " ${otherUser?.highSchool?.nameHighSchool}"
        } else {
            binding.btnHighSchool.text = " Không có trường trung học nào để hiển thị"
        }

        if (otherUser != null && otherUser?.currentCityAndProvince != null && otherUser?.currentCityAndProvince?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.tvCurrentCity.text =
                " ${otherUser?.currentCityAndProvince?.nameCurrentProvinceOrCity}"
        } else {
            binding.tvCurrentCity.text = " Không có địa điểm nào để hiển thị nào để hiển thị"
        }

        if (otherUser != null && otherUser?.homeTown != null && otherUser?.homeTown?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.tvHomeTown.text = " ${otherUser?.homeTown?.nameHomeTown}"
        } else {
            binding.tvHomeTown.text = " Không có địa điểm nào để hiển thị"
        }

        if (otherUser != null && otherUser?.relationship != null && otherUser?.relationship?.privacy == ProfileOtherUserFragment.PUBLIC) {
            binding.tvRelationShip.text = "${otherUser?.relationship?.nameRelationship}"
        } else {
            binding.tvRelationShip.text = " Không có mối quan hệ nào để hiển thị"
        }
    }

}
