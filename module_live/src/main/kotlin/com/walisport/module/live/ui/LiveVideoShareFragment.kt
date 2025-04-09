package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass

class LiveVideoShareFragment : BaseFragment<LiveVideoViewModel, FragmentLiveShareBinding>() {
    override val vbClass: KClass<FragmentLiveShareBinding> = FragmentLiveShareBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }


}