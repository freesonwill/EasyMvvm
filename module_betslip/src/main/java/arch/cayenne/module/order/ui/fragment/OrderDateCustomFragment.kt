package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateCustomBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateCustomViewModel
import kotlin.reflect.KClass

class OrderDateCustomFragment: BaseFragment<OrderDateCustomViewModel, FragmentOrderDateCustomBinding>(), OrderDataPage {

    override val vbClass: KClass<FragmentOrderDateCustomBinding> = FragmentOrderDateCustomBinding::class
    override val vmClass: KClass<OrderDateCustomViewModel> = OrderDateCustomViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.tvYesterday.setOnClickListener {
            setSelected(it)
        }
        mBinding.tvLastWeek.setOnClickListener {
            setSelected(it)
        }
        mBinding.tvLastMonth.setOnClickListener {
            setSelected(it)
        }
        mBinding.tvStartTime.setOnClickListener {
            setSelected(it)
        }
        mBinding.tvEndTime.setOnClickListener {
            setSelected(it)
        }
    }

    override suspend fun createObserver() {
        mViewModel.onStartTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvStartTime.text = date
        }
        
        mViewModel.onEndTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvEndTime.text = date
        }
    }

    override fun getResult(): LongArray {
        return if (mBinding.tvYesterday.isSelected) {
            mViewModel.getYesterdayTimeRange()
        } else if (mBinding.tvLastWeek.isSelected) {
            mViewModel.getLastWeekTimeRange()
        } else if (mBinding.tvLastMonth.isSelected) {
            mViewModel.getLastMonthTimeRange()
        } else {
            mViewModel.getCustomTimeRange()
        }
    }

    private fun setSelected(v: View) {
        mBinding.tvYesterday.isSelected = v == mBinding.tvYesterday
        mBinding.tvLastWeek.isSelected = v == mBinding.tvLastWeek
        mBinding.tvLastMonth.isSelected = v == mBinding.tvLastMonth
        mBinding.tvStartTime.isSelected = v == mBinding.tvStartTime
        mBinding.tvEndTime.isSelected = v == mBinding.tvEndTime
    }
}