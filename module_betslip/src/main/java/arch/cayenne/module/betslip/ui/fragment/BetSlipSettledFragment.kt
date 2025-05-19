package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipSettledLayoutBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.loadMoreData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import arch.cayenne.module.betslip.utisl.RecyclerItemListener
import galaxy.common.proto.Common
import kotlin.reflect.KClass


//注单已结算
class BetSlipSettledFragment :
    BaseBetSlipFragment<FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> =
        FragmentLiveBetslipSettledLayoutBinding::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter = BetSlipAdapter(BetSlipEnum.Settled)
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
        super.createObserver()
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

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Settled
    }
}