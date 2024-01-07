package com.phoint.facebooksimulation.ui.editPermissionUser

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentEditPermissionUserBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class EditPermissionUserFragment :
    BaseFragment<FragmentEditPermissionUserBinding, EditPermissionUserViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_edit_permission_user
    }

    override fun viewModelClass(): Class<EditPermissionUserViewModel> {
        return EditPermissionUserViewModel::class.java
    }

    override fun initView() {
        viewModel.getIdUser(viewModel.getIdPreferences())
        switchScreen()
        setDate()
        switchScreenWithKey()

        binding.abPermission.setOnSingClickLeft {
            findNavController().navigateUp()
        }
    }

    private fun switchScreenWithKey() {
        binding.btnEditWork.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_workExperienceFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }

        binding.btnEditHighSchool.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_highSchoolFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }

        binding.btnEditColleges.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_collegesFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }

        binding.btnEditCurrentCity.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_currentCityProvinceFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }

        binding.btnEditHomeTown.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_homeTownFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }

        binding.btnEditRelationShip.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_editPermissionUserFragment_to_relationshipFragment, bundleOf(
                    Pair("EDIT_INFORMATION", "EDIT_INFORMATION")
                )
            )
        }
    }

    private fun setDate() {
        viewModel.informationUser.observe(this) {

            if (it.workExperience != null) {
                binding.cbWork.text = it.workExperience?.nameWork
                binding.rlWork.visibility = View.VISIBLE
                binding.btnWork.visibility = View.GONE
            } else {
                binding.cbWork.text = ""
                binding.rlWork.visibility = View.GONE
                binding.btnWork.visibility = View.VISIBLE
            }

            if (it.highSchool != null) {
                binding.cbHighSchool.text = it.highSchool?.nameHighSchool
                binding.rlHighSchool.visibility = View.VISIBLE
                binding.btnHighSchool.visibility = View.GONE
            } else {
                binding.cbHighSchool.text = ""
                binding.rlHighSchool.visibility = View.GONE
                binding.btnHighSchool.visibility = View.VISIBLE
            }

            if (it.colleges != null) {
                binding.cbColleges.text = it.colleges?.nameColleges
                binding.rlColleges.visibility = View.VISIBLE
                binding.btnColleges.visibility = View.GONE
            } else {
                binding.cbColleges.text = ""
                binding.rlColleges.visibility = View.GONE
                binding.btnColleges.visibility = View.VISIBLE
            }

            if (it.currentCityAndProvince != null) {
                binding.cbCurrentCity.text = it.currentCityAndProvince?.nameCurrentProvinceOrCity
                binding.rlCurrentCity.visibility = View.VISIBLE
                binding.btnCurrentCity.visibility = View.GONE
            } else {
                binding.cbCurrentCity.text = ""
                binding.rlCurrentCity.visibility = View.GONE
                binding.btnCurrentCity.visibility = View.VISIBLE
            }

            if (it.homeTown != null) {
                binding.cbHomeTown.text = it.homeTown?.nameHomeTown
                binding.rlHomeTown.visibility = View.VISIBLE
                binding.btnHomeTown.visibility = View.GONE
            } else {
                binding.cbHomeTown.text = ""
                binding.rlHomeTown.visibility = View.GONE
                binding.btnHomeTown.visibility = View.VISIBLE
            }

            if (it.relationship != null) {
                binding.btnRelationShip.visibility = View.GONE
                binding.cbRelationShip.text = it.relationship?.nameRelationship
                binding.rlRelationShip.visibility = View.VISIBLE
            } else {
                binding.cbRelationShip.text = ""
                binding.rlRelationShip.visibility = View.GONE
                binding.btnRelationShip.visibility = View.VISIBLE
            }
        }
    }

    private fun switchScreen() {
        binding.btnWork.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_workExperienceFragment)
        }

        binding.btnHighSchool.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_highSchoolFragment)
        }

        binding.btnColleges.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_collegesFragment)
        }

        binding.btnCurrentCity.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_currentCityProvinceFragment)
        }

        binding.btnHomeTown.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_homeTownFragment)
        }

        binding.btnRelationShip.setOnSingClickListener {
            findNavController().navigate(R.id.action_editPermissionUserFragment_to_relationshipFragment)
        }
    }

}
