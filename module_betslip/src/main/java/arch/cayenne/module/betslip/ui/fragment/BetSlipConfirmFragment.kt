package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipConfirmBinding
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData
import kotlin.reflect.KClass


//注单确认‰‰
class BetSlipConfirmFragment :
    BaseBetSlipFragment<FragmentLiveBetslipConfirmBinding>() {

    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
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
        initLoadRefresh()
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.initLoadMore()
            it.setOnRefreshListener {
                mViewModel.refreshOrder(BetSlipEnum.Confirming)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreOrder(BetSlipEnum.Confirming)
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
    }

    override fun updateState(state: DynamicStateLayout.States) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.refreshLayout.finishLoadMore()
        mBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        if (state == DynamicStateLayout.States.NETWORK_ANOMALY) {
            mBinding.recyclerView.isVisible = false
            mBinding.refreshLayout.isVisible = false
            mBinding.emptyState.isVisible = true
            mBinding.emptyState.setState(state, getString(arch.cayenne.lib.common.R.string.error_net)
            ) {
                mViewModel.refreshOrder(getBetSlipEnum())
            }
        } else {
            mBinding.recyclerView.isVisible = true
            mBinding.refreshLayout.isVisible = true
            mBinding.emptyState.isVisible = false
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        mBinding.emptyState.showEmptyData(isEmpty, mBinding.refreshLayout)
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Confirming
    }
}