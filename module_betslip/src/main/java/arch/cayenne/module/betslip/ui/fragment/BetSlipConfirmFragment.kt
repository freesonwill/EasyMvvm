package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipConfirmBinding
import arch.cayenne.module.betslip.ui.viewmodel.OrderSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass


//注单确认‰‰
class BetSlipConfirmFragment :
    BaseBetSlipFragment<OrderSlipViewModel, FragmentLiveBetslipConfirmBinding>() {

    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> =
        FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<OrderSlipViewModel> = OrderSlipViewModel::class

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
            it.setOnRefreshListener {
                mViewModel.refreshData(BetSlipEnum.Confirming)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreData(BetSlipEnum.Confirming)
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