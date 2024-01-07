package com.phoint.facebooksimulation.ui.friend

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.FragmentFriendBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class FriendFragment : BaseFragment<FragmentFriendBinding, FriendViewModel>() {
    private val userLists = ArrayList<User>()
    private var adapter: InvitationFriendAdapter? = null

    companion object {
        const val FRIEND_REQUEST_PENDING = 0
        const val FRIEND_REQUEST_ACCEPTED = 1
        const val FRIEND_REQUEST_REJECTED = 2
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_friend
    }

    override fun viewModelClass(): Class<FriendViewModel> {
        return FriendViewModel::class.java
    }

    override fun initView() {
        handleAdapter()
        handleActionBar()
        binding.btnFriendOfUser.setOnSingClickListener {
            findNavController().navigate(R.id.action_userNavigationFragment_to_userOfFriendFragment)
        }
    }

    private fun handleActionBar() {
        binding.abHome.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_userNavigationFragment_to_searchHistoryFragment)
        }
    }

    private fun handleAdapter() {
        adapter = InvitationFriendAdapter(userLists, viewModel.getIdPreferences())
        binding.rcvInvitationFriend.adapter = adapter
        viewModel.getAllFriendOfUser(viewModel.getIdPreferences())
        adapter?.setCurrentUserId(viewModel.getIdPreferences())

        viewModel.friendList.observe(this) { friendRequestList ->
            viewModel.userList.observe(this) { userList ->
                adapter?.setFriend(friendRequestList)
                val filteredFriendList = friendRequestList.filter { friendRequest ->
                    (friendRequest.receiverId == viewModel.getIdPreferences() && friendRequest.senderId != null && friendRequest.status == 0)
                }

                val friendIds = filteredFriendList.mapNotNull { friendRequest ->
                    if (friendRequest.receiverId == viewModel.getIdPreferences()) {
                        friendRequest.senderId
                    } else {
                        null
                    }
                }
                val friends = userList.filter { user ->
                    user.idUser in friendIds
                }
                adapter?.setFriendList(friends)

                if (friends.isEmpty()) {
                    binding.tvHideFriend.visibility = View.VISIBLE
                    binding.rcvInvitationFriend.visibility = View.GONE
                } else {
                    binding.tvHideFriend.visibility = View.GONE
                    binding.rcvInvitationFriend.visibility = View.VISIBLE
                }
            }
        }

        adapter?.onClickFriendConfirmation = { user ->
            viewModel.getFriendRequestByIdReceiver(user.idUser!!, viewModel.getIdPreferences())
            viewModel.friendRequestByIdReceiver.observe(this) { friendRequest ->
                friendRequest.status = FRIEND_REQUEST_ACCEPTED
                viewModel.updateFriendRequest(friendRequest)
                adapter?.isFriendBoolean(user, true)
            }
        }

        adapter?.onClickDeleteInvitationFriend = { user ->
            viewModel.getFriendRequestByIdReceiver(user.idUser!!, viewModel.getIdPreferences())
            viewModel.friendRequestByIdReceiver.observe(this) { friendRequest ->
                viewModel.deleteFriendRequest(friendRequest)
                adapter?.deleteItem(user)
            }
        }

        binding.btnSuggest.setOnSingClickListener {
            findNavController().navigate(R.id.action_userNavigationFragment_to_findNewFriendsFragment)
        }

        adapter?.onItemClick = {
            findNavController().navigate(
                R.id.action_userNavigationFragment_to_profileOtherUserFragment, bundleOf(
                    Pair("otherUser", it)
                )
            )
        }
    }

}

