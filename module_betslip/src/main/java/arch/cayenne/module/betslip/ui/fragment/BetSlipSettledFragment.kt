package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipSettledLayoutBinding
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
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
            mBinding.refreshLayout.finishRefresh()
            mBinding.refreshLayout.finishLoadMore()
            betSlipAdapter.submitList(it) {
                mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
            }
            showEmpty(it.isEmpty())
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) {
            mViewModel.getOrders(BetSlipEnum.UnSettled)
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        mBinding.emptyState.showEmptyData(isEmpty, mBinding.refreshLayout)
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Settled
    }
}