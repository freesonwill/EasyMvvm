package arch.cayenne.module.betslip.ui.fragment

import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import kotlin.reflect.KClass

abstract class BaseBetSlipFragment<VB : ViewBinding>: BaseFragment<BetSlipViewModel, VB>(), BetSlipUpdateListener {

    override val vmClass: KClass<BetSlipViewModel> = BetSlipViewModel::class

    override fun initData() {
        super.initData()
        val matchId = arguments?.getLong(BetSlipFragment.matchKey,-1) ?: -1
        val sportId = arguments?.getInt(BetSlipFragment.sportKey,-1) ?: -1
        mViewModel.setIds(matchId, sportId = sportId)
        loadData()
    }

    abstract fun getBetSlipEnum(): BetSlipEnum?

    override fun updateByTime(startTime: Long?, endTime: Long?) {
        mViewModel.setTime(startTime, endTime)
        loadData()
    }

    override fun updateBySport(sportId: Int) {
        mViewModel.setIds(-1, sportId)
        loadData()
    }

    private fun loadData() {
        getBetSlipEnum()?.let {
            mViewModel.getOrders(it)
        } ?: mViewModel.getReserveOrder()
    }

    override fun onStart() {
        super.onStart()
        parentFragmentManager.setFragmentResultListener(Config.KEY_UPDATE, viewLifecycleOwner) { _, bundle ->
            val startTime = bundle.getLong(Config.VALUE_START_TIME, -1)
            val endTime = bundle.getLong(Config.VALUE_END_TIME, -1)
            updateByTime(if (startTime == -1L) null else startTime, if (endTime == -1L) null else endTime)
        }
    }
}

interface BetSlipUpdateListener {
    fun updateByTime(startTime: Long?, endTime: Long?)
    fun updateBySport(sportId: Int)
}