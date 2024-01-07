package com.phoint.facebooksimulation.ui.seenFriendsOfFriend

import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.BottomDialogChangeFriendBinding
import com.phoint.facebooksimulation.databinding.FragmentSeenFriendsOfFriendBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.userOfFriend.FriendOfUserAdapter
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SeenFriendsOfFriendFragment :
    BaseFragment<FragmentSeenFriendsOfFriendBinding, SeenFriendsOfFriendViewModel>() {
    private val userList = ArrayList<User>()
    private var adapter: FriendOfUserAdapter? = null
    private var dialog: BottomSheetDialog? = null
    private var user: User? = null
    private var bindingFriend: BottomDialogChangeFriendBinding? = null
    private var isFriendRequestSent = false

    override fun layoutRes(): Int {
        return R.layout.fragment_seen_friends_of_friend
    }

    override fun viewModelClass(): Class<SeenFriendsOfFriendViewModel> {
        return SeenFriendsOfFriendViewModel::class.java
    }

    override fun initView() {
        user = arguments?.getParcelable("user") as? User
        handleAdapter()
        handleMenuFriend()
        handleActionBar()
    }

    private fun handleActionBar() {
        binding.abFriendOfUser.setTitle("Bạn bè của ${user?.nameUser}")

        binding.abFriendOfUser.setOnSingClickLeft {
            findNavController().popBackStack()
        }

        binding.abFriendOfUser.setOnSingClickImgLeft {
            findNavController().navigate(R.id.action_userOfFriendFragment_to_findNewFriendsFragment)
        }
    }

    private fun handleMenuFriend() {
        dialog = BottomSheetDialog(requireContext())
        bindingFriend = BottomDialogChangeFriendBinding.inflate(layoutInflater)
        dialog?.create()

        adapter?.onClickMenu = { user ->
            bindingFriend?.tvUnfriend?.text = "Hủy kết bạn với ${user.nameUser}"
            bindingFriend?.tvSeeFriend?.text = "Xem tất cả bạn bè của ${user.nameUser}"
            bindingFriend?.tvUnfollow?.text = "Bỏ theo dõi ${user.nameUser}"

            dialog?.show()
            bindingFriend?.btnSeeFriend?.setOnSingClickListener {

                dialog?.dismiss()
            }

            bindingFriend?.btnUnfollow?.setOnSingClickListener {

                dialog?.dismiss()
            }

            bindingFriend?.btnUnfriend?.setOnSingClickListener {
                viewModel.getFriendRequestByIdDelete(user.idUser ?: 0, viewModel.getIdPreferences())
                viewModel.friendRequest.observe(this) {
                    viewModel.deleteFriendRequest(it)
                    adapter?.removeItem(user)
                }
                dialog?.dismiss()
            }

            dialog?.setContentView(bindingFriend?.root!!)
        }
    }

    private fun handleAdapter() {
        adapter = FriendOfUserAdapter(userList, false)
        adapter?.setCurrentUserId(viewModel.getIdPreferences())
        binding.rcvUserOfFriend.adapter = adapter
        viewModel.getAllFriendOfUser(user?.idUser!!)
        viewModel.getAllFriendCurrentUser(viewModel.getIdPreferences())

        viewModel.friendList.observe(this) { friendRequestList ->
            viewModel.userList.observe(this) { userList ->
                // Lọc danh sách bạn bè dựa trên điều kiện senderId, receiverId và status
                val filteredFriendList = friendRequestList.filter { friendRequest ->
                    (friendRequest.senderId == user?.idUser!! && friendRequest.receiverId != null && friendRequest.status == 1) ||
                            (friendRequest.receiverId == user?.idUser!! && friendRequest.senderId != null && friendRequest.status == 1)
                }

                // Lấy danh sách ID của bạn bè đã lọc
                val friendIds = filteredFriendList.mapNotNull { friendRequest ->
                    if (friendRequest.senderId == user?.idUser!!) {
                        friendRequest.receiverId
                    } else {
                        friendRequest.senderId
                    }
                }

                // Lọc danh sách người dùng dựa trên danh sách ID bạn bè
                val friends = userList.filter { user ->
                    user.idUser in friendIds
                }

                // Cập nhật danh sách bạn bè trong adapter
                adapter?.setFriendList(friends)
                adapter?.setFriendRequestLists(friendRequestList)

                adapter?.onClickItem = {
                    if (it.idUser == viewModel.getIdPreferences()) {
                        findNavController().navigate(R.id.action_seenFriendsOfFriendFragment_to_profileFragment)
                    } else {
                        findNavController().navigate(
                            R.id.action_seenFriendsOfFriendFragment_to_profileOtherUserFragment,
                            bundleOf(
                                Pair("otherUser", it)
                            )
                        )
                    }
                }

//                adapter?.onClickAddFriend = {
//                    if (!isFriendRequestSent){
//                        val newFriend = FriendRequest().apply {
//                            senderId = viewModel.getIdPreferences()
//                            receiverId = it.idUser
//                            time = System.currentTimeMillis()
//                            status = ProfileOtherUserFragment.FRIEND_REQUEST_PENDING
//                        }
//                        viewModel.insertFriendRequest(newFriend)
//                        adapter?.isFriendRequestSent(true)
//                        isFriendRequestSent = true
//                    }else {
//                        viewModel.getFriendRequestByIdDelete(user?.idUser ?: 0, viewModel.getIdPreferences())
//                        viewModel.friendRequest.observe(this) {
//                            viewModel.deleteFriendRequest(it)
//                        }
//                        adapter?.isFriendRequestSent(false)
//                        isFriendRequestSent = false
//                    }
//
//                }

                binding.svUserOfFriend.queryHint = "Tìm kiếm bạn bè"
                binding.svUserOfFriend.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Thực hiện tìm kiếm khi người dùng thay đổi văn bản tìm kiếm
                        val searchText = newText?.trim() ?: ""
                        val searchResults = friends.filter { friend ->
                            friend.nameUser!!.contains(searchText, ignoreCase = true)
                        }

                        // Cập nhật danh sách bạn bè trong adapter với kết quả tìm kiếm
                        adapter?.setFriendList(searchResults)
                        return true
                    }
                })
            }
        }

        viewModel.getCurrentUser(viewModel.getIdPreferences())
        viewModel.currentUser.observe(this) {
            adapter?.setCurrentUser(it)
        }
    }

}
