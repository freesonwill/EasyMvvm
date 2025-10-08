package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderSportPageBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.viewmodel.OrderSportPageViewModel
import kotlin.reflect.KClass

class OrderSportPageFragment : BaseFragment<OrderSportPageViewModel, FragmentOrderSportPageBinding>() {

    override val vbClass: KClass<FragmentOrderSportPageBinding> = FragmentOrderSportPageBinding::class
    override val vmClass: KClass<OrderSportPageViewModel> = OrderSportPageViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val pageIndex = arguments?.getInt("pageIndex") ?: 0
        val page = OrderSportPageEnum.entries[pageIndex]
        mBinding.tvTitle.text = page.page.title
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}