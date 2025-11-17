package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentGameOrderAllBinding
import arch.cayenne.module.order.ui.viewmodel.GameAllViewModel
import kotlin.reflect.KClass

/**
 * 投注记录-游戏-全部游戏列表页
 */

class GameOrderAllFragment : BaseFragment<GameAllViewModel, FragmentGameOrderAllBinding>() {

    override val vbClass: KClass<FragmentGameOrderAllBinding> = FragmentGameOrderAllBinding::class
    override val vmClass: KClass<GameAllViewModel> = GameAllViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}