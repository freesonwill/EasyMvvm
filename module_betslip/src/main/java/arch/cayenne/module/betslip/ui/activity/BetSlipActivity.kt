package arch.cayenne.module.betslip.ui.activity

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.module.betslip.databinding.ActivityBetslipBinding
import arch.cayenne.module.betslip.ui.fragment.BetSlipFragment
import kotlin.reflect.KClass

class BetSlipActivity: BaseActivity<EmptyViewModel, ActivityBetslipBinding>() {
    override val vbClass: KClass<ActivityBetslipBinding> = ActivityBetslipBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val f = BetSlipFragment()
        supportFragmentManager.beginTransaction()
            .replace(mBinding.fragmentContainer.id, f, f::class.java.simpleName)
            .commitAllowingStateLoss()
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}