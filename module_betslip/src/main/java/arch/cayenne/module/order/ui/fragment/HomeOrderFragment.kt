package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentHomeOrderBinding
import arch.cayenne.module.order.ui.viewmodel.HomeOrderViewModel
import kotlin.reflect.KClass

class HomeOrderFragment: BaseFragment<HomeOrderViewModel, FragmentHomeOrderBinding>() {

    override val vbClass: KClass<FragmentHomeOrderBinding> = FragmentHomeOrderBinding::class
    override val vmClass: KClass<HomeOrderViewModel> = HomeOrderViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}