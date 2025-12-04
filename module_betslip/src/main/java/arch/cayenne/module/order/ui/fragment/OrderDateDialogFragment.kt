package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateDialogBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateDialogViewModel
import kotlin.reflect.KClass

class OrderDateDialogFragment: BaseFragment<OrderDateDialogViewModel, FragmentOrderDateDialogBinding>() {

    override val vbClass: KClass<FragmentOrderDateDialogBinding> = FragmentOrderDateDialogBinding::class
    override val vmClass: KClass<OrderDateDialogViewModel> = OrderDateDialogViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}