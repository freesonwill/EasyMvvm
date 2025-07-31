package arch.cayenne.module.betslip.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.fragment.ReserveDialogFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.database.entity.ReserveOrderSelectionBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipReserveBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.adapter.BetSlipReserveAdapter
import arch.cayenne.module.betslip.ui.viewmodel.ReserveSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass

//注单预约
class BetSlipReserveFragment :
    BaseBetSlipFragment<ReserveSlipViewModel, FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<ReserveSlipViewModel> = ReserveSlipViewModel::class
    override val betSlipAdapter: BetSlipReserveAdapter by lazy {
        BetSlipReserveAdapter(object : RecyclerItemListener<BetSlipReserveBean> {
            override fun onItemClick(item: BetSlipReserveBean?, position: Int) {
                if (mViewModel.checkNetwork()) {
                    item?.let { cancelReserve(it) }
                }
            }
        }, object : BetSlipReserveAdapter.BetSlipReserveListener {
            override fun onModifyReserveClick(
                locationX: Int,
                locationY: Int,
                viewHeight: Int,
                position: Int
            ) {
                if (mViewModel.checkNetwork()) {
                    val bean = betSlipAdapter.currentList[position] as BetSlipReserveBean
                    childFragmentManager.setFragmentResultListener(
                        ReserveDialogFragment.KEY_RESULT,
                        viewLifecycleOwner
                    ) { _, bundle ->
                        childFragmentManager.clearFragmentResultListener(ReserveDialogFragment.KEY_RESULT)
                        if (bundle.getString(ReserveDialogFragment.KEY_RESULT) == ReserveDialogFragment.VALUE_RESERVE_COMPLETE) {
                            val odds = bundle.getInt(ReserveDialogFragment.KEY_ODDS_RESULT)
                            mViewModel.modifyReserve(bean, odds.getOdds())
                        }
                    }
                    ReserveDialogFragment.newInstance(
                        locationX,
                        locationY,
                        viewHeight,
                        odds = bean.selection.odds.toOdds()
                    ).show(childFragmentManager)
                }
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        betSlipAdapter.setLiveListener(object : BetSlipAdapter.BetSlipLiveListener {
            override fun isShowLiveButton(): Boolean {
                return settingViewModel.isBetSlipDetail
            }

            override fun onLiveButtonClick(data: BetSlipSelectionData) {
                if (data is ReserveOrderSelectionBean) {
                    val matchId = data.matchBasic.matchId
                    val sportId = data.matchBasic.sportId
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${matchId}&sportId=${sportId}"))
                }
            }
        })

        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = betSlipAdapter
            it.betSlipInit()
        }
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.setOnRefreshListener {
                mViewModel.refreshData(getBetSlipEnum())
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreData(getBetSlipEnum())
            }
        }
    }


    override fun initListener() {
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.reserveLiveData.observe(viewLifecycleOwner) {
            val recyclerViewState = mBinding.recyclerView.layoutManager?.onSaveInstanceState()
            betSlipAdapter.submitList(it) {
                mBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        }
        mViewModel.cancelReserveLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                showToast(if (it) getString(R.string.cancel_reserve_success) else getString(R.string.cancel_reserve_fail))
            }
        }
        mViewModel.modifyOddsLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                showToast(if (it) getString(R.string.modify_odds_success) else getString(R.string.modify_odds_fail))
            }
        }
    }

    private fun cancelReserve(order: BetSlipReserveBean) {
        CommonDialog.newInstance(
            "",
            getString(R.string.confirm_cancel_reserve),
            getString(R.string.cancel_reserve),
            getString(R.string.not_yet)
        ).also {
            it.setOnOkClickListener {
                mViewModel.cancelReserve(order)
            }
            it.show(childFragmentManager)
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Reserve
    }
}