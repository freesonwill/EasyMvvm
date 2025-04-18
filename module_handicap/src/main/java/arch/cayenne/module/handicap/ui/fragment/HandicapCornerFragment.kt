package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.handicap.databinding.FragmentHandicapCornerBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapCornerViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的角球页面
 */

class HandicapCornerFragment :
    BaseFragment<HandicapCornerViewModel, FragmentHandicapCornerBinding>() {

    override val vbClass: KClass<FragmentHandicapCornerBinding> =
        FragmentHandicapCornerBinding::class
    override val vmClass: KClass<HandicapCornerViewModel> = HandicapCornerViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}