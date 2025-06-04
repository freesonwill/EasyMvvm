package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipSettledLayoutBinding
import arch.cayenne.module.betslip.ui.viewmodel.OrderSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass


//注单已结算
class BetSlipSettledFragment :
    BaseBetSlipFragment<OrderSlipViewModel, FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> =
        FragmentLiveBetslipSettledLayoutBinding::class
    override val vmClass: KClass<OrderSlipViewModel> = OrderSlipViewModel::class

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
            betSlipAdapter.submitList(it)
        }
    }



    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Settled
    }
}