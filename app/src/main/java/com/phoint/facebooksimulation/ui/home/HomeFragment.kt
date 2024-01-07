package com.phoint.facebooksimulation.ui.home

import android.annotation.SuppressLint
import android.content.Context
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
import com.phoint.facebooksimulation.data.local.model.HidePost
import com.phoint.facebooksimulation.data.local.model.Like
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.data.local.model.Report
import com.phoint.facebooksimulation.data.local.model.SaveFavoritePost
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.BottomDialogMenuOtherUserBinding
import com.phoint.facebooksimulation.databinding.BottomDialogPermissionBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonRepostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReasonsForReportingBinding
import com.phoint.facebooksimulation.databinding.BottomDialogReportPostBinding
import com.phoint.facebooksimulation.databinding.BottomDialogShareBinding
import com.phoint.facebooksimulation.databinding.BottomDialogUndoNewspaperBinding
import com.phoint.facebooksimulation.databinding.FragmentHomeBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    private var isFragmentAttached: Boolean = false
    private val PUBLIC = "Công khai"
    private val postList = ArrayList<Post>()
    private var adapterPost: PostsAdapter? = null
    private var posts: Post? = null
    val userList = mutableListOf<User>()
    private var isSavePost = false
    private var bindingPermission: BottomDialogPermissionBinding? = null
    private var bindingSharePost: BottomDialogShareBinding? = null
    private var dialog: BottomSheetDialog? = null
    private var bindingUnDoNewspaper: BottomDialogUndoNewspaperBinding? = null
    private var bindingMenu: BottomDialogMenuOtherUserBinding? = null
    private var bindingReportPost: BottomDialogReportPostBinding? = null
    private var bindingReasonReport: BottomDialogReasonRepostBinding? = null
    private var bindingReasonsForReporting: BottomDialogReasonsForReportingBinding? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun viewModelClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isFragmentAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        isFragmentAttached = false
    }


    override fun initView() {
        (activity as BaseActivity<*, *>).hiddenLoading()

        binding.rcvPosts.visibility = View.GONE
        binding.shimmerContainer.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.visibility = View.GONE
            binding.rcvPosts.visibility = View.VISIBLE
        }, 2000)

        handlePostAdapter()
        handleGetDataUser()
        watchProfileUser()

        binding.abHome.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_userNavigationFragment_to_searchHistoryFragment)
        }

        binding.btnAddPost.setOnSingClickListener {
            findNavController().navigate(R.id.action_userNavigationFragment_to_postsAnArticleFragment)
        }
    }

    private fun watchProfileUser() {
        adapterPost?.onClickProfileUser = {
            if (it.userIdPost != viewModel.getIdPreferences()) {
                viewModel.getUserByIdPost(it.userIdPost!!)
            } else {
                findNavController().navigate(R.id.action_userNavigationFragment_to_profileFragment)
            }
        }

        viewModel.userById.observe(this) { user ->
            findNavController().navigate(
                R.id.action_userNavigationFragment_to_profileOtherUserFragment,
                bundleOf(Pair("otherUser", user))
            )
        }
    }

    private fun handlePostAdapter() {
        viewModel.getHiddenPostIds(viewModel.getIdPreferences())

        adapterPost = PostsAdapter(postList, viewModel.getIdPreferences())
        binding.rcvPosts.adapter = adapterPost

        viewModel.getInformationUser(viewModel.getIdPreferences())

        viewModel.getReceiverId(viewModel.getIdPreferences())
        viewModel.receiver.observe(this) { friendList ->
            viewModel.postAllList.observe(this) { posts ->
                viewModel.idHidePostList.observe(this) { hidePostList ->

                    val filteredFriendList = friendList.filter { friendRequest ->
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
                    // Lọc ra các bài viết của bạn bè dựa trên senderId và privacyPost == PUBLIC
                    val friendPosts = posts.filter {
                        (it.userIdPost in friendIds && it.privacyPost == PUBLIC) || (it.userIdPost == viewModel.getIdPreferences())
                    }

                    // Lọc ra danh sách các id bài viết cần ẩn
                    val hiddenPostIds = hidePostList.mapNotNull {
                        it
                    }

                    // Loại bỏ các bài viết có id trong danh sách hiddenPostIds
                    val visibleFriendPosts = friendPosts.filter { post ->
                        post.idPost !in hiddenPostIds
                    }

                    // Sắp xếp danh sách bài viết theo thời gian đăng giảm dần
                    val sortedFriendPosts =
                        visibleFriendPosts.sortedByDescending { it.dateTimePost }

                    // Cập nhật danh sách bài viết trong adapter
                    postList.clear()
                    postList.addAll(sortedFriendPosts)
                    adapterPost?.setPostShares(sortedFriendPosts)
                    adapterPost?.notifyDataSetChanged()
                }
            }
        }

        viewModel.userList.observe(this) {
            adapterPost?.setUsers(it)
            adapterPost?.notifyDataSetChanged()
        }

        adapterPost?.onClickLike = { post ->
            handleLikeClicked(post)
        }

        adapterPost?.onClickItem = { post ->
            findNavController().navigate(
                R.id.action_userNavigationFragment_to_displayImageFragment, bundleOf(
                    Pair("post", post)
                )
            )
        }

        adapterPost?.onClickComment = { post ->
            if (!post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_userNavigationFragment_to_otherPeopleCommentFragment,
                    bundleOf(Pair("post", post))
                )
            } else if (post.isAvatar && !post.isShared) {
                findNavController().navigate(
                    R.id.action_userNavigationFragment_to_seenCommentAvatarFragment,
                    bundleOf(Pair("post", post))
                )
            } else {
                findNavController().navigate(
                    R.id.action_userNavigationFragment_to_seeCommentSharePostFragment,
                    bundleOf(Pair("post", post))
                )
            }
        }

        adapterPost?.onClickDelete = {
            val newHidePost = HidePost().apply {
                idPost = it.idPost
                idUserCurrent = viewModel.getIdPreferences()
                idUserPost = it.userIdPost
            }
            viewModel.insertHidePost(newHidePost)
            // Tìm vị trí của bài viết cần xóa trong danh sách postList
            val position = postList.indexOf(it)

            // Xóa item từ danh sách và cập nhật RecyclerView
            if (position != -1) {
                adapterPost?.removeItem(position)
            }
        }

        handleDialogShare()
        handleDialogMenuPost()
    }

    private fun handleGetDataUser() {
        val currentUserId = viewModel.getIdPreferences()
        viewModel.getUserById(currentUserId)
        viewModel.currentUserId.observe(this) { user ->
            if (user != null) {
                if (user.avatarUser != null) {
                    Glide.with(requireContext()).load(user.avatarUser).into(binding.imgAvatar)
                }
            }
        }
    }

    private fun handleDialogShare() {
        if (!isFragmentAttached) {
            // Fragment chưa được gắn kết, đợi cho đến khi được gắn kết
            return
        }
        bindingSharePost = BottomDialogShareBinding.inflate(layoutInflater)
        bindingPermission = BottomDialogPermissionBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()

        viewModel.userId.observe(this) { user ->
            if (user?.avatarUser != null) {
                Glide.with(requireContext()).load(user.avatarUser)
                    .into(bindingSharePost?.imgUser!!)
            }
            bindingSharePost?.tvName?.text = "${user?.nameUser}"
        }

        adapterPost?.onClickShare = { post ->
            dialog?.show()
            bindingSharePost?.btnShare?.setOnSingClickListener {
                val newPost = Post().apply {
                    idPost = System.currentTimeMillis()
                    userIdPost = viewModel.getIdPreferences()
                    if (post.userIdPostShare != null) {
                        userIdPostShare = post.userIdPostShare
                    } else {
                        userIdPostShare = post.userIdPost
                    }
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
                            if (post.userIdPostShare != null) {
                                userIdPostShare = post.userIdPostShare
                            } else {
                                userIdPostShare = post.userIdPost
                            }
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
                            if (post.userIdPostShare != null) {
                                userIdPostShare = post.userIdPostShare
                            } else {
                                userIdPostShare = post.userIdPost
                            }
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
                            if (post.userIdPostShare != null) {
                                userIdPostShare = post.userIdPostShare
                            } else {
                                userIdPostShare = post.userIdPost
                            }
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

    private fun handleDialogMenuPost() {
        bindingMenu = BottomDialogMenuOtherUserBinding.inflate(layoutInflater)
        bindingReportPost = BottomDialogReportPostBinding.inflate(layoutInflater)
        bindingUnDoNewspaper = BottomDialogUndoNewspaperBinding.inflate(layoutInflater)
        bindingReasonReport = BottomDialogReasonRepostBinding.inflate(layoutInflater)
        bindingReasonsForReporting = BottomDialogReasonsForReportingBinding.inflate(layoutInflater)
        dialog = BottomSheetDialog(requireContext())
        dialog?.create()

        viewModel.savePosts.observe(this) { savePostList ->
            val postId = posts?.idPost ?: 0
            val isPostSaved = savePostList.any { it.savePost?.idPost == postId }
            // Cập nhật nội dung của nút btnSavePost dựa trên trạng thái lưu của bài viết
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
            }
        }

        adapterPost?.onClickMenu = { post, position ->
            viewModel.getOwnerPost(post.idPost ?: 0)
            posts = post
            dialog?.show()

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
                        post?.idPost ?: 0,
                        post?.userIdPost ?: 0
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
                                post.idPost ?: 0,
                                post.userIdPost ?: 0
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

    @SuppressLint("NotifyDataSetChanged")
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
                        isNotification = false
                        viewed = false
                    }
                    viewModel.insertNotification(newNotification)
                }
                clickedPost.likes.add(like)

            }
            // Cập nhật bài post trong ViewModel
            viewModel.updatePost(clickedPost)// Cập nhật chỉ item đó trong danh sách
            val position = postList.indexOf(clickedPost)
            if (position != -1) {
                adapterPost?.notifyItemChanged(position)
            }
        }
    }
}
