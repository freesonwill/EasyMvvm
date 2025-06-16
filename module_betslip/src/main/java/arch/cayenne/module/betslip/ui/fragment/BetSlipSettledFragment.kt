package arch.cayenne.module.betslip.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipSelectionData
import arch.cayenne.module.betslip.data.model.OrderSelectionBean
import arch.cayenne.module.betslip.databinding.FragmentLiveBetslipSettledLayoutBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.viewmodel.OrderSlipViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewExt.betSlipInit
import kotlin.reflect.KClass


//注单已结算
class BetSlipSettledFragment :
    BaseBetSlipFragment<OrderSlipViewModel, FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> =
        FragmentLiveBetslipSettledLayoutBinding::class
    override val vmClass: KClass<OrderSlipViewModel> = OrderSlipViewModel::class
    override val betSlipAdapter: BetSlipAdapter by lazy {
        BetSlipAdapter(getBetSlipEnum())
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
        initLoadRefresh()
    }

    private fun initRecycler() {
        betSlipAdapter.setLiveListener(object : BetSlipAdapter.BetSlipLiveListener {
            override fun isShowLiveButton(): Boolean {
                return settingViewModel.isBetSlipDetail
            }
            override fun onLiveButtonClick(data: BetSlipSelectionData) {
                if (data is OrderSelectionBean) {
                    val matchId = data.matchBasic.matchId
                    val sportId = data.matchBasic.sportId
                    navigate(Uri.parse("walisport://module_live/liveFragment?matchId=${matchId}&sportId=${sportId}"))
                }
            }
        })
        betSlipAdapter.setBetSlipListener(object : BetSlipAdapter.BetSlipListener {
            override fun getMoneySymbol(): String {
                return settingViewModel.moneySymbol
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
                mViewModel.refreshData(BetSlipEnum.Settled)
            }
            it.setOnLoadMoreListener {
                mViewModel.loadMoreData(BetSlipEnum.Settled)
            }
        }
    }

    override fun initListener() {
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.orderLiveData.observe(viewLifecycleOwner) {
            betSlipAdapter.submitList(it) {
                registerListener()
            }
        }
    }

    private fun registerListener() {
        parentFragmentManager.clearFragmentResultListener("BetSlip")
        parentFragmentManager.setFragmentResultListener("BetSlip", viewLifecycleOwner) { _, bundle ->
            val isUpdate = bundle.getBoolean("EarlySettled", false)
            if (isUpdate) {
                mViewModel.refreshData(getBetSlipEnum())
            }
        }
    }

    override fun getBetSlipEnum(): BetSlipEnum {
        return BetSlipEnum.Settled
    }
}