package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipSettledLayoutBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.loadMoreData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData


//注单已结算
class BetSlipSettledFragment :
    BaseFragment<BetSlipViewModel, FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> =
        FragmentLiveBetslipSettledLayoutBinding::class
    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter = BetSlipAdapter(BetSlipEnum.Settled)

        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
            it.betSlipInit()
        }
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.initLoadMore()
            it.setOnRefreshListener {
                mViewModel.refreshOrder(BetSlipEnum.Settled)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreOrder(BetSlipEnum.Settled)
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                updateData(it)
            }
            mBinding.refreshLayout.finishRefresh(300)
            mBinding.refreshLayout.finishLoadMore(300)
            showEmpty()
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            mViewModel.getOrders(BetSlipEnum.UnSettled)
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
           mBinding.refreshLayout.loadMoreData(adapter,list)
        }
    }

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey, -1) ?: -1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey, -1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        mViewModel.getOrders(BetSlipEnum.Settled)
    }
}