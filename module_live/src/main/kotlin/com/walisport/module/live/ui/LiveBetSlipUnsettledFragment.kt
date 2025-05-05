package com.walisport.module.live.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.model.LiveBetSlipData
import com.walisport.module.live.data.constants.LiveBetSlipEnum
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.ui.adapter.LiveBetSlipAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.utils.RecyclerItemListener
import com.walisport.module.live.viewmodel.LiveMainViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass

//注单未结算
class LiveBetSlipUnsettledFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> =
        FragmentLiveBetslipUnsettledBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }
    override fun initData() {
        super.initData()
        mViewModel.setIds(mainViewModel.matchId, sportId = mainViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
    }
    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                updateData(it)
            } else {
                showEmpty()
            }
        }
        mViewModel.earlySettledLiveData.observe(viewLifecycleOwner){
            mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
        }
    }

    private fun showEmpty() {

    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.map { LiveBetSlipData(order = it) }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipAdapter
            adapter.submitList(list)
        }
    }

    private fun initRecycler() {
        val adapter = LiveBetSlipAdapter(LiveBetSlipEnum.UnSettled)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_divide_live_bet_recycler
            )!!
        )
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.addItemDecoration(divider)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
        adapter.setItemListener(object : RecyclerItemListener<LiveBetSlipData> {
            override fun onItemClick(item: LiveBetSlipData?, position: Int) {
                item?.order?.let {
                    LiveEarlySettledKeyboardFragment.instance(
                        it.betId
                    ).apply {
                        setOnEarlySettleListener { money, price ->
                            LogUtils.dTag("aaa", "money $money  price $price")
                            mViewModel.earlyPartSettled(it, money, price)
                        }
                    }.show(childFragmentManager)
                }

            }
        })
    }


}