package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.handicap.databinding.FragmentHandicapSizeBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapSizeViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的大小页面
 */

class HandicapSizeFragment : BaseFragment<HandicapSizeViewModel, FragmentHandicapSizeBinding>() {

    override val vbClass: KClass<FragmentHandicapSizeBinding> = FragmentHandicapSizeBinding::class
    override val vmClass: KClass<HandicapSizeViewModel> = HandicapSizeViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}