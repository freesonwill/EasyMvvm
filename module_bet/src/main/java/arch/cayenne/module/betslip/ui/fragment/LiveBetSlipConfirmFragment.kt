package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.bet.databinding.FragmentLiveBetslipConfirmBinding
import arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum
import arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum
import arch.cayenne.module.betslip.ui.adapter.LiveBetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.LiveBetSlipViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.bet.R
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel


//注单确认‰‰
class LiveBetSlipConfirmFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipConfirmBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class
    private val pageViewModel: BetSlipPageViewModel by sharedViewModel<BetSlipPageViewModel, LiveBetSlipFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler() {
        val adapter =
            LiveBetSlipAdapter(LiveBetSlipEnum.Confirming)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.item_divide_live_bet_recycler
            )!!
        )
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.addItemDecoration(divider)
            it.adapter = adapter
            it.setItemViewCacheSize(10)
            it.setRecycledViewPool(RecyclerView.RecycledViewPool())
        }
        initLoadRefresh()
    }

    private fun initLoadRefresh(){
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum.Confirming)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(arch.cayenne.module.betslip.data.constants.LiveBetSlipEnum.Confirming)
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
            } else {
                showEmpty()
            }
        }
    }

    private fun showEmpty() {

    }

    private fun updateData(orders: List<Common.Order>) {
        val list = orders.map {
            val expandedEnum =
                if (it.selectionsList.size <= 3) LiveBetSlipExpandedEnum.Hide else LiveBetSlipExpandedEnum.Fold
            arch.cayenne.module.betslip.data.model.LiveBetSlipData(
                order = it,
                expandedEnum = expandedEnum
            )
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as LiveBetSlipAdapter
            adapter.submitList(list)
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.setIds(pageViewModel.matchId, sportId = pageViewModel.sportId)
        mViewModel.getOrders(LiveBetSlipEnum.UnSettled)
    }
}