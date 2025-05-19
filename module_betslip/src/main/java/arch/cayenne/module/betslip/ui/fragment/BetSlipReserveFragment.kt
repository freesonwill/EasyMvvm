package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipReserveBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.dialog.BetSlipModifyOddsFragment
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
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

        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.item_divide_live_bet_recycler
            )!!
        )
        adapter.setReserveListener(cancelListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
                item?.reserve?.let { cancelReserve(it) }
            }
        }, modifyListener = object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(item: BetSlipData?, position: Int) {
                item?.reserve?.let { modifyReserve(it) }
            }
        })

        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.addItemDecoration(divider)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
    }


    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.reserveLiveData.observe(this) {
            updateView(it)
        }
        mViewModel.cancelReserveLiveData.observe(this) {
            showToast(if (it == true) "取消预约成功" else "取消预约失败")
            if (it) {
                mViewModel.getReserveOrder()
            }
        }
        mViewModel.modifyOddsLiveData.observe(this) {
            showToast(if (it == true) "修改赔率成功" else "修改赔率失败")
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
        if (flag) {
            mBinding.emptyState.isVisible = true
            mBinding.recyclerView.isVisible = false
            mBinding.emptyState.setState(
                DynamicStateLayout.States.DATA_EMPTY,
                getString(R.string.lineup_empty)
            )
        } else {
            mBinding.emptyState.isVisible = false
            mBinding.recyclerView.isVisible = true
        }
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

    private fun cancelReserve(order: Common.ReserveOrder) {
        CommonDialog.newInstance("", "确定取消预约?", "取消预约", "暂不").also {
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

    override fun getBetSlipEnum(): BetSlipEnum? {
        return null
    }
}