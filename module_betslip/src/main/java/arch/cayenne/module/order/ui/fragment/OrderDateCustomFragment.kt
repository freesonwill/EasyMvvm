package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.databinding.FragmentOrderDateCustomBinding
import arch.cayenne.module.order.ui.viewmodel.OrderDateCustomViewModel
import kotlin.reflect.KClass
import androidx.core.view.isVisible
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.betslip.R
import java.util.Calendar
import java.util.Locale

class OrderDateCustomFragment: BaseFragment<OrderDateCustomViewModel, FragmentOrderDateCustomBinding>(), OrderDataPage {

    override val vbClass: KClass<FragmentOrderDateCustomBinding> = FragmentOrderDateCustomBinding::class
    override val vmClass: KClass<OrderDateCustomViewModel> = OrderDateCustomViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.layoutWheel.yearPicker.setOnValueChangedListener { _, _, newVal ->
            val month = mBinding.layoutWheel.monthPicker.value
            val day = mBinding.layoutWheel.dayPicker.value
            updateDayPicker(newVal, month, day)
            // 更新選中的日期
            updateSelectedDate(newVal, month, day)
        }
        mBinding.layoutWheel.monthPicker.setOnValueChangedListener { _, _, newVal ->
            val year = mBinding.layoutWheel.yearPicker.value
            val day = mBinding.layoutWheel.dayPicker.value
            updateDayPicker(year, newVal, day)
            // 更新選中的日期
            updateSelectedDate(year, newVal, day)
        }
        mBinding.layoutWheel.dayPicker.setOnValueChangedListener { _, _, newVal ->
            val year = mBinding.layoutWheel.yearPicker.value
            val month = mBinding.layoutWheel.monthPicker.value

            val calendar = mViewModel.calendar
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, newVal)
            
            // 更新選中的日期
            updateSelectedDate(year, month, newVal)
        }
    }

    override fun initListener() {
        mBinding.tvYesterday.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvLastWeek.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvLastMonth.setOnClickListener {
            setSelected(it)
            hideWheelView()
        }
        mBinding.tvStartTime.setOnClickListener {
            mViewModel.setStartTime(mBinding.tvStartTime.text.toString())
            setSelected(it)
            showWheelView()
        }
        mBinding.tvEndTime.setOnClickListener {
            mViewModel.setEndTime(mBinding.tvEndTime.text.toString())
            setSelected(it)
            showWheelView()
        }
    }
    
    private fun showWheelView() {
        if (mBinding.layoutWheel.root.isVisible) return
        
        // 先測量視圖的目標高度
        mBinding.layoutWheel.root.measure(
            View.MeasureSpec.makeMeasureSpec(mBinding.root.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = mBinding.layoutWheel.root.measuredHeight
        
        // 設置初始高度為 0
        mBinding.layoutWheel.root.layoutParams.height = 31.dp2px
        mBinding.layoutWheel.root.visibility = View.VISIBLE
        
        // 執行高度展開動畫
        val animator = android.animation.ValueAnimator.ofInt(31.dp2px, targetHeight)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = mBinding.layoutWheel.root.layoutParams
            layoutParams.height = value
            mBinding.layoutWheel.root.layoutParams = layoutParams

            // 通知父 Dialog 更新高度
            findUpdateCustomViewInterface(parentFragment)
        }
        animator.duration = 300
        animator.start()
    }

    private fun findUpdateCustomViewInterface(parentFragment: Fragment?) {
        if (parentFragment !is UpdateCustomViewInterface) {
            findUpdateCustomViewInterface(parentFragment?.parentFragment)
        } else {
            parentFragment.updateViewPagerHeight()
        }
    }
    
    private fun hideWheelView() {
        if (mBinding.layoutWheel.root.isGone) return
        
        val currentHeight = mBinding.layoutWheel.root.height
        
        // 執行高度收起動畫
        val animator = android.animation.ValueAnimator.ofInt(currentHeight, 31.dp2px)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = mBinding.layoutWheel.root.layoutParams
            layoutParams.height = value
            mBinding.layoutWheel.root.layoutParams = layoutParams

            // 通知父 Dialog 更新高度
            findUpdateCustomViewInterface(parentFragment)
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                mBinding.layoutWheel.root.visibility = View.GONE
            }
        })
        animator.duration = 300
        animator.start()
    }

    override suspend fun createObserver() {
        mViewModel.onStartTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvStartTime.text = date
        }
        
        mViewModel.onEndTimeListener.observe(viewLifecycleOwner) { date ->
            mBinding.tvEndTime.text = date
        }
        mViewModel.customTimeListener.observe(viewLifecycleOwner) { _ ->
            val c = mViewModel.calendar
            initYearPicker(c)
            initMonthPicker(c)
            initDayPicker(c)
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

    private fun updateDayPicker(year: Int, month: Int, day: Int) {
        val calendar = mViewModel.calendar
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val newDay = if (day > maxDay) maxDay else day
        mBinding.layoutWheel.dayPicker.displayedValues = null
        calendar.set(Calendar.DAY_OF_MONTH, newDay)
        initDayPicker(calendar)
    }

    private fun initDayPicker(calendar: Calendar) {
        mBinding.layoutWheel.dayPicker.apply {
            wrapSelectorWheel = true

            val curDay = calendar.get(Calendar.DAY_OF_MONTH)
            val minDay = 1
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val day = (minDay..maxDay).map { getString(R.string.date_picker_day).format(it) }
                .toTypedArray()
            minValue = minDay
            maxValue = maxDay
            displayedValues = day
            value = curDay
        }
    }

    private fun initMonthPicker(calendar: Calendar) {
        mBinding.layoutWheel.monthPicker.apply {
            wrapSelectorWheel = true

            val curMonth = calendar.get(Calendar.MONTH) + 1
            val minMonth = 1
            val maxMonth = 12
            val month =
                (minMonth..maxMonth).map { getString(R.string.date_picker_month).format(it) }
                    .toTypedArray()
            minValue = minMonth
            maxValue = maxMonth
            displayedValues = month
            value = curMonth
        }
    }

    private fun initYearPicker(calendar: Calendar) {
        mBinding.layoutWheel.yearPicker.apply {
            wrapSelectorWheel = false

            val curYear = calendar.get(Calendar.YEAR)
            val minYear = curYear - 10
            val maxYear = Calendar.getInstance().get(Calendar.YEAR)
            val year = (minYear..maxYear).map { it.toString() }
                .toTypedArray()
            minValue = curYear - 10
            maxValue = maxYear
            displayedValues = year
            value = curYear
        }
    }
    
    /**
     * 更新選中的日期（根據當前選中的是 startTime 還是 endTime）
     */
    private fun updateSelectedDate(year: Int, month: Int, day: Int) {
        val dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day)
        
        when {
            mBinding.tvStartTime.isSelected -> {
                mViewModel.setStartTime(dateStr)
            }
            mBinding.tvEndTime.isSelected -> {
                mViewModel.setEndTime(dateStr)
            }
        }
    }
}

interface UpdateCustomViewInterface {
    fun updateViewPagerHeight()
}