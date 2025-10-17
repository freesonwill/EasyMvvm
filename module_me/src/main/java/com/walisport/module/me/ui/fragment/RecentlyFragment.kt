package com.walisport.module.me.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.walisport.module.me.databinding.FragmentRecentlyBinding
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/10/17 16:52
 * @description:
 */
class RecentlyFragment : BaseFragment<MeVIPInfoViewModel, FragmentRecentlyBinding>() {

    override val vbClass: KClass<FragmentRecentlyBinding> = FragmentRecentlyBinding::class
    override val vmClass: KClass<MeVIPInfoViewModel> = MeVIPInfoViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
        "".logd("")
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}