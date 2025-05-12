package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.live.R
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.utils.RecyclerItemListener
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import galaxy.common.proto.Common
import galaxy.common.proto.Common.ReserveOrder
import kotlin.reflect.KClass

//注单预约
class LiveBetSlipReserveFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipReserveBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> =
        FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    private val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.Reserve)

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
        adapter.setReserveListener(cancelListener = object : RecyclerItemListener<LiveBetSlipData> {
            override fun onItemClick(item: LiveBetSlipData?, position: Int) {
                item?.reserve?.let { cancelReserve(it) }
            }
        }, modifyListener = object : RecyclerItemListener<LiveBetSlipData> {
            override fun onItemClick(item: LiveBetSlipData?, position: Int) {
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
        } else {
            showEmpty()
        }

    }

    private fun showEmpty() {

    }

    private fun updateData(orders: List<Common.ReserveOrder>) {
        val list = orders.map { LiveBetSlipData(reserve = it) }.toList()
        val recyclerViewState = mBinding.recyclerView.layoutManager?.onSaveInstanceState()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipAdapter
            adapter.submitList(list) {
                mBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        }
    }


    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getReserveOrder()
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

        LiveBetSlipModifyOddsFragment.newInstance().also {
            it.setConfirmListener { odds ->
                mViewModel.modifyReserve(order, odds)
            }
            it.show(childFragmentManager)
        }
    }
}