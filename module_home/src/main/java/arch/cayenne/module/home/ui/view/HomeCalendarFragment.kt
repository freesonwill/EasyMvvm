package arch.cayenne.module.home.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.HomeTourPopupCalendarViewBinding
import arch.cayenne.module.home.utils.DateUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import galaxy.common.proto.Common
import kotlinx.coroutines.delay
import arch.cayenne.lib.common.R as RC

class HomeCalendarFragment private constructor() : Fragment() {
    enum class AnimState {
        EXPANDING, EXPAND, COLLAPSING, COLLAPSE
    }
    enum class States {
        CALENDAR_CLOSE_NOTHING // 加载中
    }
    private val fragmentTag = this.javaClass.simpleName
    private val defaultAnimDuration = 300L
    private var onDataSelectedListener: ((String) -> Unit)? = null
    private var onResetDateListener: (()-> Unit)? = null
    private var onBeforeDismissAnimListener: (()-> Unit)? = null
    private var onAfterDismissAnimListener: (()-> Unit)? = null
    private var range: List<Common.DailyMatchCount>? = null
    private var marginTop: Int = 0
    private var maskView: View? = null
    private var heightAnimator: ValueAnimator? = null
    private var currentAnimState: AnimState? = null
    private val allDay = "0"
    private var tabSelectedDate: String = allDay
    // 1. 一個私有的、可為 null 的 backing property，用來實際儲存綁定物件。
    private var mBinding: HomeTourPopupCalendarViewBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 初始化 _binding
        mBinding = HomeTourPopupCalendarViewBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        mBinding?.let { binding->
            with(binding.clCalendarPopupRoot) {
                visibility = View.INVISIBLE
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    topMargin = this@HomeCalendarFragment.marginTop
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun initView() {
        mBinding?.let { binding->
            with(binding) {
                clCalendarPopupRoot.apply {
                    bringToFront()
                    setBackgroundResource(
                        R.drawable.shape_home_calendar_background.getSkinnableResourceId()
                    )
                }

                //update weekview color
                calendarView.apply {
                    setSelectSingleMode()
                    setWeeColor(
                        R.color.home_calendar_background.getSkinnableColor(),
                        RC.color.secondary_text.getSkinnableColor()
                    )
                    //update calendarView textColor
                    setTextColor(
                        RC.color.main_text.getSkinnableColor(),
                        RC.color.explanation_text.getSkinnableColor(),
                        RC.color.explanation_text.getSkinnableColor(),
                        RC.color.main_text.getSkinnableColor(),
                        RC.color.main_text.getSkinnableColor(),
                    )
                    setSelectedColor(
                        resources.getColor(R.color.home_calendar_selected_theme_color, null),
                        resources.getColor(RC.color.white, null),
                        resources.getColor(RC.color.white, null)
                    )
                }

                //update current month title text color
                tvCurrentMonth.setTextColor(
                    RC.color.secondary_text.getSkinnableColor()
                )

                //update calendarView button
                calendarBtnCancel.apply {
                    setBackgroundResource(
                        R.drawable.shape_home_calendar_cancel.getSkinnableResourceId()
                    )
                    setTextColor(
                        RC.color.title_bar.getSkinnableColor()
                    )
                }
                calendarBtnOk.setBackgroundResource(
                    R.drawable.shape_home_calendar_ok.getSkinnableResourceId()
                )
                setSchemeDate()
                setCalendarScrollable()
                expandView()
            }
        }

    }
    private fun setCalendarScrollable() {
        mBinding?.let {binding ->
            with (binding) {
                val minRange = calendarView.minRangeCalendar
                val maxRange = calendarView.maxRangeCalendar
                if (minRange.year == maxRange.year && minRange.month == maxRange.month) {
                    ivLeftClick.isEnabled = false
                    ivRightClick.isEnabled = false
                    calendarView.setMonthViewScrollable(false)
                } else {
                    //控制左右按鈕的enabled
                    calendarView.setMonthViewScrollable(true)
                    val isEnabledLeft = compareCurrentYearMonth(minRange.year,minRange.month,maxRange.year,maxRange.month)
                    enabledLeftArrowButton(isEnabledLeft)
                }
            }
        }
    }
    @SuppressLint("DefaultLocale")
    private fun initListener() {
        mBinding?.let {binding->
            with(binding) {
                // 獲取當前日期
                val year = "${calendarView.selectedCalendar.year}"
                val month = String.format("%02d", calendarView.selectedCalendar.month)
                val day = String.format("%02d", calendarView.selectedCalendar.day)
                var selectedDate =
                    if (tabSelectedDate == allDay) allDay
                    else tabSelectedDate
                calendarView.setOnMonthChangeListener { year, month ->
                    tvCurrentMonth.text =
                        resources.getString(
                            R.string.format_month_year,
                            month.toChineseMonth(),
                            year.toString()
                        )
                    //控制左右按鈕的enabled
                    val isEnabledLeft = compareCurrentYearMonth(year,month,calendarView.curYear,calendarView.curMonth)
                    enabledLeftArrowButton(isEnabledLeft)
                }
                // 透過 binding 操作 Popup 內部的 View
                ivRightClick.clickNoRepeat {
                    calendarView.scrollToNext(true)
                }
                ivLeftClick.clickNoRepeat {
                    calendarView.scrollToPre(true)
                }

                calendarBtnCancel.clickNoRepeat {
                    calendarView.scrollToCurrent()
                    val minRangeDate = calendarView.minRangeCalendar
                    calendarView.scrollToCalendar(
                        minRangeDate.year,
                        minRangeDate.month,
                        minRangeDate.day
                    )
                    binding.calendarView.clearSingleSelect()
                    onResetDateListener?.invoke()
                    collapseView()
                }
                calendarBtnOk.clickNoRepeat {
                    setSelectedDateTab(selectedDate)
                }

                setCurrentDate()
                calendarView.setOnCalendarSelectListener(object :
                    CalendarView.OnCalendarSelectListener {
                    override fun onCalendarOutOfRange(calendar: Calendar?) = Unit
                    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                        if (calendar == null) return
                        selectedDate = "$calendar"
                        tvCurrentMonth.text =
                            resources.getString(
                                R.string.format_month_year,
                                calendar.month.toChineseMonth(),
                                calendar.year.toString()
                            )

                        //控制左右按鈕的enabled
                        val isEnabledLeft = compareCurrentYearMonth(calendar.year,calendar.month,calendarView.curYear,calendarView.curMonth)
                        enabledLeftArrowButton(isEnabledLeft)
                    }
                })

                maskView?.setOnClickListener {
                    when(currentAnimState) {
                        AnimState.EXPANDING,
                        AnimState.EXPAND -> collapseView()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun HomeTourPopupCalendarViewBinding.enabledLeftArrowButton(
        isEnabledLeft: Boolean
    ) {
        if (isEnabledLeft) {
            ivRightClick.isEnabled = false
            ivLeftClick.isEnabled = true
        } else {
            ivRightClick.isEnabled = true
            ivLeftClick.isEnabled = false
        }
    }

    private fun Int.getSkinnableColor(): Int{
        return SkinnableResourceManager.getColor(requireContext(), this)
    }

    private fun Int.getSkinnableResourceId(): Int {
        return SkinnableResourceManager.getTargetResourceId(requireContext(), this)
    }

    private fun getFullyHeight(): Int {
        return mBinding?.clCalendarPopupRoot?.let { view ->
            // 確保 view 的寬度不是 0，否則測量無意義
            if (view.width == 0) return@let 0

            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthMeasureSpec, heightMeasureSpec)

            // let 區塊的最後一行是其回傳值
            view.measuredHeight
        } ?: 0
    }

    private fun expandView() {
       mBinding?.let { binding->
           with(binding.clCalendarPopupRoot) {
               doOnLayout {
                   val currentHeight = (heightAnimator?.animatedValue as? Int) ?: height
                   val fullyHeight = getFullyHeight()
                   val startHeight =
                       if(fullyHeight == currentHeight) 1 else currentHeight
                   heightAnimator?.cancel()
                   heightAnimator = ValueAnimator.ofInt(startHeight, fullyHeight).apply {
                       addUpdateListener {
                           (it.animatedValue as Int).let { offset ->
                               clipBounds = Rect(0, fullyHeight - offset, width, fullyHeight)
                               translationY = (offset - fullyHeight).toFloat()
                           }
                       }
                       duration = defaultAnimDuration
                       interpolator = DecelerateInterpolator()
                       doOnStart {
                           currentAnimState = AnimState.EXPANDING
                           clipBounds = Rect(0, fullyHeight - startHeight, width, fullyHeight)
                           translationY = (startHeight - fullyHeight).toFloat()
                           visibility = View.VISIBLE
                           setMaskViewAlpha(true)
                       }
                       doOnEnd { currentAnimState = AnimState.EXPAND }
                       start()
                   }
               }
           }

       }
    }

    private fun collapseView() {
        this.mBinding?.let { binding ->
            with(binding.clCalendarPopupRoot) {
                val currentHeight = (heightAnimator?.animatedValue as? Int) ?: height
                heightAnimator?.cancel()

                heightAnimator = ValueAnimator.ofInt(currentHeight, 1).apply {
                    addUpdateListener {
                        (it.animatedValue as Int).let { offset ->
                            clipBounds = Rect(0, getFullyHeight() - offset, width, getFullyHeight())
                            translationY = (offset - getFullyHeight()).toFloat()
                        }
                    }
                    duration = defaultAnimDuration
                    interpolator = DecelerateInterpolator()
                    doOnStart {
                        currentAnimState = AnimState.COLLAPSING
                        setMaskViewAlpha(false)
                        mBinding?.clCalendarPopupRoot?.postDelayed({
                            onBeforeDismissAnimListener?.invoke()
                        },50L)
                    }
                    doOnEnd {
                        currentAnimState = AnimState.COLLAPSE
                        visibility = View.INVISIBLE
                        mBinding?.clCalendarPopupRoot?.postDelayed({
                            if(currentAnimState == AnimState.COLLAPSE) {
                                dismiss()
                                onAfterDismissAnimListener?.invoke()
                            }
                        }, 100L)
                    }
                    start()
                }
            }

        }
    }

    private fun updateHeight(height: Int) {
        mBinding?.clCalendarPopupRoot?.apply {
            layoutParams = layoutParams.apply { this.height = height }
            requireView()
        }
    }

    private fun setMaskViewAlpha(visible: Boolean) {
        maskView?.let {
            it.post {
                it.animate()
                    .alpha(if (visible) 1f else 0f)
                    .setDuration(defaultAnimDuration)
                    .setListener(object: Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            if(visible) it.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            if(!visible) it.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator) = Unit
                        override fun onAnimationRepeat(p0: Animator) = Unit
                    })
                    .start()
            }
        }
    }

    fun show(manager: FragmentManager, containerId: Int, tabSelectedDate: String) {
        this.tabSelectedDate = tabSelectedDate
        val lastFragment = manager.findFragmentByTag(fragmentTag)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, this.javaClass.simpleName)
                .commit()
        }
    }

    fun callDismiss() {
        collapseView()
    }

    fun getAnimState(): AnimState? {
        return currentAnimState
    }

    private fun dismiss() {
        if (parentFragment != null) {
            mBinding?.clCalendarPopupRoot?.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun setCurrentDate() {
        val currentYear = mBinding?.calendarView?.curYear ?: 0
        val currentMonth = mBinding?.calendarView?.curMonth ?: 0
        //日期tab為全部時標記為今日
        if (tabSelectedDate == allDay) {
            mBinding?.let { binding->
                binding.calendarView.scrollToCurrent(true)
                binding.tvCurrentMonth.text = resources.getString(
                    R.string.format_month_year,
                    currentMonth.toChineseMonth(),
                    currentYear.toString()
                )
                binding.calendarView.clearSingleSelect()
                tabSelectedDate = allDay
            }
        } else {
            val result = tabSelectedDate.extractDate()
            result?.let {
                val (year, month, day) = it
                mBinding?.let { binding->
                    with(binding) {
                        calendarView.scrollToCalendar(year, month, day)
                        tvCurrentMonth.text = resources.getString(
                            R.string.format_month_year,
                            month.toChineseMonth(),
                            year.toString()
                        )
                    }
                }
            } ?: run {
                mBinding?.let { binding->
                   with(binding) {
                       val curYear = calendarView.curYear
                       val curMonth = calendarView.curMonth
                       calendarView.scrollToCurrent(true)
                       tvCurrentMonth.text = resources.getString(
                           R.string.format_month_year,
                           curMonth.toChineseMonth(),
                           curYear.toString()
                       )
                   }

                }
            }
        }
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(selectedDate: String) {
        if (selectedDate == allDay) {
            //如果選擇的是全部日期，則不需要進行任何操作
            onDataSelectedListener?.invoke(selectedDate)
        } else {
            onDataSelectedListener?.invoke(DateUtils.getMonthDay(selectedDate))
        }
        // 通常選擇完資料後，會自動關閉 popup
        collapseView()
    }

    //設定標記紅色日期及可選取日期範圍
    private fun setSchemeDate() {
        range?.let { list ->
            val map: MutableMap<String, Calendar> = HashMap()
            for (date in list) {
                //API回傳資料，有比賽的日期才需要標記紅字
                val dateArray = date.day.split("-")
                if (date.count > 0) {
                    val schemeCalendar = getSchemeCalendar(
                        dateArray[0].toInt(),
                        dateArray[1].toInt(),
                        dateArray[2].toInt()
                    )
                    map[schemeCalendar.toString()] = schemeCalendar
                }
            }
            //可選取日期區間為未來7天
            val startDateTriple = list.first()
            val startDateArray = startDateTriple.day.split("-")
            val endDateTriple = list.last()
            val endDateArray = endDateTriple.day.split("-")
            //設定可以選取的日期區間，目前設定為31天
            this.mBinding?.let { binding->
                with (binding) {
                    calendarView.setRange(
                        startDateArray[0].toInt(),
                        startDateArray[1].toInt(),
                        startDateArray[2].toInt(),
                        endDateArray[0].toInt(),
                        endDateArray[1].toInt(),
                        endDateArray[2].toInt()
                    )
                    calendarView.setSchemeDate(map)
                    calendarView.scrollToCalendar(
                        startDateArray[0].toInt(),
                        startDateArray[1].toInt(),
                        startDateArray[2].toInt()
                    )
                }
            }
        }
    }

    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.drawIndex = 0
        return calendar
    }

    fun updateRange(range: List<Common.DailyMatchCount>) {
        this.range = range
        setSchemeDate()
    }
    fun compareCurrentYearMonth(curYear: Int, curMonth: Int, year2: Int, month2: Int) : Boolean {
        val result = when {
            curYear > year2 -> true
            curYear < year2 -> false
            else -> { // 年份相同，比較月份
                when {
                    curMonth > month2 -> true
                    curMonth < month2 -> false
                    else -> false
                }
            }
        }
        return result
    }

    /**
     * Builder 類別用於構建 HomeCalendarPopupWindow
     */
    class Builder {
        private var onDateSelectedListener: ((String) -> Unit)? = null
        private var onResetDateListener: (()-> Unit)? = null
        private var onAfterDismissAnimListener: (()-> Unit)? = null
        private var onBeforeDismissAnimListener: (() -> Unit)? = null
        private var range: List<Common.DailyMatchCount>? = null
        private var marginTop: Int = 0
        private var maskView: View? = null

        /**
         * 提供一個公開的方法讓外部設定監聽器
         */
        fun setOnDateSelectedListener(listener: (String) -> Unit) = apply {
            this.onDateSelectedListener = listener
        }
        fun setOnResetDateListener(listener: () -> Unit) = apply {
            this.onResetDateListener = listener
        }
        fun setOnAfterDismissAnimListener(listener: () -> Unit) = apply {
            this.onAfterDismissAnimListener = listener
        }
        fun setOnBeforeDismissAnimListener(listener: () -> Unit) = apply {
            this.onBeforeDismissAnimListener = listener
        }
        fun setRange(range: List<Common.DailyMatchCount>) = apply {
            this.range = range
        }
        fun setMarginTop(value: Int) {
            this.marginTop = value
        }
        fun setMaskView(maskView: View) = apply {
            this.maskView = maskView
        }
        fun build(): HomeCalendarFragment {
            return HomeCalendarFragment().apply {
                this.onResetDateListener = this@Builder.onResetDateListener
                this.onDataSelectedListener = this@Builder.onDateSelectedListener
                this.onBeforeDismissAnimListener = this@Builder.onBeforeDismissAnimListener
                this.onAfterDismissAnimListener = this@Builder.onAfterDismissAnimListener
                this.range = this@Builder.range
                this.marginTop = this@Builder.marginTop
                this.maskView = this@Builder.maskView
            }
        }
    }
}