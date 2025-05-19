package arch.cayenne.module.betslip.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.ui.viewmodel.HomeBetSlipViewModel
import kotlin.reflect.KClass

abstract class BaseBetSlipFragment<VB : ViewBinding>: BaseFragment<BetSlipViewModel, VB>() {

    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class
    private val homeSlipViewModel: HomeBetSlipViewModel? by lazy {
        try {
            ViewModelProvider(requireParentFragment())[HomeBetSlipViewModel::class.java]
        } catch (e: Exception) {
            null
        }
    }

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey,-1) ?: -1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey,-1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        mViewModel.loadData(getBetSlipEnum())
    }

    abstract fun getBetSlipEnum(): BetSlipEnum?

    override fun createObserver() {
        homeSlipViewModel?.onDateTime?.observe(viewLifecycleOwner) {
            mViewModel.setTime(it.first, it.second)
            mViewModel.loadData(getBetSlipEnum())
        }
    }
}