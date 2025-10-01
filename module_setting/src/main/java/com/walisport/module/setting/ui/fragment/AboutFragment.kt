package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.setting.databinding.FragmentAboutBinding
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 关于我们界面
 */

class AboutFragment : BaseFragment<SettingViewModel, FragmentAboutBinding>() {

    override val vbClass: KClass<FragmentAboutBinding> = FragmentAboutBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}