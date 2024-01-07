package com.phoint.facebooksimulation.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.BottomDialogMenuItemPostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogProfileAvatarBinding
import com.phoint.facebooksimulation.databinding.BottomDialogProfileCoverImageBinding
import com.phoint.facebooksimulation.databinding.BottomDialogSharePostBinding
import com.phoint.facebooksimulation.databinding.FragmentProfileBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.ui.home.PostsAdapter
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    private var adapterPost: PostsAdapter? = null
    private var adapterFriendRequest: FriendAdapter? = null
    private var postList = ArrayList<Post>()
    private var friendList = ArrayList<User>()
    private var bindingDialog: BottomDialogProfileAvatarBinding? = null
    private var bindingDialogCover: BottomDialogProfileCoverImageBinding? = null
    private var bindingMenuPost: BottomDialogMenuItemPostBinding? = null
    private var bindingSharePost: BottomDialogSharePostBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var isUpdatingProfileImage = false
    private var postPin: Int = 0
    private var userId: Long? = null

    companion object {
        const val KEY_PROFILE_EDIT_POST = "KEY_PROFILE_EDIT_POST"
        const val KEY_AVATAR = "AVATAR"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Bạn đã cấp quyền truy cập", Toast.LENGTH_SHORT)
                    .show()
                Log.d("permission", "Bạn đã cấp quyền truy cập")
            } else {
                Toast.makeText(requireContext(), "You have denied access", Toast.LENGTH_SHORT)
                    .show()
                Log.d("permission", "Bạn đã từ chối quyền truy cập")
            }
        }

    private val startActivityForImageUser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                handleImageResult(imageUri)
            }
        }

    private fun handleImageResult(imageUri: Uri?) {
        try {
            val id = viewModel.getIdPreferences()
            viewModel.userId.observe(this) { user ->
                if (id == user.idUser) {
                    if (isUpdatingProfileImage) {
                        if (imageUri != null) {
                            val newPost = Post().apply {
                                idPost = System.currentTimeMillis()
                                userIdPost = viewModel.getIdPreferences()
                                dateTimePost = System.currentTimeMillis()
                                srcPost = imageUri.toString()
                                privacyPost = "Công khai"
                                isAvatar = true
                            }
                            viewModel.insertPost(newPost)
                        }

                        user.avatarUser = imageUri.toString()
                        Glide.with(requireContext()).load(user.avatarUser)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.btnAvatarUser)
                        Glide.with(requireContext()).load(user.avatarUser)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgAvatar)
                        adapterPost?.updateUserAvatar(id, user.avatarUser!!)
                        isUpdatingProfileImage = false
                    } else {
                        user.coverImageUser = imageUri.toString()
                        Glide.with(requireContext()).load(user.coverImageUser)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgCoverImageUser)
                    }
                    viewModel.updateUser(user)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun viewModelClass(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun initView() {
        binding.rcvPost.visibility = View.GONE
        binding.shimmerContainer.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.visibility = View.GONE
            binding.rcvPost.visibility = View.VISIBLE
        }, 2000)

        userId = viewModel.getIdPreferences()
        // handleOtherUserProfile()
        handleInformation()
        switchScreen()
        handleAdapterPosts()
        handleDialogBottom()
        backToTheNextScreen()
        ////handleSwipeRefreshLayout()
        showFriend()
        showFollow()
        seeUserOfFriend()
        handleActionBar()
    }

    private fun handleActionBar() {
        binding.abProfile.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_profileFragment_to_searchHistoryFragment)
        }
    }

    private fun seeUserOfFriend() {
        binding.btnSeeFriend.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userOfFriendFragment)
        }
    }

    private fun showFriend() {
        adapterFriendRequest = FriendAdapter(friendList)
        adapterFriendRequest?.setCurrentUserId(viewModel.getIdPreferences())
        binding.rcvFriends.adapter = adapterFriendRequest

        viewModel.getAllFriend(viewModel.getIdPreferences())

        viewModel.friendList.observe(this) { friendRequestList ->
            viewModel.userList.observe(this) { userList ->
                // Lọc danh sách bạn bè dựa trên điều kiện senderId, receiverId và status
                val filteredFriendList = friendRequestList.filter { friendRequest ->
                    (friendRequest.senderId == viewModel.getIdPreferences() && friendRequest.receiverId != null && friendRequest.status == 1) ||
                            (friendRequest.receiverId == viewModel.getIdPreferences() && friendRequest.senderId != null && friendRequest.status == 1)
                }

                // Lấy danh sách ID của bạn bè đã lọc
                val friendIds = filteredFriendList.mapNotNull { friendRequest ->
                    if (friendRequest.senderId == viewModel.getIdPreferences()) {
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

        adapterFriendRequest?.onClickItem = {
            findNavController().navigate(
                R.id.action_profileFragment_to_profileOtherUserFragment, bundleOf(
                    Pair("otherUser", it)
                )
            )
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

//    private fun handleSwipeRefreshLayout() {
//        binding.swipeRefreshLayout.setColorSchemeColors(Color.BLACK)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.coordinatorLayout.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//                if (scrollY < oldScrollY) {
//                    binding.swipeRefreshLayout.isEnabled = false
//                } else {
//                    binding.swipeRefreshLayout.isEnabled = scrollY == 0
//                }
//            }
//        }
//
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            Handler(Looper.getMainLooper()).postDelayed({
//                handleAdapterPosts()
//                binding.swipeRefreshLayout.isRefreshing = false
//            }, 3000)
//        }
//    }

    private fun backToTheNextScreen() {
        binding.abProfile.setBackCurrentScreen {
            findNavController().popBackStack()
        }
    }

    private fun handleDialogBottom() {
        binding.btnAvatarUser.setOnSingClickListener {
            createBottomDialogImageUser()
            dialog?.show()
        }

        binding.imgCoverImageUser.setOnSingClickListener {
            createBottomDialogImageCover()
            dialog?.show()
        }

        binding.btnCoverImageMin.setOnSingClickListener {
            createBottomDialogImageCover()
            dialog?.show()
        }
    }

    private fun handleAdapterPosts() {
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()
        bindingMenuPost = BottomDialogMenuItemPostBinding.inflate(layoutInflater)
        viewModel.getInformationUser(userId ?: 0)
        viewModel.getJoinDataPost(userId ?: 0)

        binding.rcvPost.isNestedScrollingEnabled = false
        adapterPost = PostsAdapter(postList, userId ?: 0)
        binding.rcvPost.adapter = adapterPost

        viewModel.userList.observe(this) {
            adapterPost?.setUsers(it)
            adapterPost?.notifyDataSetChanged()
        }

        adapterPost?.onClickLike = { post ->
            handleLikeClicked(post)
        }

        adapterPost?.onClickItem = { post ->
            findNavController().navigate(
                R.id.action_profileFragment_to_displayImageFragment, bundleOf(
                    Pair("post", post)
                )
            )
        }

        viewModel.postList.observe(this) { posts ->
            val sortedFriendPosts = posts.sortedByDescending { it.dateTimePost }
            postList.addAll(sortedFriendPosts)
            adapterPost?.notifyDataSetChanged()
        }

        viewModel.postList.observe(this) {
            adapterPost?.updatePostList(it as ArrayList<Post>)
            adapterPost?.notifyDataSetChanged()
        }

        viewModel.postAllList.observe(this) {
            adapterPost?.setPostShares(it)
            adapterPost?.notifyDataSetChanged()
        }

        adapterPost?.onClickMenu = { post, position ->
            dialog?.show()
            postPin = position
            if (post.isPinned) {
                bindingMenuPost?.btnPostPin?.text = "Bỏ ghim"
                val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_uppin)
                bindingMenuPost?.btnPostPin?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
            } else {
                bindingMenuPost?.btnPostPin?.text = "Ghim bài viết"
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_post_pins)
                bindingMenuPost?.btnPostPin?.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
            }

            bindingMenuPost?.btnPostPin?.setOnSingClickListener {
                if (!post.isPinned) {
                    post.isPinned = !post.isPinned
                    viewModel.updatePost(post)
                    if (post.isPinned) {
                        for (item in postList) {
                            if (item != post) {
                                item.isPinned = false
                                viewModel.updatePost(item)
                                bindingMenuPost?.btnPostPin?.text = "Bỏ ghim"
                            }
                            break
                        }
                    }
                    dialog?.dismiss()
                } else {
                    post.isPinned = false
                    viewModel.updatePost(post)
                    bindingMenuPost?.btnPostPin?.text = "Ghim bài viết"
                    adapterPost?.notifyDataSetChanged()
                    dialog?.dismiss()
                }
                adapterPost?.notifyItemChanged(postPin)
            }

            bindingMenuPost?.btnHandlePermission?.setOnSingClickListener {
                findNavController().navigate(
                    R.id.action_profileFragment_to_permissionFragment, bundleOf(
                        Pair("post", post),
                        Pair(KEY_PROFILE_EDIT_POST, KEY_PROFILE_EDIT_POST)
                    )
                )
                dialog?.dismiss()
            }

            bindingMenuPost?.btnEditPost?.setOnSingClickListener {
                findNavController().navigate(
                    R.id.action_profileFragment_to_editPostFragment, bundleOf(
                        Pair("post", post),
                        Pair(KEY_PROFILE_EDIT_POST, KEY_PROFILE_EDIT_POST)
                    )
                )

                dialog?.dismiss()
            }

            bindingMenuPost?.btnDeletePost?.setOnSingClickListener {
                viewModel.deletePost(post)
                adapterPost?.removePost(post)
                dialog?.dismiss()
            }

            dialog?.setContentView(bindingMenuPost?.root!!)
        }

        adapterPost?.onClickShare = {
            dialog?.show()
            bindingSharePost = BottomDialogSharePostBinding.inflate(layoutInflater)
            dialog?.setContentView(bindingSharePost?.root!!)
        }

        adapterPost?.onClickComment = { post ->
            if (!post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_profileFragment_to_seeCommentsFragment,
                    bundleOf(Pair("post", post))
                )
            } else if (post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_profileFragment_to_seenCommentAvatarFragment,
                    bundleOf(Pair("post", post))
                )
            } else {
                findNavController().navigate(
                    R.id.action_profileFragment_to_seeCommentSharePostFragment,
                    bundleOf(Pair("post", post))
                )
            }
        }

    }

    private fun handleLikeClicked(post: Post?) {
        post?.let { clickedPost ->
            val currentUser = viewModel.getIdPreferences()

            // Kiểm tra xem người dùng đã like bài post trước đó chưa
            val isLikedByCurrentUser = clickedPost.likes.any { it.likeUserId == currentUser }

            if (isLikedByCurrentUser) {
                // Nếu đã like trước đó, thì xóa like của người dùng
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    clickedPost.likes.removeIf { it.likeUserId == currentUser }
                    clickedPost.liked = false
                }
            } else {
                // Ngược lại, thêm like của người dùng
                clickedPost.liked = true
                val like = Like()
                like.likeUserId = currentUser
                like.likeId = System.currentTimeMillis()
                viewModel.userId.observe(this) {
                    like.nameUser = it.nameUser
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

    private fun switchScreen() {
        binding.btnPost.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_postsAnArticleFragment)
        }

        binding.btnSearchFriend.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_findNewFriendsFragment)
        }

        binding.btnSendInformation.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_sendInformationFragment)
        }

        binding.btnEditInformation.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editInformationFragment)
        }

        binding.btnMenu.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingPageUserFragment)
        }

        binding.btnEditPublicInformation.setOnSingClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editInformationFragment)
        }


        viewModel.getInformationUser(viewModel.getIdPreferences())
        viewModel.userId.observe(this) { user ->
            binding.btnImage.setOnSingClickListener {
                findNavController().navigate(
                    R.id.action_profileFragment_to_seeImagesFragment, bundleOf(
                        Pair("user", user)
                    )
                )
            }
        }
    }

    private fun createBottomDialogImageCover() {
        bindingDialogCover = BottomDialogProfileCoverImageBinding.inflate(layoutInflater)

        bindingDialogCover?.btnCoverImage?.setOnSingClickListener {
            isUpdatingProfileImage = false
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForImageUser.launch(intent)
                dialog?.dismiss()
            } else {
                requestPermissionLauncher.launch(permission)
            }

        }

        bindingDialogCover?.btnSendImage?.setOnSingClickListener {
//            findNavController().navigate(R.id.action_profileFragment_to_displayImageFragment)
            dialog?.dismiss()
        }

        bindingDialogCover?.btnAvatarFacebook?.setOnSingClickListener {
            Toast.makeText(requireContext(), "Tính năng đang phát", Toast.LENGTH_SHORT).show()
        }

        bindingDialogCover?.btnImageFacebook?.setOnSingClickListener {
            Toast.makeText(requireContext(), "Tính năng đang phát", Toast.LENGTH_SHORT).show()
        }

        bindingDialogCover?.btnCreateTeamImage?.setOnSingClickListener {
            Toast.makeText(requireContext(), "Tính năng đang phát", Toast.LENGTH_SHORT).show()
        }

        dialog?.setContentView(bindingDialogCover?.root!!)
    }

    private fun createBottomDialogImageUser() {
        bindingDialog = BottomDialogProfileAvatarBinding.inflate(layoutInflater)

        bindingDialog?.btnProfileUser?.setOnSingClickListener {
            isUpdatingProfileImage = true
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForImageUser.launch(intent)
                dialog?.dismiss()
            } else {
                requestPermissionLauncher.launch(permission)
            }
        }

        bindingDialog?.btnAvatarFacebook?.setOnSingClickListener {
            Toast.makeText(requireContext(), "Đang trong quá trình phát triển", Toast.LENGTH_SHORT)
                .show()
            dialog?.dismiss()
        }

        bindingDialog?.btnSeenAvatar?.setOnSingClickListener {
//            findNavController().navigate(R.id.action_profileFragment_to_displayImageFragment, bundleOf(
//                Pair("avatar", KEY_AVATAR)
//            ))
            dialog?.dismiss()
        }


        dialog?.setContentView(bindingDialog?.root!!)
    }

    private fun handleInformation() {
        viewModel.userId.observe(this) {
            if (it.workExperience != null) {
                binding.tvWork.text = it.workExperience?.nameWork
                val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_work)
                binding.tvWork.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                binding.tvWork.visibility = View.VISIBLE
            } else {
                binding.tvWork.visibility = View.GONE
                binding.tvWork.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.nameUser != null) {
                binding.tvNameUser.text = it.nameUser
                binding.tvNameUser.visibility = View.VISIBLE
            } else {
                binding.tvNameUser.visibility = View.GONE
            }

            if (it.story != null) {
                binding.tvStory.text = it.story?.nameStory
                binding.tvStory.visibility = View.VISIBLE
            } else {
                binding.tvStory.visibility = View.GONE
            }

            if (it.colleges != null) {
                binding.tvColleges.text = it.colleges?.nameColleges
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
                binding.tvColleges.visibility = View.VISIBLE
            } else {
                binding.tvColleges.visibility = View.GONE
                binding.tvColleges.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.highSchool != null) {
                binding.tvHighSchool.text = it.highSchool?.nameHighSchool
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
                binding.tvHighSchool.visibility = View.VISIBLE
            } else {
                binding.tvHighSchool.visibility = View.GONE
                binding.tvHighSchool.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.currentCityAndProvince != null) {
                binding.tvCurrentCity.text = it.currentCityAndProvince?.nameCurrentProvinceOrCity
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_address_location_map)
                binding.tvCurrentCity.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                binding.tvCurrentCity.visibility = View.VISIBLE
            } else {
                binding.tvCurrentCity.visibility = View.GONE
                binding.tvCurrentCity.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.homeTown != null) {
                binding.tvHomeTown.text = it.homeTown?.nameHomeTown
                val drawableLeft =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_address_location_map)
                binding.tvHomeTown.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                binding.tvHomeTown.visibility = View.VISIBLE
            } else {
                binding.tvHomeTown.visibility = View.GONE
                binding.tvHomeTown.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.relationship != null) {
                binding.tvRelationship.text = it.relationship?.nameRelationship
                val drawableLeft = ContextCompat.getDrawable(requireContext(), R.drawable.ic_love)
                binding.tvRelationship.setCompoundDrawablesWithIntrinsicBounds(
                    drawableLeft,
                    null,
                    null,
                    null
                )
                binding.tvRelationship.visibility = View.VISIBLE
            } else {
                binding.tvRelationship.visibility = View.GONE
                binding.tvRelationship.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }

            if (it.avatarUser != null) {
                Glide.with(requireContext()).load(it.avatarUser).into(binding.btnAvatarUser)
                Glide.with(requireContext()).load(it.avatarUser).into(binding.imgAvatar)
            }

            if (it.coverImageUser != null) {
                Glide.with(requireContext()).load(it.coverImageUser).into(binding.imgCoverImageUser)
            }

            if (it.nameUser != null) {
                binding.abProfile.setName(it.nameUser!!)
            }
        }
    }

    override fun onDestroy() {
        unregisterLauncher()
        super.onDestroy()
    }

    private fun unregisterLauncher() {
        requestPermissionLauncher.unregister()
        startActivityForImageUser.unregister()
    }
}
