package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.PositionedDialogFragment
import com.walisport.module.live.databinding.FragmentLivePortraitSourceBinding
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass

class LiveVideoPortraitSourceFragment:PositionedDialogFragment<LiveVideoViewModel, FragmentLivePortraitSourceBinding>() {

    override val vbClass: KClass<FragmentLivePortraitSourceBinding> = FragmentLivePortraitSourceBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}