package arch.cayenne.module.betslip.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.PullRefreshLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
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
        mViewModel.state.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled(viewLifecycleOwner)?.let { states ->
                updateState(states)
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

    private fun updateState(state: DynamicStateLayout.States){
         val refreshLayout = mBinding.root.findViewById<PullRefreshLayout>(R.id.refreshLayout)
         val emptyState = mBinding.root.findViewById<DynamicStateLayout>(R.id.empty_state)
         val recyclerView = mBinding.root.findViewById<RecyclerView>(R.id.recyclerView)
         refreshLayout.setEnableLoadMore( if(getBetSlipEnum() == BetSlipEnum.Reserve) mViewModel.isReserveLoadMore() else mViewModel.isOrderLoadMore())
         refreshLayout.finishRefresh()
         refreshLayout.finishLoadMore()

         when(state){
             DynamicStateLayout.States.DATA_EMPTY,
             DynamicStateLayout.States.NETWORK_ANOMALY ->{
                 emptyState.showEmptyData(true, recyclerView)
                 val resId = if(DynamicStateLayout.States.DATA_EMPTY == state)  R.string.lineup_empty else  arch.cayenne.lib.common.R.string.error_net
                 emptyState.setState(state,getString(resId))
             }
             else -> {
                 emptyState.showEmptyData(false, recyclerView)
             }
         }
     }
}