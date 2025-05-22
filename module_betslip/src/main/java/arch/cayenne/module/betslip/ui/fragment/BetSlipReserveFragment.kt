package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipReserveBinding
import arch.cayenne.module.betslip.ui.dialog.BetSlipModifyOddsFragment
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import galaxy.common.proto.Common
import kotlin.reflect.KClass


//注单预约
class BetSlipReserveFragment :
    BaseBetSlipFragment<FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        betSlipAdapter.setReserveListener(cancelListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
                item?.reserve?.let { cancelReserve(it) }
            }
        }, modifyListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
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


    override fun initListener() {
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.reserveLiveData.observe(viewLifecycleOwner) {
            val recyclerViewState = mBinding.recyclerView.layoutManager?.onSaveInstanceState()
            betSlipAdapter.submitList(it) {
                mBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
            showEmpty(it.isEmpty())
        }
        mViewModel.cancelReserveLiveData.observe(viewLifecycleOwner) {
            showToast(if (it == true) getString(R.string.cancel_reserve_success) else getString(R.string.cancel_reserve_fail))
            if (it) {
                mViewModel.getReserveOrder()
            }
        }
        mViewModel.modifyOddsLiveData.observe(viewLifecycleOwner) {
            showToast(if (it == true) getString(R.string.modify_odds_success) else getString(R.string.modify_odds_fail))
            if (it) {
                mViewModel.getReserveOrder()
            }
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        mBinding.emptyState.showEmptyData(isEmpty, mBinding.recyclerView)
    }

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey, -1) ?: -1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey, -1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        mViewModel.getReserveOrder()
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

        BetSlipModifyOddsFragment.newInstance().also {
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