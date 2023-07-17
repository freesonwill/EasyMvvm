package com.xcjh.app.ui.home.schedule

import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.ActivityWebBinding
import com.xcjh.app.vm.MainVm


class ScheduleFragment : BaseFragment<MainVm, ActivityWebBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mViewBind.titleTop.root)
            .init()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBarMarginTop(mViewBind.titleTop.root)
            .init()
    }

    private fun initEvent() {


    }

    override fun createObserver() {
       /* appViewModel.updateLoginEvent.observe(this) {
            mViewModel.getUserBaseInfo()
        }*/
    }



}