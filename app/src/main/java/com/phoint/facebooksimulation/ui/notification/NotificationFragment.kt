package com.phoint.facebooksimulation.ui.notification

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Notification
import com.phoint.facebooksimulation.databinding.BottomDialogNotificationBinding
import com.phoint.facebooksimulation.databinding.FragmentNotificationBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationViewModel>() {
    private var adapter: NotificationAdapter? = null
    private val notificationList = ArrayList<Notification>()
    private var dialog: BottomSheetDialog? = null
    private var bindingNotification: BottomDialogNotificationBinding? = null
    override fun layoutRes(): Int {
        return R.layout.fragment_notification
    }

    override fun viewModelClass(): Class<NotificationViewModel> {
        return NotificationViewModel::class.java
    }

    override fun initView() {
        binding.rcvNotification.visibility = View.GONE
        binding.shimmerContainer.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.visibility = View.GONE
            binding.rcvNotification.visibility = View.VISIBLE
        }, 2000)

        handleActionBar()
        handleAdapterNotification()
    }

    private fun handleAdapterNotification() {
        adapter = NotificationAdapter(notificationList)
        binding.rcvNotification.adapter = adapter

        viewModel.getAllNotificationById(viewModel.getCurrentUserId(), viewModel.getCurrentUserId())
        viewModel.notificationList.observe(this) {
            notificationList.clear()
            notificationList.addAll(it)
            adapter?.notifyDataSetChanged()

            if (notificationList.isEmpty()) {
                binding.tvHideNotification.visibility = View.VISIBLE
                binding.rcvNotification.visibility = View.GONE
            } else {
                binding.tvHideNotification.visibility = View.GONE
                binding.rcvNotification.visibility = View.VISIBLE
            }
        }

        viewModel.informationUserIdList.observe(this) {
            adapter?.setUsers(it)
            adapter?.notifyDataSetChanged()
        }

        adapter?.onItemClick = {
            it.viewed = true
            viewModel.updateNotification(it)
            viewModel.getPostById(it.postId!!)
        }

        viewModel.isPost.observe(this) {
            if (it.isAvatar) {
                findNavController().navigate(
                    R.id.action_userNavigationFragment_to_seenCommentAvatarFragment, bundleOf(
                        Pair("post", it)
                    )
                )
            } else {
                findNavController().navigate(
                    R.id.action_userNavigationFragment_to_otherPeopleCommentFragment, bundleOf(
                        Pair("post", it)
                    )
                )
            }
        }

        adapter?.onClickMenu = { notification ->
            viewModel.getCurrentUser(notification.senderId!!)
            bindingNotification = BottomDialogNotificationBinding.inflate(layoutInflater)
            dialog = BottomSheetDialog(requireContext())
            dialog?.create()
            dialog?.show()

            viewModel.currentUser.observe(this) { user ->
                Glide.with(requireContext()).load(user.avatarUser)
                    .into(bindingNotification?.imgAvatar!!)
            }

            bindingNotification?.imgIcon?.setImageResource(notification.profilePicture!!)

            bindingNotification?.tvContent?.text = notification.notificationType

            bindingNotification?.btnDeleteNotification?.setOnSingClickListener {
                viewModel.deleteNotification(notification)
                val position = notificationList.indexOf(notification)
                if (position != -1) {
                    adapter?.removeItem(position)
                }
                dialog?.dismiss()
            }

            dialog?.setContentView(bindingNotification?.root!!)
        }
    }

    private fun handleActionBar() {
        binding.abNotification.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_userNavigationFragment_to_searchHistoryFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllNotificationById(viewModel.getCurrentUserId(), viewModel.getCurrentUserId())
    }
}
