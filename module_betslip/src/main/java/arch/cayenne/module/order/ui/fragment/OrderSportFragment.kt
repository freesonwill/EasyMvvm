package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderSportBinding
import arch.cayenne.module.order.ui.viewmodel.OrderSportViewModel
import kotlin.reflect.KClass

class OrderSportFragment : BaseFragment<OrderSportViewModel, FragmentOrderSportBinding>() {

    override val vbClass: KClass<FragmentOrderSportBinding> = FragmentOrderSportBinding::class
    override val vmClass: KClass<OrderSportViewModel> = OrderSportViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}