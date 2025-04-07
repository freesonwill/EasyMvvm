package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.live.databinding.FragmentLiveStandingsBinding
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import kotlin.reflect.KClass

//积分榜
class LiveStandingsFragment : BaseFragment<LiveStandingsViewModel, FragmentLiveStandingsBinding>() {

    override val vbClass: KClass<FragmentLiveStandingsBinding> = FragmentLiveStandingsBinding::class
    override val vmClass: KClass<LiveStandingsViewModel> = LiveStandingsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}