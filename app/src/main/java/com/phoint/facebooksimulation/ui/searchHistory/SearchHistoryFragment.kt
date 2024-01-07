package com.phoint.facebooksimulation.ui.searchHistory

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.databinding.FragmentSearchHistoryBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class SearchHistoryFragment : BaseFragment<FragmentSearchHistoryBinding, SearchHistoryViewModel>() {
    private var adapterSaveUser: SaveUserAdapter? = null
    private val saveOtherUserList = ArrayList<SaveOtherUser>()

    companion object {
        const val KEY_SAVE_OTHER_USER = "KEY_SAVE_OTHER_USER"
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_search_history
    }

    override fun viewModelClass(): Class<SearchHistoryViewModel> {
        return SearchHistoryViewModel::class.java
    }

    override fun initView() {
        handleAdapterSaveUser()

        binding.abSearchHistory.setOnSearchClickListener {
            findNavController().navigate(R.id.action_searchHistoryFragment_to_searchOtherUserFragment)
        }

        binding.abSearchHistory.setOnBackClick {
            findNavController().popBackStack()
        }
    }

    private fun handleAdapterSaveUser() {
        viewModel.getAllSaveOtherOwnerId(viewModel.getCurrentIdUser())
        adapterSaveUser = SaveUserAdapter(saveOtherUserList)
        binding.rcvOtherUser.adapter = adapterSaveUser

        viewModel.saveOtherUserList.observe(this) {
            saveOtherUserList.clear()
            saveOtherUserList.addAll(it)
            adapterSaveUser?.notifyDataSetChanged()
        }

        viewModel.userList.observe(this) {
            adapterSaveUser?.setUsers(it)
            adapterSaveUser?.notifyDataSetChanged()
        }

        adapterSaveUser?.onDeleteOtherUser = {
            adapterSaveUser?.removeItem(it)
            viewModel.deleteSaveOtherUser(it)
        }

        adapterSaveUser?.onItemClickOtherUser = { saveOtherUser ->
            findNavController().navigate(
                R.id.action_searchHistoryFragment_to_informationUserOtherFragment, bundleOf(
                    Pair("saveOtherUser", saveOtherUser),
                    Pair(KEY_SAVE_OTHER_USER, KEY_SAVE_OTHER_USER)
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        saveOtherUserList.clear()
        viewModel.getAllSaveOtherOwnerId(viewModel.getCurrentIdUser())
    }

}
