package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.module.betslip.R
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
            betSlipAdapter.submitList(it)
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Confirming
    }
}