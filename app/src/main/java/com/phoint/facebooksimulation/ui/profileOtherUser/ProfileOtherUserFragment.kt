package com.phoint.facebooksimulation.ui.profileOtherUser

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.FriendRequest
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.BottomDialogConfirmFriendBinding
import com.phoint.facebooksimulation.databinding.BottomDialogDeleteFriendBinding
import com.phoint.facebooksimulation.databinding.BottomDialogMenuOtherUserBinding
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonRepostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonsForReportingBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReportPostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogShareBinding
import com.phoint.facebooksimulation.databinding.BottomDialogUndoNewspaperBinding
import com.phoint.facebooksimulation.databinding.FragmentProfileOtherUserBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.home.PostsAdapter
import com.phoint.facebooksimulation.ui.profile.FriendAdapter
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ProfileOtherUserFragment :
    BaseFragment<FragmentProfileOtherUserBinding, ProfileOtherUserViewModel>() {
    private var otherUser: User? = null
    private var bindingPermission: BottomDialogPermissionBinding? = null
    private var posts: Post? = null
    private var adapterPost: PostsAdapter? = null
    private var postList = ArrayList<Post>()
    private var dialog: BottomSheetDialog? = null
    private var bindingConfirmFriend: BottomDialogConfirmFriendBinding? = null
    private var bindingDeleteFriend: BottomDialogDeleteFriendBinding? = null
    private var bindingUnDoNewspaper: BottomDialogUndoNewspaperBinding? = null
    private var bindingMenu: BottomDialogMenuOtherUserBinding? = null
    private var bindingSharePost: BottomDialogShareBinding? = null
    private var bindingReportPost: BottomDialogReportPostBinding? = null
    private var bindingReasonReport: BottomDialogReasonRepostBinding? = null
    private var bindingReasonsForReporting: BottomDialogReasonsForReportingBinding? = null
    private var isPostDataLoaded = false
    private var isSavePost = false
    private var isConfirmFriend = false
    private var adapterFriendRequest: FriendAdapter? = null
    private var friendList = ArrayList<User>()

    companion object {
        const val PUBLIC = "Công khai"
        const val FRIEND_REQUEST_PENDING = 0
        const val FRIEND_REQUEST_ACCEPTED = 1
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_profile_other_user
    }

    override fun viewModelClass(): Class<ProfileOtherUserViewModel> {
        return ProfileOtherUserViewModel::class.java
    }

    override fun initView() {
        binding.rcvPost.visibility = View.GONE
        binding.shimmerContainer.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.visibility = View.GONE
            binding.rcvPost.visibility = View.VISIBLE
        }, 2000)

        viewModel.savePosts.observe(this) { savePostList ->
            val postId = posts?.idPost ?: 0
            val isPostSaved = savePostList.any { it.savePost?.idPost == postId }
            isSavePost = isPostSaved
            if (isPostSaved) {
                bindingMenu?.btnSavePost?.text =
                    "Bỏ lưu bài viết \nGỡ khỏi danh sách các mục đã lưu"
                val drawableLeft = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_remove_save_favorite_post
                )
                bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                isSavePost = false
            } else {
                bindingMenu?.btnSavePost?.text =
                    "Lưu bài viết \nThêm vào danh sách các mục đã lưu"
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_post)
                bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                isSavePost = true
            }
        }

        if (!isPostDataLoaded) {
            otherUser = arguments?.getParcelable("otherUser") as? User
            if (otherUser != null) {
                isPostDataLoaded = true
            }
        }

        handleActionBar()
        handlingUserDatabase()
        handleAdapterPost()
        handleDialogShare()
        handleDialogMenu()
        handleComment()
        handleListFriend()
        showFollow()
        switchToViewAllFriends()
        switchToViewAllImages()
        handleFriendFunction()

        binding.btnMenu.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileOtherUserFragment_to_settingPageOtherUserFragment)
        }

        binding.btnSendInformation.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_profileOtherUserFragment_to_sendProfileOtherFragment, bundleOf(
                    Pair("otherUser", otherUser)
                )
            )
        }

    }

    private fun switchToViewAllImages() {
        binding.btnImage.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_profileOtherUserFragment_to_seeImagesFragment, bundleOf(
                    Pair("user", otherUser)
                )
            )
        }
    }

    private fun switchToViewAllFriends() {
        binding.btnSeeFriend.setOnSingClickListener {
            findNavController().navigate(
                R.id.action_profileOtherUserFragment_to_seenFriendsOfFriendFragment, bundleOf(
                    Pair("user", otherUser)
                )
            )
        }
    }

    private fun handleListFriend() {
        adapterFriendRequest = FriendAdapter(friendList)
        adapterFriendRequest?.setCurrentUserId(viewModel.getIdPreferences())
        binding.rcvFriends.adapter = adapterFriendRequest

        viewModel.getAllFriend(otherUser?.idUser!!)

        viewModel.friendList.observe(this) { friendRequestList ->
            viewModel.userList.observe(this) { userList ->
                // Lọc danh sách bạn bè dựa trên điều kiện senderId, receiverId và status
                val filteredFriendList = friendRequestList.filter { friendRequest ->
                    (friendRequest.senderId == otherUser?.idUser!! && friendRequest.receiverId != null && friendRequest.status == 1) ||
                            (friendRequest.receiverId == otherUser?.idUser!! && friendRequest.senderId != null && friendRequest.status == 1)
                }

                // Lấy danh sách ID của bạn bè đã lọc
                val friendIds = filteredFriendList.mapNotNull { friendRequest ->
                    if (friendRequest.senderId == otherUser?.idUser!!) {
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
                adapterFriendRequest?.setFriendList(friends)
                // Lấy số lượng bạn bè
                val numberOfFriends = adapterFriendRequest?.itemCount

                // Hiển thị số lượng bạn bè
                if (numberOfFriends != null) {
                    binding.tvFriends.text = "$numberOfFriends người bạn"
                }
            }
        }
    }

    private fun showFollow() {
        viewModel.getAllFriend(viewModel.getIdPreferences())

        viewModel.friendList.observe(this) { friendRequestList ->
            viewModel.userList.observe(this) { userList ->
                val filteredFriendList = friendRequestList.filter { friendRequest ->
                    (friendRequest.senderId == viewModel.getIdPreferences() && friendRequest.receiverId != null && friendRequest.status == 0) ||
                            (friendRequest.receiverId == viewModel.getIdPreferences() && friendRequest.senderId != null && friendRequest.status == 0)
                }

                val friendIds = filteredFriendList.mapNotNull { friendRequest ->
                    if (friendRequest.senderId == viewModel.getIdPreferences()) {
                        friendRequest.receiverId
                    } else {
                        friendRequest.senderId
                    }
                }

                val numberOfFriends = friendIds.size
                if (numberOfFriends != null && numberOfFriends == 1) {
                    binding.tvFollow.text = "$numberOfFriends người theo dõi"
                    binding.tvFollow.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleFriendFunction() {
        viewModel.getSenderId(viewModel.getIdPreferences())
        viewModel.sender.observe(this) {
            if (it.senderId == viewModel.getIdPreferences()) {
                viewModel.getFriendRequestById(viewModel.getIdPreferences(), otherUser?.idUser ?: 0)
                viewModel.friendRequest.observe(this) {
                    when (it.status) {
                        FRIEND_REQUEST_PENDING -> {
                            binding.tvAddFriend.text = "Hủy lời mời"
                            binding.imgAddFriend.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_add_friend
                            )
                        }

                        FRIEND_REQUEST_ACCEPTED -> {
                            binding.tvAddFriend.text = "Bạn bè"
                            binding.imgAddFriend.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_confirm_friend
                            )
                        }

                        else -> {
                            binding.tvAddFriend.text = "Thêm bạn bè"
                            binding.imgAddFriend.background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_add_friend
                            )
                        }
                    }
                }

            }
        }

        viewModel.getFriendRequest(otherUser?.idUser!!, viewModel.getIdPreferences())
        viewModel.friendRequestOwnerUser.observe(this) {
            isConfirmFriend = true
            viewModel.getFriendRequestById(otherUser?.idUser ?: 0, viewModel.getIdPreferences())
            viewModel.friendRequest.observe(this) {
                when (it.status) {
                    FRIEND_REQUEST_PENDING -> {
                        binding.tvAddFriend.text = "Phản hồi"
                        binding.imgAddFriend.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_confirm_friend
                        )
                    }

                    FRIEND_REQUEST_ACCEPTED -> {
                        binding.tvAddFriend.text = "Bạn bè"
                        binding.imgAddFriend.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_confirm_friend
                        )
                    }

                    else -> {
                        binding.tvAddFriend.text = "Thêm bạn bè"
                        binding.imgAddFriend.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_friend)
                    }
                }
            }
        }

        binding.btnAddFriend.setOnSingClickListener {
            if (!isConfirmFriend) {
                when (binding.tvAddFriend.text) {
                    "Bạn bè" -> {
                        handleDialogDeleteFriend()
                    }

                    "Hủy lời mời" -> {
                        viewModel.getFriendRequestById(
                            viewModel.getIdPreferences(),
                            otherUser?.idUser ?: 0
                        )
                        viewModel.friendRequest.observe(this) {
                            viewModel.deleteFriendRequest(it)
                        }
                        binding.tvAddFriend.text = "Thêm bạn bè"
                    }

                    else -> {
                        viewModel.userId.observe(this) {
                            val friendRequest = FriendRequest().apply {
                                senderId = viewModel.getIdPreferences()
                                receiverId = otherUser?.idUser
                                time = System.currentTimeMillis()
                                status = FRIEND_REQUEST_PENDING
                            }
                            viewModel.insertFriendRequest(friendRequest)
                        }
                        binding.tvAddFriend.text = "Hủy lời mời"
                    }
                }
            } else {
                if (binding.tvAddFriend.text == "Phản hồi") {
                    handleDialogFriend()
                } else {
                    handleDialogDeleteInvitation()
                }
            }
        }
    }

    private fun handleDialogDeleteInvitation() {
        bindingDeleteFriend = BottomDialogDeleteFriendBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        dialog?.show()

        bindingDeleteFriend?.btnDeleteFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(otherUser?.idUser ?: 0, viewModel.getIdPreferences())
            viewModel.friendRequest.observe(this) {
                viewModel.deleteFriendRequest(it)
            }
            dialog?.dismiss()
        }

        viewModel.doneDeleteFriendRequest.observe(this) {
            binding.tvAddFriend.text = "Thêm bạn bè"
            isConfirmFriend = false
            dialog?.dismiss()
        }
        dialog?.setContentView(bindingDeleteFriend?.root!!)
    }

    private fun handleDialogDeleteFriend() {
        bindingDeleteFriend = BottomDialogDeleteFriendBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        dialog?.show()

        bindingDeleteFriend?.btnDeleteFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(otherUser?.idUser ?: 0, viewModel.getIdPreferences())
            viewModel.friendRequest.observe(this) {
                viewModel.deleteFriendRequest(it)
            }
            binding.tvAddFriend.text = "Thêm bạn bè"
            dialog?.dismiss()
        }

        viewModel.doneDeleteFriendRequest.observe(this) {
            binding.tvAddFriend.text = "Thêm bạn bè"
        }
        dialog?.setContentView(bindingDeleteFriend?.root!!)
    }

    private fun handleDialogFriend() {
        bindingConfirmFriend = BottomDialogConfirmFriendBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        dialog?.show()

        bindingConfirmFriend?.btnConfirmFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(otherUser?.idUser ?: 0, viewModel.getIdPreferences())
            viewModel.friendRequest.observe(this) {
                it.status = FRIEND_REQUEST_ACCEPTED
                viewModel.updateFriendRequest(it)
            }
            binding.tvAddFriend.text = "Bạn bè"
            dialog?.dismiss()
        }

        bindingConfirmFriend?.btnDeleteFriend?.setOnSingClickListener {
            viewModel.getFriendRequestById(otherUser?.idUser ?: 0, viewModel.getIdPreferences())
            viewModel.friendRequest.observe(this) {
                viewModel.deleteFriendRequest(it)
            }
            binding.tvAddFriend.text = "Thêm bạn bè"
            dialog?.dismiss()
        }


        viewModel.doneUpdateFriendRequest.observe(this) {
            binding.tvAddFriend.text = "Bạn bè"
            isConfirmFriend = false
        }

        viewModel.doneDeleteFriendRequest.observe(this) {
            binding.tvAddFriend.text = "Thêm bạn bè"
            isConfirmFriend = false
        }
        dialog?.setContentView(bindingConfirmFriend?.root!!)
    }

    private fun handleComment() {
        adapterPost?.onClickComment = { post ->
            if (!post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_profileOtherUserFragment_to_otherPeopleCommentFragment,
                    bundleOf(Pair("post", post))
                )
                postList.clear()
            } else if (post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_profileOtherUserFragment_to_seenCommentAvatarFragment,
                    bundleOf(Pair("post", post))
                )
            } else {
                findNavController().navigate(
                    R.id.action_profileOtherUserFragment_to_seeCommentSharePostFragment,
                    bundleOf(Pair("post", post))
                )
            }
        }
    }

    private fun handleDialogShare() {
        bindingSharePost = BottomDialogShareBinding.inflate(layoutInflater)
        bindingPermission = BottomDialogPermissionBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()

        if (otherUser?.avatarUser != null) {
            Glide.with(requireContext()).load(otherUser?.avatarUser)
                .into(bindingSharePost?.imgUser!!)
        }
        bindingSharePost?.tvName?.text = "${otherUser?.nameUser}"

        adapterPost?.onClickShare = { post ->
            dialog?.show()
            bindingSharePost?.btnShare?.setOnSingClickListener {
                val newPost = Post().apply {
                    idPost = System.currentTimeMillis()
                    userIdPost = viewModel.getIdPreferences()
                    userIdPostShare = otherUser?.idUser
                    contentPost = bindingSharePost?.edtContent?.text.toString().trim()
                    dateTimePost = System.currentTimeMillis()
                    privacyPost = bindingSharePost?.tvPermission?.text.toString().trim()
                    sharedFromPostId = if (post.sharedFromPostId != null) {
                        post.sharedFromPostId
                    } else {
                        post.idPost
                    }
                    isAvatar = true
                    isShared = true
                }
                viewModel.insertPost(newPost)
                dialog?.dismiss()
            }

            bindingSharePost?.tvPermission?.setOnSingClickListener {
                dialog?.show()

                bindingPermission?.btnPermissionPublic?.setOnSingClickListener {
                    bindingSharePost?.tvPermission?.text =
                        bindingPermission?.tvPublic?.text.toString().trim()
                    val drawableLeft =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_public)
                    val drawableRight =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                    bindingSharePost?.tvPermission?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        drawableRight,
                        null
                    )
                    dialog?.show()


                    bindingSharePost?.btnShare?.setOnSingClickListener {
                        val newPostOne = Post().apply {
                            idPost = System.currentTimeMillis()
                            userIdPost = viewModel.getIdPreferences()
                            userIdPostShare = otherUser?.idUser
                            contentPost = bindingSharePost?.edtContent?.text.toString().trim()
                            dateTimePost = System.currentTimeMillis()
                            privacyPost = bindingSharePost?.tvPermission?.text.toString().trim()
                            sharedFromPostId = if (post.sharedFromPostId != null) {
                                post.sharedFromPostId
                            } else {
                                post.idPost
                            }
                            isAvatar = true
                            isShared = true
                        }
                        viewModel.insertPost(newPostOne)
                        dialog?.dismiss()
                    }

                    dialog?.setContentView(bindingSharePost?.root!!)
                }

                bindingPermission?.btnPermissionFriend?.setOnSingClickListener {
                    bindingSharePost?.tvPermission?.text =
                        bindingPermission?.tvFriend?.text.toString().trim()
                    val drawableLeft =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_permission_friend)
                    val drawableRight =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                    bindingSharePost?.tvPermission?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        drawableRight,
                        null
                    )
                    dialog?.show()


                    bindingSharePost?.btnShare?.setOnSingClickListener {
                        val newPostTwo = Post().apply {
                            idPost = System.currentTimeMillis()
                            userIdPost = viewModel.getIdPreferences()
                            userIdPostShare = otherUser?.idUser
                            contentPost = bindingSharePost?.edtContent?.text.toString().trim()
                            dateTimePost = System.currentTimeMillis()
                            privacyPost = bindingSharePost?.tvPermission?.text.toString().trim()
                            if (post.sharedFromPostId != null) {
                                sharedFromPostId = post.sharedFromPostId
                            } else {
                                sharedFromPostId = post.idPost
                            }
                            isAvatar = true
                            isShared = true
                        }
                        viewModel.insertPost(newPostTwo)
                        dialog?.dismiss()
                    }

                    dialog?.setContentView(bindingSharePost?.root!!)
                }

                bindingPermission?.btnPermissionPrivate?.setOnSingClickListener {
                    bindingSharePost?.tvPermission?.text =
                        bindingPermission?.tvPrivate?.text.toString().trim()
                    val drawableLeft = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_permission_padlock
                    )
                    val drawableRight =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_permission)
                    bindingSharePost?.tvPermission?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        drawableRight,
                        null
                    )
                    dialog?.show()
                    bindingSharePost?.btnShare?.setOnSingClickListener {
                        val newPostThree = Post().apply {
                            idPost = System.currentTimeMillis()
                            userIdPost = viewModel.getIdPreferences()
                            userIdPostShare = otherUser?.idUser
                            contentPost = bindingSharePost?.edtContent?.text.toString().trim()
                            dateTimePost = System.currentTimeMillis()
                            privacyPost = bindingSharePost?.tvPermission?.text.toString().trim()
                            if (post.sharedFromPostId != null) {
                                sharedFromPostId = post.sharedFromPostId
                            } else {
                                sharedFromPostId = post.idPost
                            }
                            isAvatar = true
                            isShared = true
                        }
                        viewModel.insertPost(newPostThree)
                        dialog?.dismiss()
                    }
                    dialog?.setContentView(bindingSharePost?.root!!)
                }
                dialog?.setContentView(bindingPermission?.root!!)
            }
            dialog?.setContentView(bindingSharePost?.root!!)
        }
    }

    private fun handleDialogMenu() {
        bindingMenu = BottomDialogMenuOtherUserBinding.inflate(layoutInflater)
        bindingReportPost = BottomDialogReportPostBinding.inflate(layoutInflater)
        bindingUnDoNewspaper = BottomDialogUndoNewspaperBinding.inflate(layoutInflater)
        bindingReasonReport = BottomDialogReasonRepostBinding.inflate(layoutInflater)
        bindingReasonsForReporting = BottomDialogReasonsForReportingBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()

        adapterPost?.onClickMenu = { post, position ->
            viewModel.getOwnerPost(post.idPost ?: 0)
            posts = post
            dialog?.show()

            viewModel.savePosts.observe(this) { savePostList ->
                val postId = posts?.idPost ?: 0
                val isPostSaved = savePostList.any { it.savePost?.idPost == postId }
                isSavePost = isPostSaved
                if (isPostSaved) {
                    bindingMenu?.btnSavePost?.text =
                        "Bỏ lưu bài viết \nGỡ khỏi danh sách các mục đã lưu"
                    val drawableLeft = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_remove_save_favorite_post
                    )
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = false
                } else {
                    bindingMenu?.btnSavePost?.text =
                        "Lưu bài viết \nThêm vào danh sách các mục đã lưu"
                    val drawableLeft =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_post)
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = true
                }
            }

            bindingMenu?.btnSavePost?.setOnClickListener {
                val postId = post.idPost ?: 0
                val userId = viewModel.getIdPreferences()
                if (!isSavePost) {
                    val saveFavoritePost = SaveFavoritePost().apply {
                        id = System.currentTimeMillis()
                        userOwnerId = userId
                        postOwnerId = postId
                        timestamp = System.currentTimeMillis()
                        savePost = post
                    }
                    viewModel.insertSaveFavoritePost(saveFavoritePost)
                    bindingMenu?.btnSavePost?.text =
                        "Bỏ lưu bài viết \nGỡ khỏi danh sách các mục đã lưu"
                    val drawableLeft = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_remove_save_favorite_post
                    )
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = true
                } else {
                    viewModel.getAllSaveFavoritePost(userId, postId)
                    viewModel.saveFavoritePostUser.observe(this) {
                        viewModel.deleteSaveFavoritePost(it)
                    }
                    bindingMenu?.btnSavePost?.text =
                        "Lưu bài viết \nThêm vào danh sách các mục đã lưu"
                    val drawableLeft =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_save_post)
                    bindingMenu?.btnSavePost?.setCompoundDrawablesWithIntrinsicBounds(
                        drawableLeft,
                        null,
                        null,
                        null
                    )
                    isSavePost = false
                }
                dialog?.dismiss()
            }

            bindingMenu?.btnReportPost?.setOnSingClickListener {
                viewModel.ownerPost.observe(this) {
                    bindingReportPost?.tvBlock?.text = "Chặn trang cá nhân của ${it.nameUser}"
                    bindingReportPost?.tvHideAll?.text = "Ẩn tất cả từ ${it.nameUser}"
                }

                dialog?.show()
                val reportPost = Report().apply {
                    id = System.currentTimeMillis()
                    userId = post.userIdPost
                    postId = post.idPost
                    content = "${post.contentPost} and ${post.srcPost}"
                    reason = "Bài đăng này có vấn đề"
                    timestamp = System.currentTimeMillis()
                }
                viewModel.insertReport(reportPost)

                // Hoan tac bao cao
                bindingReportPost?.btnUndoNewspaper?.setOnSingClickListener {
                    viewModel.getAllReportPost(
                        reportPost.id ?: 0,
                        post.idPost ?: 0,
                        post.userIdPost ?: 0
                    )
                    viewModel.reportPost.observe(this) {
                        viewModel.deleteReport(it)
                    }

                    viewModel.ownerPost.observe(this) {
                        bindingUnDoNewspaper?.tvBlock?.text =
                            "Chặn trang cá nhân của ${it.nameUser}"
                        bindingUnDoNewspaper?.tvHideAll?.text = "Ẩn tất cả từ ${it.nameUser}"
                    }
                    dialog?.show()

                    dialog?.setContentView(bindingUnDoNewspaper?.root!!)
                }

                // reason report
                bindingReportPost?.btnMoreReason?.setOnSingClickListener {
                    dialog?.show()

                    //back
                    bindingReasonReport?.btnBack?.setOnSingClickListener {
                        dialog?.show()

                        dialog?.setContentView(bindingReportPost?.root!!)
                    }

                    // nude
                    bindingReasonReport?.btnNudePhoto?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Ảnh khỏa thân"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Họat động tình dục" +
                                    "\n\n• Bán hoặc mua dâm" +
                                    "\n\n• Nhũ hoa (trừ trường hợp đang cho con bú, liên quan đến sử khỏa và hành động phản đối)" +
                                    "\n\n• Ảnh khỏa thân hiển thị bộ phận sinh dục" +
                                    "\n\n• Ngôn ngữ khiêu dâm"
                        dialog?.show()
                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    //Violence
                    bindingReasonReport?.btnViolence?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Bạo lực"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Đe dọa sử dụng bạo lực \n\t Ví dụ : nhắm mục tiêu một người và nhắc để vũ khí cụ thể" +
                                    "\n\n• Cá nhân hoặc tổ chức nguy hiểm \n\t  Ví dụ: Chủ nghĩa khủng bố hoặc một tổ chức tội phạm" +
                                    "\n\n• Hình ảnh cực kỳ bạo lực \n\t  Ví dụ: tôn vinh bạo lực hoặc tán dương sự đau khổ" +
                                    "\n\n• Một loại bạo lực khác \n\t  Ví dụ: Hình ảnh hoặc nội dung khác gây khó chịu"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post.idPost ?: 0,
                                post.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }

                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnIllegalSales?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Chất cấm, chất gây nghiện"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi không cho phép:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Mua bán đồ uống có cồn, thuốc lá, cần sa hoặc thuốc để điều trị" +
                                    "\n\n  • Mua hoặc bán các loại thuốc không phải để điều trị (ví dụ: cocaine hoặc heroin)" +
                                    "\n\n  • Sử dụng, chế tạo hoặc quảng cáo thuốc không phải điều trị" +
                                    "\n\nChúng tôi cho phép:" +
                                    "\n\n  • Sử dụng đồ uổng có cồn, thuốc lá hoặc cần sa" +
                                    "\n\n  • Thảo luận về cách phục hồi trong trường hợp nghiện hoặc lạm dụng thuốc"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post.idPost ?: 0,
                                post.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Bán hàng trái phép"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnSpam?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Spam"
                        bindingReasonsForReporting?.tvContent?.text = "Chúng tôi cho phép:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Mua, bán hay tặng tài khoản, vai trò hoặc quyền" +
                                    "\n\n  • Khuyền khích mọi người tương tác với nội dung sai sự thật" +
                                    "\n\n  • Dùng liên kết gây hiểu nhầm đề chuyển mọi người từ Facebook đến nơi khác"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post.idPost ?: 0,
                                post.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnTrouble?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Nội dung này có vi phạm Tiêu chuẩn cộng đồng của chúng tôi không?"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Các tiêu chuẩn của chúng tôi giải thích về những gì chúng tôi cho phép và không cho phép hiển thị trên Facebook. Với sự hỗ trợ của các chuyên gia, chúng tôi thường xuyên xem xét và cập nhật các tiêu chuẩn mình đề ra"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Quấy rối"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnTerrorism?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Khủng bố"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi chỉ gỡ nội dung vi phạm Tiêu chuẩn cộng đồng của mình.\nChúng tôi gỡ nội dung về mọi cá nhân hoặc nhóm phi chính phủ tham gia hay ủng hộ các hành vi bạo lực có kế hoạch phục vụ mục đích chính trị, tôn giáo hoặc lí tưởng"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnHateSpeech?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text = "Ngôn từ gây thù ghét"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Chúng tôi chỉ gỡ nội dung trực tiếp công kích mọi người dựa trên một số đặc điểm được bảo vệ, chẳng hạn như:"
                        bindingReasonsForReporting?.tvContentReport?.text =
                            " • Ngôn từ bạo lực hoặc xúc phạm nhân phẩm\nVí dụ: so sánh tất cả những người thuộc một chủng tộc nào đó với côn trùng hoặc động vật" +
                                    "\n\n • Lời lẽ hạ thấp, khinh miệt hoặc coi thường người khác\nVí dụ: gợi ý rằng tất cả những người thuộc giới tính nào đó là ghê tởm" +
                                    "\n\n • Từ ngữ phỉ báng\nVí dụ: dùng các định kiến rập khuôn có hại về chủng tộc để lăng mạ ai đó" +
                                    "\n\n • Lời kêu gọi bài xích hoặc cô lập\nVí dụ: nói rằng những người thuộc tôn giáo nào đó không được phép bỏ phiếu"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason =
                                    bindingReasonsForReporting?.tvTitleReport?.text.toString()
                                        .trim()
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    bindingReasonReport?.btnMisinformation?.setOnSingClickListener {
                        bindingReasonsForReporting?.tvTitleReport?.text =
                            "Nội dung này có vi phạm Tiêu chuẩn cộng đồng của chúng tôi không?"
                        bindingReasonsForReporting?.tvContent?.text =
                            "Các tiêu chuẩn của chúng tôi giải thích về những gì chúng tôi cho phép và không cho phép hiển thị trên Facebook. Với sự hỗ trợ của các chuyên gia, chúng tôi thường xuyên xem xét và cập nhật các tiêu chuẩn mình đề ra"
                        dialog?.show()

                        bindingReasonsForReporting?.btnSentReport?.setOnSingClickListener {
                            viewModel.getAllReportPost(
                                reportPost.id ?: 0,
                                post?.idPost ?: 0,
                                post?.userIdPost ?: 0
                            )
                            viewModel.reportPost.observe(this) {
                                it.reason = "Thông tin sai sự thật"
                                viewModel.updateReport(it)
                                dialog?.dismiss()
                            }
                        }

                        bindingReasonsForReporting?.btnDismiss?.setOnSingClickListener {
                            dialog?.dismiss()
                        }

                        bindingReasonsForReporting?.btnBack?.setOnSingClickListener {
                            dialog?.show()

                            dialog?.setContentView(bindingReasonReport?.root!!)
                        }
                        dialog?.setContentView(bindingReasonsForReporting?.root!!)
                    }

                    dialog?.setContentView(bindingReasonReport?.root!!)
                }

                dialog?.setContentView(bindingReportPost?.root!!)
            }

            dialog?.setContentView(bindingMenu?.root!!)
        }
    }

    private fun handleAdapterPost() {
        viewModel.getInformationUser(viewModel.getIdPreferences())
        binding.rcvPost.isNestedScrollingEnabled = false
        viewModel.getJoinDataPostPrivacy(otherUser?.idUser!!)
        adapterPost = PostsAdapter(postList, viewModel.getIdPreferences())
        binding.rcvPost.adapter = adapterPost

        viewModel.privacyPublic.observe(this) { posts ->
            val sortedFriendPosts = posts.sortedByDescending { it.dateTimePost }
            postList.addAll(sortedFriendPosts)
            adapterPost?.notifyDataSetChanged()
        }

        viewModel.userList.observe(this) {
            adapterPost?.setUsers(it)
            adapterPost?.notifyDataSetChanged()
        }

        viewModel.postAllList.observe(this) {
            adapterPost?.setPostShares(it)
            adapterPost?.notifyDataSetChanged()
        }

        adapterPost?.onClickLike = { post ->
            handleLikeClicked(post)
        }

        adapterPost?.onClickItem = { post ->
            findNavController().navigate(
                R.id.action_profileOtherUserFragment_to_displayImageFragment, bundleOf(
                    Pair("post", post)
                )
            )
        }
    }

    private fun handleLikeClicked(post: Post) {
        post.let { clickedPost ->
            val currentUser = viewModel.getIdPreferences()
            // Kiểm tra xem người dùng đã like bài post trước đó chưa
            val isLikedByCurrentUser = clickedPost.likes.any { it.likeUserId == currentUser }

            if (isLikedByCurrentUser) {
                // Nếu đã like trước đó, thì xóa like của người dùng
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    clickedPost.likes.removeIf { it.likeUserId == currentUser }
                    clickedPost.liked = false
                    viewModel.getNotificationById(
                        viewModel.getIdPreferences(),
                        post.userIdPost!!,
                        post.idPost!!
                    )
                    viewModel.notificationById.observe(this) {
                        viewModel.deleteNotification(it)
                    }
                }
            } else {
                // Ngược lại, thêm like của người dùng
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                like.likeId = System.currentTimeMillis()
                viewModel.userId.observe(this) {
                    like.nameUser = it.nameUser
                    val newNotification = Notification().apply {
                        userId = post.userIdPost
                        senderId = viewModel.getIdPreferences()
                        postId = post.idPost
                        notificationType = if (post.isAvatar) {
                            "${it.nameUser} thích ảnh đại diện của bạn của bạn"
                        } else {
                            "${it.nameUser} thích ảnh của bạn: ${post.contentPost}"
                        }
                        profilePicture = R.drawable.ic_like_blue
                        timestamp = System.currentTimeMillis()
                        status = "on"
                        notificationId = System.currentTimeMillis()
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)
                }
                clickedPost.likes.add(like)
            }
            // Cập nhật bài post trong ViewModel
            viewModel.updatePost(clickedPost)
            val position = postList.indexOf(clickedPost)
            if (position != -1) {
                adapterPost?.notifyItemChanged(position)
            }
        }
    }

    private fun handlingUserDatabase() {
        if (otherUser?.avatarUser != null) {
            Glide.with(requireContext()).load(otherUser?.avatarUser).into(binding.imgUser)
        }

        if (otherUser?.coverImageUser != null) {
            Glide.with(requireContext()).load(otherUser?.coverImageUser)
                .into(binding.imgCoverImageUser)
        }

        if (otherUser?.story != null) {
            binding.tvStory.visibility = View.VISIBLE
            binding.tvStory.text = "${otherUser?.story}"
        } else {
            binding.tvStory.visibility = View.GONE
        }

        binding.tvNameUser.text = "${otherUser?.nameUser}"

        if (otherUser != null && otherUser?.workExperience != null && otherUser?.workExperience?.privacy == PUBLIC) {
            binding.tvWork.visibility = View.VISIBLE
            val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_work)
            binding.tvWork.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvWork.text = otherUser?.workExperience?.nameWork
        } else {
            binding.tvWork.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvWork.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.colleges != null && otherUser?.colleges?.privacy == PUBLIC) {
            binding.tvColleges.visibility = View.VISIBLE
            val drawableLeft =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_student_education_study
                )
            binding.tvColleges.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvColleges.text = otherUser?.colleges?.nameColleges
        } else {
            binding.tvColleges.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvColleges.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.highSchool != null && otherUser?.highSchool?.privacy == PUBLIC) {
            binding.tvHighSchool.visibility = View.VISIBLE
            val drawableLeft =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_student_education_study
                )
            binding.tvHighSchool.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvHighSchool.text = otherUser?.highSchool?.nameHighSchool
        } else {
            binding.tvHighSchool.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvHighSchool.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.currentCityAndProvince != null && otherUser?.currentCityAndProvince?.privacy == PUBLIC) {
            binding.tvCurrentCity.visibility = View.VISIBLE
            val drawableLeft =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_address_location_map)
            binding.tvCurrentCity.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvCurrentCity.text =
                otherUser?.currentCityAndProvince?.nameCurrentProvinceOrCity
        } else {
            binding.tvCurrentCity.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvCurrentCity.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.homeTown != null && otherUser?.homeTown?.privacy == PUBLIC) {
            binding.tvHomeTown.visibility = View.VISIBLE
            val drawableLeft =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_address_location_map)
            binding.tvHomeTown.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvHomeTown.text = otherUser?.homeTown?.nameHomeTown
        } else {
            binding.tvHomeTown.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvHomeTown.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.relationship != null && otherUser?.relationship?.privacy == PUBLIC) {
            binding.tvRelationship.visibility = View.VISIBLE
            val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_love)
            binding.tvRelationship.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
            binding.tvRelationship.text = otherUser?.relationship?.nameRelationship
        } else {
            binding.tvRelationship.setCompoundDrawables(
                null,
                null,
                null,
                null
            )
            binding.tvRelationship.visibility = View.GONE
        }

        if (otherUser != null && otherUser?.story != null && otherUser?.story?.privacy == PUBLIC) {
            binding.tvStory.visibility = View.VISIBLE
            binding.tvStory.text = otherUser?.story?.nameStory
        } else {
            binding.tvStory.visibility = View.GONE
        }

    }

    private fun handleActionBar() {
        if (otherUser?.nameUser != null) {
            binding.abProfileOtherUser.setName(otherUser?.nameUser!!)
        }

        binding.abProfileOtherUser.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_profileOtherUserFragment_to_searchOtherUserFragment)
        }

        binding.abProfileOtherUser.setBackCurrentScreen {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.ownerPost.removeObservers(this)
        viewModel.postList.removeObservers(this)
        viewModel.doneUpdate.removeObservers(this)
        viewModel.doneUpdateFriendRequest.removeObservers(this)
        viewModel.doneDeleteFriendRequest.removeObservers(this)
        viewModel.userId.removeObservers(this)
        viewModel.savePosts.removeObservers(this)
        viewModel.privacyPublic.removeObservers(this)
        viewModel.saveFavoritePostUser.removeObservers(this)
        viewModel.reportPost.removeObservers(this)
        viewModel.friendRequest.removeObservers(this)
        viewModel.sender.removeObservers(this)
        viewModel.receiver.removeObservers(this)
        viewModel.friendRequestOwnerUser.removeObservers(this)
        viewModel.notificationById.removeObservers(this)
        viewModel.friendList.removeObservers(this)
        viewModel.postAllList.removeObservers(this)
        dialog?.dismiss()
        binding.rcvPost.adapter = null
        binding.rcvFriends.adapter = null
    }
}
