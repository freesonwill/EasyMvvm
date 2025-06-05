package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipReserve
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipReserveBinding
import arch.cayenne.module.betslip.ui.dialog.BetSlipModifyOddsFragment
import arch.cayenne.module.betslip.ui.viewmodel.ReserveSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import galaxy.common.proto.Common
import kotlin.reflect.KClass

//注单预约
class BetSlipReserveFragment :
    BaseBetSlipFragment<ReserveSlipViewModel, FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<ReserveSlipViewModel> = ReserveSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        betSlipAdapter.setReserveListener(cancelListener = object : RecyclerItemListener<BetSlipReserve> {
            override fun onItemClick(item: BetSlipReserve?, position: Int) {
                item?.reserve?.let { cancelReserve(it) }
            }
        }, modifyListener = object : RecyclerItemListener<BetSlipReserve> {
            override fun onItemClick(item: BetSlipReserve?, position: Int) {
                item?.reserve?.let { modifyReserve(it) }
            }
        })
        betSlipAdapter.setLiveListener(object : RecyclerItemListener<BetSlipSelectionData> {
            override fun onItemClick(item: BetSlipSelectionData?, position: Int) {

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

    override fun createObserver() {
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

    private fun cancelReserve(order: Common.ReserveOrder) {
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

    private fun modifyReserve(order: Common.ReserveOrder) {

        BetSlipModifyOddsFragment.newInstance(order.selection.odds).also {
            it.setConfirmListener { odds ->
                mViewModel.modifyReserve(order, odds)
            }
            it.show(childFragmentManager)
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Reserve
    }
}