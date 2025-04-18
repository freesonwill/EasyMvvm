package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.handicap.databinding.FragmentHandicapSizeBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapBigSmallViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的大小页面
 */

class HandicapBigSmallFragment : BaseFragment<HandicapBigSmallViewModel, FragmentHandicapSizeBinding>() {

    override val vbClass: KClass<FragmentHandicapSizeBinding> = FragmentHandicapSizeBinding::class
    override val vmClass: KClass<HandicapBigSmallViewModel> = HandicapBigSmallViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}