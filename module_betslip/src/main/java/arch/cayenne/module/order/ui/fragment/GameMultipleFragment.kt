package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameMultipleBinding
import arch.cayenne.module.order.ui.viewmodel.GameMultipleViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-最大倍数列表页
 */

class GameMultipleFragment : BaseFragment<GameMultipleViewModel, FragmentGameMultipleBinding>() {

    override val vbClass: KClass<FragmentGameMultipleBinding> = FragmentGameMultipleBinding::class
    override val vmClass: KClass<GameMultipleViewModel> = GameMultipleViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}