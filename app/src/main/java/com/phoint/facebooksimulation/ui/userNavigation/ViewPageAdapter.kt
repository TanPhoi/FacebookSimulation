package com.phoint.facebooksimulation.ui.userNavigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phoint.facebooksimulation.ui.friend.FriendFragment
import com.phoint.facebooksimulation.ui.home.HomeFragment
import com.phoint.facebooksimulation.ui.menu.MenuFragment
import com.phoint.facebooksimulation.ui.notification.NotificationFragment
import com.phoint.facebooksimulation.ui.profile.ProfileFragment

class ViewPageAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FriendFragment()
            2 -> NotificationFragment()
            3 -> MenuFragment()
            4 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}