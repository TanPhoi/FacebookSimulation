package com.phoint.facebooksimulation.ui.menu

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.ChildSupport
import com.phoint.facebooksimulation.data.local.model.ParentSupport
import com.phoint.facebooksimulation.data.local.model.Shortcut
import com.phoint.facebooksimulation.databinding.FragmentMenuBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener


class MenuFragment : BaseFragment<FragmentMenuBinding, MenuViewModel>() {
    private val listShortcut = ArrayList<Shortcut>()
    private var isShowingAllItems = false
    private var adapterShortcut: ShortcutsAdapter? = null
    private var isObservingUserID = false

    private val listParentSupport = ArrayList<ParentSupport>()
    private var adapterParentSupport: ParentSupportAdapter? = null

    override fun layoutRes(): Int {
        return R.layout.fragment_menu
    }

    override fun viewModelClass(): Class<MenuViewModel> {
        return MenuViewModel::class.java
    }

    override fun initView() {
        setDateShortcut()
        initAdapterShortcut()
        prepareData()
        handleGetDataUser()
        handleAdapterParentSupport()
        handleActionBar()
    }

    private fun handleActionBar() {
        binding.abMenu.setAbSearchOtherUser {
            findNavController().navigate(R.id.action_userNavigationFragment_to_searchHistoryFragment)
        }
    }

    private fun handleAdapterParentSupport() {
        adapterParentSupport?.onItemChildSupport = { parentSupport, childSupport ->
            when (childSupport.title) {
                "Cài đặt" ->
                    findNavController().navigate(R.id.action_userNavigationFragment_to_settingFragment)
            }
        }
    }

    private fun handleGetDataUser() {
        val currentUserId = viewModel.getCurrentUserIdPreferences()
        viewModel.getUserById(currentUserId)

        viewModel.currentUserId.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvName.text = user.nameUser
                if (user.avatarUser != null) {
                    Glide.with(requireContext()).load(user.avatarUser).into(binding.imgUser)
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun initAdapterShortcut() {
        adapterShortcut = ShortcutsAdapter(listShortcut)
        binding.rcvShortcut.adapter = adapterShortcut

        binding.btnProfile.setOnSingClickListener {
            findNavController().navigate(R.id.action_userNavigationFragment_to_profileFragment)
        }

        adapterShortcut?.itemOnClick = {
            when (it?.title) {
                "Friend" -> {
                    findNavController().navigate(R.id.action_userNavigationFragment_to_userOfFriendFragment)
                }

                "Saved" -> {
                    findNavController().navigate(R.id.action_userNavigationFragment_to_saveFavoritePostsFragment)
                }

                "Reels" -> {
                    Toast.makeText(requireContext(), "Reels", Toast.LENGTH_SHORT).show()
                }

                "Play game" -> {

                }

                "Broad feed" -> {

                }

                "Event" -> {

                }

                "Team" -> {

                }

                "Stories" -> {

                }

                "Avatar" -> {

                }

                "Marketplace" -> {

                }

                "Endow" -> {

                }

                "Watch video" -> {

                }

                "Live" -> {

                }

                "Đơn đặt hàng và thanh toán" -> {

                }

                "Page" -> {

                }

                "Messenger min" -> {

                }

                "Game giả tưởng" -> {

                }

                "Kỷ niệm" -> {

                }

                "Hẹn hò" -> {

                }
            }
        }

        binding.btnShowItem.setOnClickListener {
            isShowingAllItems = !isShowingAllItems
            if (isShowingAllItems) {
                adapterShortcut?.showAllItems()
                binding.btnShowItem.text = "Ẩn bớt"
            } else {
                adapterShortcut?.showLimitedItems()
                binding.btnShowItem.text = "Xem thêm"
            }
        }

        binding.btnLogout.setOnSingClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_userNavigationFragment_to_loginFragment)
        }
    }

