package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.helper.showToast
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipUnsettledBinding
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.dialog.BetSlipEarlySettledFragment
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.loadMoreData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import arch.cayenne.module.betslip.utisl.RecyclerItemListener


//注单未结算
class BetSlipUnsettledFragment :
    BaseFragment<BetSlipViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> =
        FragmentLiveBetslipUnsettledBinding::class
    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey, -1) ?: -1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey, -1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        mViewModel.getOrders(BetSlipEnum.UnSettled)
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            if (!it.isNullOrEmpty()) {
                updateData(it)
            }
            showEmpty()
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            showToast(if (it == true) getString(R.string.early_settle_success) else getString(R.string.early_settle_faile))
            mViewModel.getOrders(BetSlipEnum.UnSettled)
        }
        mViewModel.earlySettlePriceLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = BetSlipUtils.earlySettlePrice(
                    order.betAmount, price.toString(), order.earlyBetAmount
                )
                BetSlipEarlySettledFragment.instance(it.betId, money.toDouble()).apply {
                    setOnEarlySettleListener { betId, money ->
                        mViewModel.earlyPartSettled(
                            betId,
                            money.toString(),
                            mViewModel.earlySettlePriceLiveData.value?.price ?: "0"
                        )
                    }
                }.show(childFragmentManager)
            }
        }
    }

    private fun showEmpty() {
        val flag = mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            adapter.currentList.isEmpty()
        } ?: true
        mBinding.emptyState.showEmptyData(flag, mBinding.refreshLayout)
    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.toBetSlipData()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as BetSlipAdapter
            mBinding.refreshLayout.loadMoreData(adapter, list)
        }
    }

    private fun initRecycler() {
        val adapter =
            BetSlipAdapter(BetSlipEnum.UnSettled)
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.betSlipInit()
        }
        adapter.setEarlySettleListener(object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(
                item: BetSlipData?, position: Int
            ) {
                item?.order?.let {
                    mViewModel.selectOrder = it
                    mViewModel.earlySettledPrice(it.betId)
                }
            }
        })
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.initLoadMore()
            it.setOnRefreshListener {
                mViewModel.refreshOrder(BetSlipEnum.UnSettled)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreOrder(BetSlipEnum.UnSettled)
            }
        }
    }

}