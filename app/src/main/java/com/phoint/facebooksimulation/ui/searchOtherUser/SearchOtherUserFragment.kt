package com.phoint.facebooksimulation.ui.searchOtherUser

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentSearchOtherUserBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.widget.ActionBarLogin

class SearchOtherUserFragment :
    BaseFragment<FragmentSearchOtherUserBinding, SearchOtherUserViewModel>() {
    private var adapterUser: SearchOtherUserAdapter? = null
    private val userList = ArrayList<User>()
    private var lastSearchText = ""

    override fun layoutRes(): Int {
        return R.layout.fragment_search_other_user
    }

    override fun viewModelClass(): Class<SearchOtherUserViewModel> {
        return SearchOtherUserViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionBarLogin = ActionBarLogin(requireContext(), null)
        binding.abSearchOtherUser.addView(actionBarLogin)
        binding.abSearchOtherUser.requestSearchViewFocus()
    }

    override fun initView() {

        adapterUser = SearchOtherUserAdapter(userList)
        binding.rcvOtherUser.adapter = adapterUser

        binding.abSearchOtherUser.setOnClickQueryTextFocusChangeListener { searchText ->
            if (searchText.isNotEmpty()) {
                lastSearchText = searchText

                viewModel.searchNameUser(searchText)
                viewModel.getSearchResult(searchText).observe(this) { userList ->
                    if (userList.isNotEmpty()) {
                        adapterUser?.setData(userList)
                    } else {
                        adapterUser?.setData(emptyList())
                    }
                }
            } else {
                adapterUser?.setData(emptyList())
            }
        }

        adapterUser?.onItemClickOtherUser = { userOther ->
            val saveOtherUser = SaveOtherUser().apply {
                id = System.currentTimeMillis()
                userOwnerId = viewModel.getCurrentIdUser()
                nameOther = userOther.nameUser
                idUserOther = userOther.idUser
            }
            viewModel.insertSaveOtherUser(saveOtherUser)

            findNavController().navigate(
                R.id.action_searchOtherUserFragment_to_informationUserOtherFragment, bundleOf(
                    Pair("nameUser", userOther)
                )
            )
        }

        binding.abSearchOtherUser.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        val userOther = arguments?.getString("nameUser") ?: ""
        if (userOther.isNotEmpty()) {
            binding.abSearchOtherUser.setTextSearch(userOther)

            viewModel.searchNameUser(userOther)
            viewModel.getSearchResult(userOther).observe(this) { userList ->
                if (userList.isNotEmpty()) {
                    adapterUser?.setData(userList)
                } else {
                    adapterUser?.setData(emptyList())
                }
            }
        } else {
            if (lastSearchText.isNotEmpty()) {
                binding.abSearchOtherUser.setTextSearch(lastSearchText)

                viewModel.searchNameUser(lastSearchText)
                viewModel.getSearchResult(lastSearchText).observe(this) { userList ->
                    if (userList.isNotEmpty()) {
                        adapterUser?.setData(userList)
                    } else {
                        adapterUser?.setData(emptyList())
                    }
                }
            }
        }
    }

}