    private fun setDateShortcut() {
        val shortcut = Shortcut().apply {
            title = "Friend"
            url = R.drawable.ic_friend_blue
        }

        val shortcut2 = Shortcut().apply {
            title = "Saved"
            url = R.drawable.ic_save
        }

        val shortcut3 = Shortcut().apply {
            title = "Reels"
            url = R.drawable.ic_reels
        }

        val shortcut4 = Shortcut().apply {
            title = "Play game"
            url = R.drawable.ic_play_game
        }

        val shortcut5 = Shortcut().apply {
            title = "Broad feed"
            url = R.drawable.ic_feed
        }

        val shortcut6 = Shortcut().apply {
            title = "Event"
            url = R.drawable.ic_event
        }

        val shortcut7 = Shortcut().apply {
            title = "Team"
            url = R.drawable.ic_team
        }

        val shortcut8 = Shortcut().apply {
            title = "Stories"
            url = R.drawable.ic_stoties
        }

        val shortcut9 = Shortcut().apply {
            title = "Avatar"
            url = R.drawable.ic_avater
        }

        val shortcut10 = Shortcut().apply {
            title = "Marketplace"
            url = R.drawable.ic_markertplace
        }


        val shortcut11 = Shortcut().apply {
            title = "Endow"
            url = R.drawable.ic_endow
        }


        val shortcut12 = Shortcut().apply {
            title = "Watch video"
            url = R.drawable.ic_watch_blue
        }

        val shortcut14 = Shortcut().apply {
            title = "Live"
            url = R.drawable.ic_live
        }

        val shortcut15 = Shortcut().apply {
            title = "Đơn đặt hàng và thanh toán"
            url = R.drawable.ic_card
        }

        val shortcut16 = Shortcut().apply {
            title = "Page"
            url = R.drawable.ic_page
        }

        val shortcut17 = Shortcut().apply {
            title = "Messenger min"
            url = R.drawable.ic_messenger
        }

        val shortcut18 = Shortcut().apply {
            title = "Game giả tưởng"
            url = R.drawable.ic_game
        }

        val shortcut19 = Shortcut().apply {
            title = "Kỷ niệm"
            url = R.drawable.ic_watch_celebrate
        }

        val shortcut20 = Shortcut().apply {
            title = "Hẹn hò"
            url = R.drawable.ic_love
        }

        listShortcut.add(shortcut)
        listShortcut.add(shortcut2)
        listShortcut.add(shortcut3)
        listShortcut.add(shortcut4)
        listShortcut.add(shortcut5)
        listShortcut.add(shortcut6)
        listShortcut.add(shortcut7)
        listShortcut.add(shortcut8)
        listShortcut.add(shortcut9)
        listShortcut.add(shortcut10)
        listShortcut.add(shortcut11)
        listShortcut.add(shortcut12)
        listShortcut.add(shortcut14)
        listShortcut.add(shortcut15)
        listShortcut.add(shortcut16)
        listShortcut.add(shortcut17)
        listShortcut.add(shortcut18)
        listShortcut.add(shortcut19)
        listShortcut.add(shortcut20)
    }

    private fun prepareData() {
        adapterParentSupport = ParentSupportAdapter(listParentSupport)
        binding.rcvSupport.adapter = adapterParentSupport

        binding.rcvSupport.setHasFixedSize(true)
        binding.rcvSupport.isNestedScrollingEnabled = false

        val childSupport1 = ArrayList<ChildSupport>()
        childSupport1.add(ChildSupport("Trung tâm trợ giúp", R.drawable.ic_help))
        childSupport1.add(ChildSupport("Hợp thư hỗ trợ", R.drawable.ic_mailbox))
        childSupport1.add(ChildSupport("Báo cáo sự cố", R.drawable.ic_alert_fill))
        childSupport1.add(
            ChildSupport(
                "Điều khoản & chính sách",
                R.drawable.ic_file_document_black
            )
        )
        val parentSupport1 =
            ParentSupport("Trợ giúp & hỗ trợ", R.drawable.ic_question_mark, childSupport1)
        listParentSupport.add(parentSupport1)

        val childSupport2 = ArrayList<ChildSupport>()
        childSupport2.add(ChildSupport("Cài đặt", R.drawable.ic_setting_white))
        childSupport2.add(ChildSupport("Yêu cầu từ thiết bị", R.drawable.ic_mobile))
        childSupport2.add(ChildSupport("Hoạt động quảng cáo gần đây", R.drawable.ic_advertised))
        childSupport2.add(ChildSupport("Tìm Wi-Fi", R.drawable.ic_wifi))
        val parentSupport2 =
            ParentSupport("Cài đặt & quyền riêng tư", R.drawable.ic_setting_white, childSupport2)
        listParentSupport.add(parentSupport2)

        val childSupport3 = ArrayList<ChildSupport>()
        childSupport3.add(ChildSupport("Trình quản lý quảng cáo", R.drawable.ic_advertised))
        val parentSupport3 =
            ParentSupport("Cũng từ Meta", R.drawable.ic_menu_setting, childSupport3)
        listParentSupport.add(parentSupport3)
    }

    override fun onStart() {
        super.onStart()
        handleGetDataUser()
    }

    override fun onResume() {
        super.onResume()
        handleGetDataUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentUserId.removeObservers(requireActivity())
    }
}
