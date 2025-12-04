package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateCustomBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateCustomViewModel
import kotlin.reflect.KClass

class OrderDateCustomFragment: BaseFragment<OrderDateCustomViewModel, FragmentOrderDateCustomBinding>() {

    override val vbClass: KClass<FragmentOrderDateCustomBinding> = FragmentOrderDateCustomBinding::class
    override val vmClass: KClass<OrderDateCustomViewModel> = OrderDateCustomViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}