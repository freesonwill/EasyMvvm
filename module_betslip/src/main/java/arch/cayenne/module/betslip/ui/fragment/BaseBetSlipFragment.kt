package arch.cayenne.module.betslip.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import kotlin.reflect.KClass

abstract class BaseBetSlipFragment<VB : ViewBinding>: BaseFragment<BetSlipViewModel, VB>() {

    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class
    private val filterViewModel: BetSlipFilterViewModel? by lazy {
        try {
            ViewModelProvider(requireParentFragment())[BetSlipFilterViewModel::class.java]
        } catch (e: Exception) {
            null
        }
    }

    protected val betSlipAdapter: BetSlipAdapter by lazy {
        BetSlipAdapter(getBetSlipEnum())
    }

    abstract fun getBetSlipEnum(): BetSlipEnum

    override fun createObserver() {
        filterViewModel?.apply {
            onFilterChangeListener.observe(viewLifecycleOwner) {
                mViewModel.setIds(it.matchId, it.sportId)
                mViewModel.setTime(it.startTime, it.endTime)
                mViewModel.loadData(getBetSlipEnum())
            }
        }
    }

    override fun initData() {
        super.initData()
        if (filterViewModel == null) {
            mViewModel.setIds(-1, -1)
            mViewModel.setTime(null, null)
            mViewModel.loadData(getBetSlipEnum())
        }
    }
}