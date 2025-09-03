package arch.cayenne.module.betslip.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.view.PullRefreshLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.helper.showToast
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

    private val recyclerView:RecyclerView by lazy { mBinding.root.findViewById(R.id.recyclerView) }
    private val dynamicState:DynamicStateLayout by lazy { mBinding.root.findViewById(R.id.empty_state) }
    private val refreshLayout:PullRefreshLayout by lazy { mBinding.root.findViewById(R.id.refreshLayout) }
    private var firstLoading:Boolean = true

    abstract fun getBetSlipEnum(): BetSlipEnum

    override suspend fun createObserver() {
        filterViewModel?.apply {
            onFilterChangeListener.observe(viewLifecycleOwner) {
                mViewModel.setIds(it.matchId, it.sportIds)
                mViewModel.setTime(it.startTime, it.endTime)
                if(it.matchId == -1L){ //首页注单加载使用
                    refreshData()
                }
            }
        }
        mViewModel.apiStateListener.observe(viewLifecycleOwner) {
            updateState(it)
        }
        mViewModel.networkConnectedEvent.observeEvent(viewLifecycleOwner, this) {
            if (it is DataState.NetworkUnavailable) {
                showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //直播间注单返回单前页后每次都要刷新，首页注单不用每次更新
        if (filterViewModel?.onFilterChangeListener?.value?.matchId != -1L) {
            refreshData()
        }
    }

    private fun refreshData(){
        mViewModel.refreshData(getBetSlipEnum())
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

         refreshLayout.setEnableLoadMore(mViewModel.canLoadMore())
         refreshLayout.finishRefresh()
         refreshLayout.finishLoadMore()

         when(state){
             DataState.DataEmpty,
             DataState.NetworkUnavailable ->{
                 dynamicState.showEmptyData(true, recyclerView)
                 val resId = if(DataState.DataEmpty == state)  R.string.betslip_list_empty else  arch.cayenne.lib.common.R.string.error_net
                 val newState = if (DataState.DataEmpty == state) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NETWORK_ANOMALY()
                 dynamicState.setState(newState,getString(resId))
             }
             DataState.NoMoreData,
             DataState.LoadSuccess -> {
                 dynamicState.showEmptyData(false, recyclerView)
             }
             DataState.Loading ->{
                 if(!firstLoading){ //firstLoading只在第一次加载的时候展示，其余静默加载
                     return
                 }
                 dynamicState.showEmptyData(true, recyclerView)
                 dynamicState.setState(DynamicStateLayout.States.LOADING, arch.cayenne.lib.common.R.string.loading.getString())
                 firstLoading = false
             }
             else ->{

             }
         }
    }

    override fun onDestroy() {
        mViewModel.deleteAll()
        super.onDestroy()
    }
}