package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import galaxy.common.proto.Common
import kotlin.reflect.KClass
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentLiveBetslipInvalidBinding
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipPageViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel

//注单失效
class BetSlipInvalidFragment :
    BaseFragment<BetSlipViewModel, FragmentLiveBetslipInvalidBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipInvalidBinding> =
        FragmentLiveBetslipInvalidBinding::class
    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class
    private val pageViewModel: BetSlipPageViewModel by sharedViewModel<BetSlipPageViewModel, BetSlipFragment>()

    override fun initView(savedInstanceState: Bundle?) {

        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        val adapter =
            arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter(arch.cayenne.module.betslip.data.constants.BetSlipEnum.Invalid)
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
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.setOnRefreshListener {
            mViewModel.refreshOrder(arch.cayenne.module.betslip.data.constants.BetSlipEnum.Invalid)
        }
        mBinding.refreshLayout.setOnLoadMoreListener {
            mViewModel.loadMoreOrder(arch.cayenne.module.betslip.data.constants.BetSlipEnum.Invalid)
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
                if (it.selectionsList.size <= 3) BetSlipExpandedEnum.Hide else BetSlipExpandedEnum.Fold
            arch.cayenne.module.betslip.data.model.BetSlipData(
                order = it,
                expandedEnum = expandedEnum
            )
        }.toList()
        mBinding.recyclerView.adapter?.let {
            val adapter = it as arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
            adapter.submitList(list)
        }
    }


    override fun initData() {
        super.initData()
//        TODO
        mViewModel.setIds(pageViewModel.matchId, sportId = pageViewModel.sportId)
        mViewModel.getOrders(arch.cayenne.module.betslip.data.constants.BetSlipEnum.Invalid)
    }
}