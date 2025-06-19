package arch.cayenne.module.betslip.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.PullRefreshLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BaseBetSlipViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipOtherSettingViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseBetSlipFragment<VM: BaseBetSlipViewModel, VB : ViewBinding>: BaseFragment<VM, VB>() {

    private val filterViewModel: BetSlipFilterViewModel? by lazy {
        try {
            ViewModelProvider(requireParentFragment())[BetSlipFilterViewModel::class.java]
        } catch (e: Exception) {
            null
        }
    }

    protected val settingViewModel: BetSlipOtherSettingViewModel by viewModel()

    protected abstract val betSlipAdapter: BetSlipAdapter

    abstract fun getBetSlipEnum(): BetSlipEnum

    override fun createObserver() {
        filterViewModel?.apply {
            onFilterChangeListener.observe(viewLifecycleOwner) {
                mViewModel.setIds(it.matchId, it.sportIds)
                mViewModel.setTime(it.startTime, it.endTime)
                mViewModel.refreshData(getBetSlipEnum())
            }
        }
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            updateState(it)
        }
    }

    override fun initData() {
        super.initData()
        if (filterViewModel == null) {
            mViewModel.setIds(-1, -1)
            mViewModel.setTime(null, null)
            mViewModel.refreshData(getBetSlipEnum())
        }
    }

    private fun updateState(state: DataState){
         val refreshLayout = mBinding.root.findViewById<PullRefreshLayout>(R.id.refreshLayout)
         val emptyState = mBinding.root.findViewById<DynamicStateLayout>(R.id.empty_state)
         val recyclerView = mBinding.root.findViewById<RecyclerView>(R.id.recyclerView)
         refreshLayout.setEnableLoadMore(mViewModel.canLoadMore())
         refreshLayout.finishRefresh()
         refreshLayout.finishLoadMore()

         when(state){
             DataState.DataEmpty,
             DataState.NetworkUnavailable ->{
                 emptyState.showEmptyData(true, recyclerView)
                 val resId = if(DataState.DataEmpty == state)  R.string.lineup_empty else  arch.cayenne.lib.common.R.string.error_net
                 val newState = if (DataState.DataEmpty == state) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NETWORK_ANOMALY
                 emptyState.setState(newState,getString(resId))
             }
             else -> {
                 emptyState.showEmptyData(false, recyclerView)
             }
         }
    }

    override fun onDestroy() {
        mViewModel.deleteAll()
        super.onDestroy()
    }
}