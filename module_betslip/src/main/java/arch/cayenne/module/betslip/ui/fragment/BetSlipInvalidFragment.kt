package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipInvalidBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.initLoadMore
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.loadMoreData
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.showEmptyData

//注单失效
class BetSlipInvalidFragment :
    BaseFragment<BetSlipViewModel, FragmentLiveBetslipInvalidBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipInvalidBinding> =
        FragmentLiveBetslipInvalidBinding::class
    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter = BetSlipAdapter(BetSlipEnum.Invalid)
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
                mViewModel.refreshOrder(BetSlipEnum.Invalid)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreOrder(BetSlipEnum.Invalid)
            }
        }
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
        mViewModel.getOrders(BetSlipEnum.Invalid)
    }
}