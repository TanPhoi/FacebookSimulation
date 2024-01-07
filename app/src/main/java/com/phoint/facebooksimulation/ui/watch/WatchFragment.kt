package com.phoint.facebooksimulation.ui.watch

import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.FragmentWatchBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment

class WatchFragment : BaseFragment<FragmentWatchBinding, WatchViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_watch
    }

    override fun viewModelClass(): Class<WatchViewModel> {
        return WatchViewModel::class.java
    }

    override fun initView() {

    }

}