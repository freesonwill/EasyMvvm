package com.coolone.mvvm

import com.coolone.lib_base.base.BaseVMActivity
import com.coolone.lib_base.network.manager.NetState
import com.coolone.mvvm.databinding.ActivityMainBinding

class MainActivity : BaseVMActivity<ActivityMainBinding, EmptyViewModel>() {


    override fun initObserver() {
        mViewBinding.model = mViewModel

    }

    override fun onNetworkStateChanged(netState: NetState) {

    }

}