package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.module.handicap.databinding.FragmentHandicapLetBallBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapLetBallViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的让球页面
 */

class HandicapLetBallFragment : BaseFragment<HandicapLetBallViewModel, FragmentHandicapLetBallBinding>() {

    override val vbClass: KClass<FragmentHandicapLetBallBinding> = FragmentHandicapLetBallBinding::class
    override val vmClass: KClass<HandicapLetBallViewModel> = HandicapLetBallViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }


}