package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderGameBinding
import arch.cayenne.module.order.ui.viewmodel.OrderGameViewModel
import kotlin.reflect.KClass

class OrderGameFragment : BaseFragment<OrderGameViewModel, FragmentOrderGameBinding>() {

    override val vbClass: KClass<FragmentOrderGameBinding> = FragmentOrderGameBinding::class
    override val vmClass: KClass<OrderGameViewModel> = OrderGameViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}