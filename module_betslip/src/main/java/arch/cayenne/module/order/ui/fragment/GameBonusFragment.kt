package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameBonusBinding
import arch.cayenne.module.order.ui.viewmodel.GameBonusViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-奖金列表页
 */

class GameBonusFragment: BaseFragment<GameBonusViewModel, FragmentGameBonusBinding>() {

    override val vbClass: KClass<FragmentGameBonusBinding> = FragmentGameBonusBinding::class
    override val vmClass: KClass<GameBonusViewModel> = GameBonusViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}