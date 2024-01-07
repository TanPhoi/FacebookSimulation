package com.phoint.facebooksimulation.ui.informationUserOther

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.BottomDialogConfirmFriendBinding
import com.phoint.facebooksimulation.databinding.BottomDialogDeleteFriendBinding
import com.phoint.facebooksimulation.databinding.FragmentInformationUserOtherBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.profileOtherUser.ProfileOtherUserFragment
import com.phoint.facebooksimulation.ui.searchHistory.SearchHistoryFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class InformationUserOtherFragment :
    BaseFragment<FragmentInformationUserOtherBinding, InformationUserOtherViewModel>() {
    private var adapter: InformationUserOtherAdapter? = null
    private var userList = ArrayList<User>()
    private var bindingConfirmFriend: BottomDialogConfirmFriendBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingDeleteFriend: BottomDialogDeleteFriendBinding? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_information_user_other
    }

    override fun viewModelClass(): Class<InformationUserOtherViewModel> {
        return InformationUserOtherViewModel::class.java
    }

    override fun initView() {
        val userOther = arguments?.getParcelable("nameUser") ?: User()
        val saveOtherUser = arguments?.getParcelable("saveOtherUser") ?: SaveOtherUser()
        val key = arguments?.getString("KEY_SAVE_OTHER_USER") ?: ""

        adapter = InformationUserOtherAdapter(userList)
        binding.rcvInformationUserOther.adapter = adapter

        adapter?.setCurrentUserId(viewModel.getCurrentIdUser())

        if (SearchHistoryFragment.KEY_SAVE_OTHER_USER == key) {
            viewModel.getUserById(saveOtherUser.idUserOther!!)
            viewModel.userById.observe(this) { user ->
                viewModel.getFriendOfUser(viewModel.getCurrentIdUser(), user.idUser!!)

                viewModel.friendList.observe(this) { friendRequestList ->
                    adapter?.setFriendRequestLists(friendRequestList)
                }

                binding.abInformationUserOther.setTextUser(user.nameUser!!)

                binding.abInformationUserOther.setOnSearchClickListener {
                    if (SearchHistoryFragment.KEY_SAVE_OTHER_USER == key) {
                        findNavController().navigate(
                            R.id.action_informationUserOtherFragment_to_searchOtherUserFragment,
                            bundleOf(
                                Pair("nameUser", user)
                            )
                        )
                    } else {
                        findNavController().popBackStack()
                    }
                }

                if (user != null) {
                    viewModel.searchNameUser(user.nameUser!!)
                    viewModel.getSearchResult(user.nameUser!!).observe(this) {
                        if (it.isNotEmpty()) {
                            adapter?.setData(it)
                            binding.tvShow.visibility = View.VISIBLE
                        } else {
                            adapter?.setData(emptyList())
                            binding.tvShow.visibility = View.GONE
                        }
                    }

                } else {
                    adapter?.setData(emptyList())
                    binding.tvShow.visibility = View.GONE
                }

                binding.abInformationUserOther.setOnBackClick {
                    findNavController().popBackStack()
                }

                adapter?.onItemClick = {
                    val saveOtherUser = SaveOtherUser().apply {
                        id = System.currentTimeMillis()
                        userOwnerId = viewModel.getCurrentIdUser()
                        nameOther = user.nameUser
                        idUserOther = user.idUser
                        isAvatar = true
                    }
                    viewModel.insertSaveOtherUser(saveOtherUser)

                    if (it.idUser == viewModel.getCurrentIdUser()) {
                        findNavController().navigate(R.id.action_informationUserOtherFragment_to_profileFragment)

                    } else {
                        findNavController().navigate(
                            R.id.action_informationUserOtherFragment_to_profileOtherUserFragment,
                            bundleOf(Pair("otherUser", it))
                        )
                    }
                }

                adapter?.onClickAddFriend = {
                    val newFriend = FriendRequest().apply {
                        senderId = viewModel.getCurrentIdUser()
                        receiverId = it.idUser
                        time = System.currentTimeMillis()
                        status = 0
                    }
                    viewModel.insertFriendRequest(newFriend)
                }

                adapter?.onClickCancelFriendRequest = { user ->
                    viewModel.getFriendRequestById(viewModel.getCurrentIdUser(), user.idUser ?: 0)
                    viewModel.friendRequest.observe(this) {
                        viewModel.deleteFriendRequest(it)
                    }
                }

                adapter?.onClickDeleteFriendRequest = {
                    handleDialogDeleteFriend(it)
                }

                adapter?.onClickFeedback = { user ->
                    handleDialogFriend(user)
                }
            }

        } else {
            viewModel.getFriendOfUser(viewModel.getCurrentIdUser(), userOther.idUser!!)
            binding.abInformationUserOther.setTextUser(userOther.nameUser!!)
            binding.abInformationUserOther.setOnSearchClickListener {
                if (SearchHistoryFragment.KEY_SAVE_OTHER_USER == key) {
                    findNavController().navigate(
                        R.id.action_informationUserOtherFragment_to_searchOtherUserFragment,
                        bundleOf(
                            Pair("nameUser", userOther)
                        )
                    )
                } else {
                    findNavController().popBackStack()
                }
            }

            viewModel.friendList.observe(this) { friendRequestList ->
                adapter?.setFriendRequestLists(friendRequestList)
            }

            if (userOther != null) {
                viewModel.searchNameUser(userOther.nameUser!!)
                viewModel.getSearchResult(userOther.nameUser!!).observe(this) {
                    if (it.isNotEmpty()) {
                        adapter?.setData(it)
                        binding.tvShow.visibility = View.VISIBLE
                    } else {
                        adapter?.setData(emptyList())
                        binding.tvShow.visibility = View.GONE
                    }
                }
            } else {
                adapter?.setData(emptyList())
                binding.tvShow.visibility = View.GONE
            }

            binding.abInformationUserOther.setOnBackClick {
                findNavController().popBackStack()
            }

            adapter?.onItemClick = {
                val saveOtherUser = SaveOtherUser().apply {
                    id = System.currentTimeMillis()
                    userOwnerId = viewModel.getCurrentIdUser()
                    nameOther = userOther.nameUser
                    idUserOther = userOther.idUser
                    isAvatar = true
                }
                viewModel.insertSaveOtherUser(saveOtherUser)

                if (it.idUser == viewModel.getCurrentIdUser()) {
                    findNavController().navigate(R.id.action_informationUserOtherFragment_to_profileFragment)

                } else {
                    findNavController().navigate(
                        R.id.action_informationUserOtherFragment_to_profileOtherUserFragment,
                        bundleOf(Pair("otherUser", it))
                    )
                }
            }

            adapter?.onClickAddFriend = {
                val newFriend = FriendRequest().apply {
                    senderId = viewModel.getCurrentIdUser()
                    receiverId = it.idUser
                    time = System.currentTimeMillis()
                    status = 0
                }
                viewModel.insertFriendRequest(newFriend)
            }

            adapter?.onClickCancelFriendRequest = { user ->
                viewModel.getFriendRequestById(viewModel.getCurrentIdUser(), user.idUser ?: 0)
                viewModel.friendRequest.observe(this) {
                    viewModel.deleteFriendRequest(it)
                }
            }

            adapter?.onClickDeleteFriendRequest = {
                handleDialogDeleteFriend(it)
            }

            adapter?.onClickFeedback = { user ->
                handleDialogFriend(user)
            }

        }
    }

    private fun handleDialogDeleteFriend(user: User) {
        bindingDeleteFriend = BottomDialogDeleteFriendBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        dialog?.show()

        bindingDeleteFriend?.btnDeleteFriend?.setOnSingClickListener {
            viewModel.getFriendRequestByIdDelete(user.idUser ?: 0, viewModel.getCurrentIdUser())
            viewModel.friendRequestByIdDelete.observe(this) {
                viewModel.deleteFriendRequest(it)
                adapter?.isFriendDelete(true)
            }
            dialog?.dismiss()
        }
        dialog?.setContentView(bindingDeleteFriend?.root!!)
    }

    private fun handleDialogFriend(user: User) {
        bindingConfirmFriend = BottomDialogConfirmFriendBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        dialog?.show()

        bindingConfirmFriend?.btnConfirmFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(user.idUser ?: 0, viewModel.getCurrentIdUser())
            viewModel.friendRequest.observe(this) {
                it.status = ProfileOtherUserFragment.FRIEND_REQUEST_ACCEPTED
                viewModel.updateFriendRequest(it)
            }
            dialog?.dismiss()
        }

        bindingConfirmFriend?.btnDeleteFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(user.idUser ?: 0, viewModel.getCurrentIdUser())
            viewModel.friendRequest.observe(this) {
                viewModel.deleteFriendRequest(it)
            }
            dialog?.dismiss()
        }
        dialog?.setContentView(bindingConfirmFriend?.root!!)
    }

}
