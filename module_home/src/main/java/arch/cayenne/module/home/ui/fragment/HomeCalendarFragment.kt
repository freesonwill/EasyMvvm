package arch.cayenne.module.home.ui.fragment

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.extractDate
import arch.cayenne.lib.common.utils.ext.toChineseMonth
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentHomeCalendarBinding
import arch.cayenne.module.home.utils.DateUtils
import com.haibin.calendarview.CalendarView
import galaxy.common.proto.Common
import kotlin.reflect.KClass

/**
 * @author: ricky.chang
 * @date: 2025/7/30 下午2:30
 * @description:
 */
class HomeCalendarFragment : BaseFragment<EmptyViewModel, FragmentHomeCalendarBinding>() {
    override val vbClass: KClass<FragmentHomeCalendarBinding>
        get() = FragmentHomeCalendarBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class
    private val defaultAnimDuration = 300L
    private var tabSelectedDate: String = "0"
    private var marginTop: Int = 0
    private var marginStart: Int = 0
    private var marginEnd: Int = 0
    private var schemeDates: List<Common.DailyMatchCount> = emptyList()
    // 回傳結果的Bundle
    private val resultBundle by lazy { Bundle() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding.clCalendar) {
            visibility = View.INVISIBLE
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = this@HomeCalendarFragment.marginTop
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 從 arguments 中獲取初始資料
        tabSelectedDate = arguments?.getString(ARG_SELECTED_DATE) ?: "0"
        mBinding.maskView.background = createMaskGradient()
        setCalendarView()
        setSchemeDate()
        setMaskViewAlpha(true)
        expandView()
    }
    private fun setCalendarView() {
        with(mBinding) {
            val backgroundColor = SkinnableResourceManager.getColor(
                requireContext(),
                R.color.home_calendar_background
            )
            val textColor = SkinnableResourceManager.getColor(
                requireContext(),
                arch.cayenne.lib.common.R.color.secondary_text
            )
            calendarView.setWeeColor(backgroundColor, textColor)
            //update current month title text color
            tvCalendarTitle.setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.secondary_text
                )
            )
            //update previous and next month button drawable
            ivPrevMonth.setImageResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    R.drawable.ic_calendar_arrow_left
                )
            )
            ivNextMonth.setImageResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    R.drawable.ic_calendar_arrow_right
                )
            )
            //update calendarView textColor
            calendarView.setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_text
                ),
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.explanation_text
                ),
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.explanation_text
                ),
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_text
                ),
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.main_text
                )
            )
            calendarView.setSelectedColor(
                requireContext().resources.getColor(R.color.home_calendar_selected_theme_color, null),
                requireContext().resources.getColor(arch.cayenne.lib.common.R.color.white, null),
                requireContext().resources.getColor(arch.cayenne.lib.common.R.color.white, null)
            )
            //update calendarView button
            tvReset.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    R.drawable.shape_home_calendar_cancel
                )
            )
            tvReset.setTextColor(
                SkinnableResourceManager.getColor(
                    requireContext(),
                    arch.cayenne.lib.common.R.color.title_bar
                )
            )
            tvConfirm.setBackgroundResource(
                SkinnableResourceManager.getTargetResourceId(
                    requireContext(),
                    R.drawable.shape_home_calendar_ok
                )
            )
        }
    }
    override fun initListener() {
        with(mBinding) {
            // 獲取當前日期
            maskView.clickNoRepeat {
                setMaskDismissResult()
                close()
                dismiss()
            }
            var selectedDate = if (tabSelectedDate == "0") {
                "${this.calendarView.selectedCalendar}"
            } else tabSelectedDate
            // 透過 binding 操作 Popup 內部的 View
            this.ivNextMonth.clickNoRepeat {
                this.calendarView.scrollToNext(true)
            }
            this.ivPrevMonth.clickNoRepeat {
                this.calendarView.scrollToPre(true)
            }
            this.tvReset.clickNoRepeat {
                this.calendarView.scrollToCurrent()
                val currDate =
                    "${calendarView.curYear}${calendarView.curMonth}${calendarView.curDay}"
                sendResult(currDate)
                val minRangeDate = calendarView.minRangeCalendar
                calendarView.scrollToCalendar(
                    minRangeDate.year,
                    minRangeDate.month,
                    minRangeDate.day
                )
                close()
            }
            this.tvConfirm.clickNoRepeat {
                setSelectedDateTab(selectedDate)
               close()
            }
            setCurrentDate(tabSelectedDate)
            this.calendarView.setOnCalendarSelectListener(object :
                CalendarView.OnCalendarSelectListener {
                override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar?) {

                }

                override fun onCalendarSelect(
                    calendar: com.haibin.calendarview.Calendar?,
                    isClick: Boolean
                ) {
                    if (calendar == null) return
                    selectedDate = "$calendar"
                    tvCalendarTitle.text =
                        resources.getString(
                            R.string.format_month_year,
                            calendar.month.toChineseMonth(),
                            calendar.year.toString()
                        )
                    //控制左右按鈕的enabled
                    if (calendar.month > calendarView.curMonth) {
                        ivNextMonth.isEnabled = false
                        ivPrevMonth.isEnabled = true
                    } else {
                        ivNextMonth.isEnabled = true
                        ivPrevMonth.isEnabled = false
                    }
                }
            })
        }
    }
    fun setSchemeDateList(dates: List<Common.DailyMatchCount>) {
        schemeDates = dates
    }
    private fun close() {
        setMaskViewAlpha(false)
        collapseView() // 關閉 Popup
    }

    private fun expandView() {
        with(mBinding.clCalendar) {
            post {
                ValueAnimator.ofInt(1, height).apply {
                    addUpdateListener {
                        layoutParams =
                            layoutParams.apply {
                                height = it.animatedValue as Int
                            }
                    }
                    duration = defaultAnimDuration
                    interpolator = DecelerateInterpolator()
                    doOnStart {
                        layoutParams =
                            layoutParams.apply {
                                height = 1
                            }
                        visibility = View.VISIBLE
                        with(mBinding) {
                            calendarView.visibility = View.VISIBLE
                            tvReset.visibility = View.VISIBLE
                            tvConfirm.visibility = View.VISIBLE
                        }

                    }
                    start()
                }
            }
        }
    }
    private fun createMaskGradient(): Drawable {
        val defaultColor = 0x80000000
        val defaultStartAt = 0.3f
        val defaultStopAt = 0.7f
        return object : Drawable() {
            private val paint = Paint()
            private lateinit var shader: LinearGradient

            override fun onBoundsChange(bounds: Rect) {
                super.onBoundsChange(bounds)
                shader = LinearGradient(
                    0f, bounds.bottom.toFloat(),
                    0f, bounds.top.toFloat(),
                    intArrayOf(defaultColor.toInt(), defaultColor.toInt(), Color.TRANSPARENT),
                    floatArrayOf(0f, defaultStartAt, defaultStopAt),
                    Shader.TileMode.CLAMP
                )
                paint.shader = shader
            }

            override fun draw(canvas: Canvas) {
                canvas.drawRect(bounds, paint)
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
            }

            @Deprecated(
                message = "Deprecated in Java",
                replaceWith = ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
            )
            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

            override fun setColorFilter(colorFilter: ColorFilter?) {
                paint.colorFilter = colorFilter
            }
        }
    }
    private fun setMaskViewAlpha(visible: Boolean) {
        with(mBinding.maskView) {
            post {
                animate()
                    .alpha(if (visible) 1f else 0f)
                    .setDuration(defaultAnimDuration)
                    .start()
            }
        }
    }

    private fun setCurrentDate(tabSelectedDate: String) {
        with(mBinding) {
            val currentYear = calendarView.curYear
            val currentMonth = calendarView.curMonth
            //日期tab為全部時標記為今日
            if (tabSelectedDate == "0") {
                calendarView.scrollToCurrent(true)
                tvCalendarTitle.text = resources.getString(
                    R.string.format_month_year,
                    currentMonth.toChineseMonth(),
                    currentYear.toString()
                )
            } else {
                val result = tabSelectedDate.extractDate()
                result?.let {
                    val (year, month, day) = it
                    calendarView.scrollToCalendar(year, month, day)
                    tvCalendarTitle.text = resources.getString(
                        R.string.format_month_year,
                        month.toChineseMonth(),
                        year.toString()
                    )
                } ?: run {
                    val curYear = calendarView.curYear
                    val curMonth = calendarView.curMonth
                    calendarView.scrollToCurrent(true)
                    tvCalendarTitle.text = resources.getString(
                        R.string.format_month_year,
                        curMonth.toChineseMonth(),
                        curYear.toString()
                    )
                }
            }
        }
    }

    //選取日期後按確定時連動至早盤日期tab,選取對應的日期
    private fun setSelectedDateTab(selectedDate: String) {
        sendResult(selectedDate)
        // 通常選擇完資料後，會自動關閉 popup
        dismiss()
    }

    private fun dismiss() {
        if (parentFragment != null) {
            parentFragmentManager.setFragmentResult(RESULT_KEY_DATE, resultBundle)
            mBinding.root.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
        setMaskViewAlpha(false)
    }

    private fun sendResult(selectedDate: String) {
        resultBundle.putString(RESULT_KEY_DATE, DateUtils.getMonthDay(selectedDate))
    }
    private fun setMaskDismissResult() {
        resultBundle.putBoolean(RESULT_MASK_DISMISS, true)
    }
    private fun collapseView() {
        with(mBinding.clCalendar) {
            ValueAnimator.ofInt(height, 1).apply {
                addUpdateListener {
                    layoutParams =
                        layoutParams.apply {
                            height = it.animatedValue as Int
                        }
                }
                duration = defaultAnimDuration
                interpolator = DecelerateInterpolator()
                doOnEnd {
                    visibility = View.INVISIBLE
                    mBinding.root.postDelayed({
                        dismiss()
                    }, 100L)
                }
                start()
            }
        }
    }

    fun show(manager: FragmentManager, containerId: Int) {
        val lastFragment = manager.findFragmentByTag(TAG)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, this.javaClass.simpleName)
                .commit()
        }
    }

    override fun createObserver() {

    }
    //設定標記紅色日期及可選取日期範圍
    private fun setSchemeDate() {
        val map: MutableMap<String, com.haibin.calendarview.Calendar> = HashMap()
        for (date in schemeDates) {
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
        val startDateTriple = schemeDates.first()
        val startDateArray = startDateTriple.day.split("-")
        val endDateTriple = schemeDates.last()
        val endDateArray = endDateTriple.day.split("-")
        //設定可以選取的日期區間，目前設定為31天
        with(mBinding) {
            calendarView.setRange(
                startDateArray[0].toInt(),
                startDateArray[1].toInt(),
                startDateArray[2].toInt(),
                endDateArray[0].toInt(),
                endDateArray[1].toInt(),
                endDateArray[2].toInt()
            )
            calendarView.setSchemeDate(map)
            calendarView.scrollToCalendar(startDateArray[0].toInt(),  startDateArray[1].toInt(),  startDateArray[2].toInt())
        }
    }
    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int
    ): com.haibin.calendarview.Calendar {
        val calendar = com.haibin.calendarview.Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.drawIndex = 0
        return calendar
    }

    companion object {
        const val TAG = "CalendarDialogFragment"
        const val RESULT_KEY = "selected_key"
        const val RESULT_KEY_DATE = "selected_date"
        const val RESULT_MASK_DISMISS = "result_mask_dismiss"
        private const val ARG_SELECTED_DATE = "arg_selected_date"
    }
    class Builder {
        private var marginTop: Int = 0
        private var schemeDates: List<Common.DailyMatchCount> = emptyList()
        fun setMarginTop(value: Int) {
            marginTop = value
        }
        fun build(): HomeCalendarFragment {
            return HomeCalendarFragment().apply {
                this.schemeDates = this@Builder.schemeDates
                this.marginTop = this@Builder.marginTop
            }
        }
    }
}