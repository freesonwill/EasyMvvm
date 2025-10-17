package com.walisport.module.me.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.me.databinding.FragmentGameCollectionsBinding
import com.walisport.module.me.databinding.FragmentMeVipInfoBinding
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/10/17 16:52
 * @description:
 */
class GameCollectionsFragment : BaseFragment<MeVIPInfoViewModel, FragmentGameCollectionsBinding>() {

    override val vbClass: KClass<FragmentGameCollectionsBinding> = FragmentGameCollectionsBinding::class
    override val vmClass: KClass<MeVIPInfoViewModel> = MeVIPInfoViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}