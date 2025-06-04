package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipUnsettledBinding
import arch.cayenne.module.betslip.ui.dialog.BetSlipEarlySettledFragment
import arch.cayenne.module.betslip.ui.viewmodel.UnsettledViewModel
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass


//注单未结算
class BetSlipUnsettledFragment :
    BaseBetSlipFragment<UnsettledViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> =
        FragmentLiveBetslipUnsettledBinding::class
    override val vmClass: KClass<UnsettledViewModel> = UnsettledViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    override fun initListener() {
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            betSlipAdapter.submitList(it)
        }
        mViewModel.earlySettledResultLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled(viewLifecycleOwner)?.let {
                showToast(if (it) getString(R.string.early_settle_success) else getString(R.string.early_settle_faile))
                if (it) {
                    parentFragmentManager.setFragmentResult("BetSlip", Bundle().apply {
                        putBoolean("EarlySettled", true)
                    })
                }
            }
        }
        mViewModel.isSupportEarlySettleLiveData.observe(viewLifecycleOwner) {
            val price = it.price.toDoubleOrNull()
            if (price == null || price <= 0) {
                showToast(getString(R.string.not_support_early_settle))
                return@observe
            }
            mViewModel.selectOrder?.let { order ->
                val money = BetSlipUtils.earlySettlePrice(
                    order.betAmount, order.earlyBetAmount
                )
                BetSlipEarlySettledFragment.instance(money).apply {
                    setOnEarlySettleListener { money ->
                        mViewModel.earlyPartSettled(
                            it.betId,
                            money,
                            it.price
                        )
                    }
                }.show(childFragmentManager)
            }
        }
    }

    private fun initRecycler() {
        mBinding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = betSlipAdapter
            it.betSlipInit()
        }
        betSlipAdapter.setEarlySettleListener(object : RecyclerItemListener<BetSlipData> {
            override fun onItemClick(
                item: BetSlipData?, position: Int
            ) {
                item?.order?.let {
                    mViewModel.isSupportEarlySettled(it)
                }
            }
        })
        betSlipAdapter.setLiveListener(object : RecyclerItemListener<BetSlipSelectionData> {
            override fun onItemClick(item: BetSlipSelectionData?, position: Int) {

            }
        })
    }

    private fun initLoadRefresh() {
        mBinding.refreshLayout.also {
            it.setOnRefreshListener {
                mViewModel.refreshData(BetSlipEnum.UnSettled)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreData(BetSlipEnum.UnSettled)
            }
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.UnSettled
    }
}