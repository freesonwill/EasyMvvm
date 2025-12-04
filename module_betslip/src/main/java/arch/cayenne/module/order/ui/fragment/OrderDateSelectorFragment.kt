package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateSelectorBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateSelectorViewModel
import kotlin.reflect.KClass

class OrderDateSelectorFragment: BaseFragment<OrderDateSelectorViewModel, FragmentOrderDateSelectorBinding>() {

    override val vbClass: KClass<FragmentOrderDateSelectorBinding> = FragmentOrderDateSelectorBinding::class
    override val vmClass: KClass<OrderDateSelectorViewModel> = OrderDateSelectorViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}