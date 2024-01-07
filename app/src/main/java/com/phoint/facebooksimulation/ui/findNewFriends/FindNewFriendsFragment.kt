package com.phoint.facebooksimulation.ui.findNewFriends

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.RemoveUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentFindNewFriendsBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.profileOtherUser.ProfileOtherUserFragment

class FindNewFriendsFragment :
    BaseFragment<FragmentFindNewFriendsBinding, FindNewFriendsViewModel>() {
    private var userList = ArrayList<User>()
    private var adapter: FindNewFriendAdapter? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_find_new_friends
    }

    override fun viewModelClass(): Class<FindNewFriendsViewModel> {
        return FindNewFriendsViewModel::class.java
    }

    override fun initView() {
        viewModel.getUsersWithFriendRequests(viewModel.getIdPreferences())
        adapter = FindNewFriendAdapter(userList)
        binding.rcvFindNewFriend.adapter = adapter
        adapter?.setCurrentUserId(viewModel.getIdPreferences())

        viewModel.userList.observe(this) { userList ->
            this.userList.clear()
            this.userList.addAll(userList)
            adapter?.notifyDataSetChanged()
        }

        binding.abFriendOfUser.setOnSingClickLeft {
            findNavController().popBackStack()
        }

        adapter?.onItemClick = {
            findNavController().navigate(
                R.id.action_findNewFriendsFragment_to_profileOtherUserFragment, bundleOf(
                    Pair("otherUser", it)
                )
            )
        }

        adapter?.onClickAddFriend = {
            val friendRequest = FriendRequest().apply {
                senderId = viewModel.getIdPreferences()
                receiverId = it.idUser
                time = System.currentTimeMillis()
                status = ProfileOtherUserFragment.FRIEND_REQUEST_PENDING
            }
            viewModel.insertFriendRequest(friendRequest)

            // Cập nhật trạng thái yêu cầu kết bạn trong friendRequestStatusMap
            adapter?.updateFriendRequestStatus(it.idUser!!, true)
            adapter?.notifyDataSetChanged()
        }

        adapter?.onClickRemoveFriend = {
            viewModel.getFriendRequestById(viewModel.getIdPreferences(), it.idUser ?: 0)
            viewModel.friendRequest.observe(this) { friendRequest ->
                viewModel.deleteFriendRequest(friendRequest)
                adapter?.updateFriendRequestStatus(it.idUser!!, false)
            }
        }

        adapter?.onClickRemove = { user ->
            val currentUserId = viewModel.getIdPreferences()
            val newRemoveUser = RemoveUser().apply {
                userIdRemoved = currentUserId
                userIdIsRemoved = user.idUser
            }
            viewModel.insertRemoveUser(newRemoveUser)
            adapter?.removeItem(user)
        }
    }
}


