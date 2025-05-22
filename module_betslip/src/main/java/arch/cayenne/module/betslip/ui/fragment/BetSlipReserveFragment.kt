package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipReserveBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.dialog.BetSlipModifyOddsFragment
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import galaxy.common.proto.Common
import galaxy.common.proto.Common.ReserveOrder
import kotlin.reflect.KClass


//注单预约
class BetSlipReserveFragment :
    BaseBetSlipFragment<FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class
    private val adapter =
        BetSlipAdapter(BetSlipEnum.Reserve)

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        adapter.setReserveListener(cancelListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
                item?.reserve?.let { cancelReserve(it) }
            }
        }, modifyListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
                item?.reserve?.let { modifyReserve(it) }
            }
        })
        adapter.setLiveListener(object : RecyclerItemListener<BetSlipSelectionData> {
            override fun onItemClick(item: BetSlipSelectionData?, position: Int) {

            }
        })

        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.betSlipInit()
        }
    }


    override fun initListener() {
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.reserveLiveData.observe(viewLifecycleOwner) {
            updateView(it)
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

    private fun updateView(list: List<ReserveOrder>?) {
        if (!list.isNullOrEmpty()) {
            updateData(list)
        }
        showEmpty()
    }

    private fun showEmpty() {
        val flag = mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.currentList.isEmpty()
        } ?: true
        mBinding.emptyState.showEmptyData(flag, mBinding.recyclerView)
    }


    private fun updateData(orders: List<Common.ReserveOrder>) {
        val list = orders.map { BetSlipData(reserve = it) }.toList()
        val recyclerViewState = mBinding.recyclerView.layoutManager?.onSaveInstanceState()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.submitList(list) {
                mBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        }
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