package com.walisport.module.search.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.toColorInt
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.WeekBar
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchDatePickerBinding
import com.walisport.module.search.ui.view.SearchCustomWeekBar
import com.walisport.module.search.ui.viewmodel.SearchBaseViewModel
import com.walisport.module.search.ui.viewmodel.SearchDatePickerViewModel
import com.walisport.module.search.utils.IconScaleAnimUtil.enableScaleIcon
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.reflect.KClass

class SearchDatePickerFragment private constructor(): BaseFragment<SearchDatePickerViewModel, FragmentSearchDatePickerBinding>() {
    override val vbClass: KClass<FragmentSearchDatePickerBinding>
        get() = FragmentSearchDatePickerBinding::class
    override val vmClass: KClass<SearchDatePickerViewModel>
        get() = SearchDatePickerViewModel::class

    private val sharedViewModel: SearchBaseViewModel by sharedViewModel<SearchBaseViewModel, SearchFragment>()

    enum class AnimState {
        EXPANDING, EXPAND, COLLAPSING, COLLAPSE
    }

    private val defaultAnimDuration = 150L

    private var marginTop: Int = 0
    private var marginStart: Int = 0
    private var marginEnd: Int = 0
    private var selectedDate: Long? = null
    private var schemeDates: Map<String, com.haibin.calendarview.Calendar> = emptyMap()
    private var rangeStartDate: Calendar = Calendar.getInstance()
    private var rangeEndDate: Calendar = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
    private var heightAnimator: ValueAnimator? = null
    private var currentAnimState: AnimState? = null
    private var onBeforeDismissAnimListener: (() -> Unit)? = null
    private var onAfterDismissAnimListener: ((startTime: Long?, endTime: Long?, timeInMills: Long?)-> Unit)? = null
    private var onBeforeExpandAnimListener: (() -> Unit)? = null
    private val maskClickListener = {
        currentAnimState?.let { state ->
            if(!mViewModel.isMaskClickable) return@let
            when(state) {
                AnimState.EXPANDING -> collapseView()
                AnimState.EXPAND -> {
                    close()
                }
                else -> expandView()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding.clCalendar) {
            visibility = View.INVISIBLE
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                topMargin = this@SearchDatePickerFragment.marginTop
                leftMargin = this@SearchDatePickerFragment.marginStart
                rightMargin = this@SearchDatePickerFragment.marginEnd
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            ivPrevMonth.apply {
                checkMonthSwitchEnabled(true)
                enableScaleIcon()
            }
            ivNextMonth.apply {
                checkMonthSwitchEnabled(false)
                enableScaleIcon()
            }
            calendarView.apply {
                setSelectSingleMode()
                updateWeekBarLocale()
                setWeeColor(
                    Color.TRANSPARENT,
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_week_text_color
                    )
                )
                setTextColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_red
                    ),
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_current_month_text_color
                    ),
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_other_month_text_color
                    ),
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_current_month_text_color
                    ),
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_other_month_text_color
                    )
                )
                setSelectedColor(
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_selected_theme_color
                    ),
                    Color.WHITE,
                    Color.TRANSPARENT
                )
                setSchemeColor(
                    Color.TRANSPARENT,
                    SkinnableResourceManager.getColor(
                        requireContext(),
                        R.color.search_calendar_red
                    ),
                    Color.TRANSPARENT
                )
                scrollToSelectedDate()
                addSchemeDate(schemeDates)
                setRange(
                    rangeStartDate.get(Calendar.YEAR),
                    rangeStartDate.get(Calendar.MONTH) + 1,
                    rangeStartDate.get(Calendar.DAY_OF_MONTH),
                    rangeEndDate.get(Calendar.YEAR),
                    rangeEndDate.get(Calendar.MONTH) + 1,
                    rangeEndDate.get(Calendar.DAY_OF_MONTH)
                )
                setCalendarTitle(curYear, curMonth)
            }
            maskView.background = object : Drawable() {
                override fun draw(canvas: Canvas) {
                    val paint = Paint()
                    // 上半部分
                    paint.color = Color.TRANSPARENT
                    canvas.drawRect(0f, 0f, bounds.width().toFloat(), mBinding.clCalendar.top.toFloat(), paint)

                    // 下半部
                    paint.color = "#BF000000".toColorInt()
                    canvas.drawRect(
                        0f,
                        mBinding.clCalendar.top.toFloat(),
                        bounds.width().toFloat(),
                        bounds.height().toFloat(),
                        paint
                    )
                }

                override fun setAlpha(alpha: Int) = Unit
                override fun setColorFilter(colorFilter: ColorFilter?) = Unit
                @Suppress("OVERRIDE_DEPRECATION")
                override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
            }
            maskViewTop.layoutParams.height = this@SearchDatePickerFragment.marginTop
            expandView()
        }
    }

    override fun initListener() {
        with(mBinding) {
            calendarView.setOnMonthChangeListener { year, month ->
                setCalendarTitle(year, month)
                ivPrevMonth.checkMonthSwitchEnabled(true, year, month)
                ivNextMonth.checkMonthSwitchEnabled(false, year, month)
            }
            ivPrevMonth.clickNoRepeat {
                calendarView.scrollToPre(true)
            }
            ivNextMonth.clickNoRepeat {
                calendarView.scrollToNext(true)
            }
            tvReset.clickNoRepeat {
                mViewModel.setMaskClickable(false)
                calendarView.clearSingleSelect()
                sendResult(null)
                collapseView()
            }
            tvConfirm.clickNoRepeat {
                mViewModel.setMaskClickable(false)
                sendResult()
                collapseView()
            }
            maskView.setOnClickListener { maskClickListener.invoke() }
            maskViewTop.setOnClickListener { maskClickListener.invoke() }
        }
    }

    override suspend fun createObserver() {
        launch(Lifecycle.State.STARTED) {
            sharedViewModel.currentLanguage.collect {
                // 更新日曆標題
                setCalendarTitle(
                    mBinding.calendarView.curYear,
                    mBinding.calendarView.curMonth
                )
                // 更新weekBar的語言
                updateWeekBarLocale()
            }
        }
    }

    private fun ImageView.checkMonthSwitchEnabled(
        isPrev: Boolean,
        year: Int = mBinding.calendarView.curYear,
        month: Int = mBinding.calendarView.curMonth
    ) {
        fun Calendar.isSameDay(other: Calendar): Boolean {
            return get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                    get(Calendar.MONTH) == other.get(Calendar.MONTH) &&
                    get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
        }

        isEnabled = Calendar.getInstance()
            .apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, if (isPrev) -1 else 1)
            }.run {
                if(isPrev) (rangeStartDate.clone() as Calendar)
                    .apply { set(Calendar.DAY_OF_MONTH, 1) }
                    .let { prev -> !before(prev) || isSameDay(prev) }
                else (rangeEndDate.clone() as Calendar)
                    .apply { set(Calendar.DAY_OF_MONTH, 1) }
                    .let { next -> !after(next) || isSameDay(next) }
            }
    }

    @SuppressLint("DiscouragedApi")
    private fun updateWeekBarLocale() {
        with(mBinding.calendarView) {
            getWeekBarByReflection()?.apply {
                if(this is SearchCustomWeekBar) {
                    setLocale(sharedViewModel.getCurrentLanguage())
                    updateWeekBar()
                }
            }
        }
    }

    private fun CalendarView.getWeekBarByReflection(): WeekBar? {
        return runCatching {
            CalendarView::class.java
                .getDeclaredField("mWeekBar")
                .apply { isAccessible = true }
                .get(this) as? WeekBar
        }.onFailure { it.printStackTrace() }
            .getOrNull()
    }

    private fun setCalendarTitle(year: Int, month: Int) {
        val locale = sharedViewModel.getCurrentLanguage()
        val monthStr =
            SimpleDateFormat("MMMM", locale)
                .format(
                    Calendar.getInstance(locale).apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month - 1)
                    }.time
                )


        mBinding.tvCalendarTitle.text =
            String.format(
                SkinnableResourceManager.getString(
                    requireContext(),
                    R.string.search_result_race_calendar_title,
                    locale
                ),
                monthStr,
                "$year"
            )
    }

    private fun Long.toDateStartTime(): Long {
        return Calendar.getInstance().apply {
            timeInMillis = this@toDateStartTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun Long.toDateEndTime(): Long {
        return Calendar.getInstance().apply {
            timeInMillis = this@toDateEndTime
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun scrollToSelectedDate() {
        with(mBinding.calendarView) {
            selectedDate?.let {
                Calendar.getInstance().apply {
                    timeInMillis = it
                }.run {
                    scrollToCalendar(
                        get(Calendar.YEAR),
                        get(Calendar.MONTH) + 1,
                        get(Calendar.DAY_OF_MONTH)
                    )
                }
            } ?: clearSingleSelect()
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

    private fun getFullyHeight(): Int {
        with(mBinding.clCalendar) {
            measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            return measuredHeight
        }
    }

    private fun expandView() {
        with(mBinding.clCalendar) {
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
                        onBeforeExpandAnimListener?.invoke()
                    }
                    doOnEnd { currentAnimState = AnimState.EXPAND }
                    start()
                }
            }
        }
    }

    private fun collapseView() {
        with(mBinding.clCalendar) {
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
                    onBeforeDismissAnimListener?.invoke()
                }
                doOnEnd {
                    currentAnimState = AnimState.COLLAPSE
                    visibility = View.INVISIBLE
                    mBinding.root.postDelayed({
                        if(currentAnimState == AnimState.COLLAPSE) {
                            dismiss()
                            mViewModel.resultTime.let { time ->
                                onAfterDismissAnimListener?.invoke(
                                    time?.toDateStartTime(),
                                    time?.toDateEndTime(),
                                    time
                                )
                            }
                        }
                    }, 100L)
                }
                start()
            }
        }
    }

    fun close() {
        if(mViewModel.isMaskClickable) {
            sendResult(selectedDate)
            collapseView()
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

    private fun dismiss() {
        if (parentFragment != null) {
            mBinding.clCalendar.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun sendResult(date: Long? = mBinding.calendarView.selectedCalendar.timeInMillis) {
        mViewModel.setResultTime(date)
    }

    class Builder {
        private var marginTop: Int = 0
        private var marginStart: Int = 0
        private var marginEnd: Int = 0
        private var selectedDate: Long? = null
        private var schemeDates: Map<String, com.haibin.calendarview.Calendar> = emptyMap()
        private var rangeStartDate: Calendar = Calendar.getInstance()
        private var rangeEndDate: Calendar = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
        private var onAfterDismissAnimListener: ((startTime: Long?, endTime: Long?, timeInMills: Long?) -> Unit)? = null
        private var onBeforeDismissAnimListener: (() -> Unit)? = null
        private var onBeforeExpandAnimListener: (() -> Unit)? = null

        fun setMarginTop(value: Int) {
            marginTop = value
        }

        fun setMarginStart(value: Int) {
            marginStart = value
        }

        fun setMarginEnd(value: Int) {
            marginEnd = value
        }

        fun setSelectedDate(date: Long)  {
            selectedDate = date
        }

        fun setSchemeDates(dates: Map<String, com.haibin.calendarview.Calendar>) {
            schemeDates = dates
        }

        fun setRange(start: Calendar, end: Calendar) {
            rangeStartDate = start
            rangeEndDate = end
        }

        fun setOnAfterDismissAnimListener(listener: (startTime: Long?, endTime: Long?, timeInMills: Long?) -> Unit) = apply {
            this.onAfterDismissAnimListener = listener
        }

        fun setOnBeforeDismissAnimListener(listener: () -> Unit) = apply {
            this.onBeforeDismissAnimListener = listener
        }

        fun setOnBeforeExpandAnimListener(listener: () -> Unit) = apply {
            this.onBeforeExpandAnimListener = listener
        }

        fun build(): SearchDatePickerFragment {
            return SearchDatePickerFragment().apply {
                this.marginTop = this@Builder.marginTop
                this.marginStart = this@Builder.marginStart
                this.marginEnd = this@Builder.marginEnd
                this.selectedDate = this@Builder.selectedDate
                this.schemeDates = this@Builder.schemeDates
                this.rangeStartDate = this@Builder.rangeStartDate
                this.rangeEndDate = this@Builder.rangeEndDate
                this.onBeforeDismissAnimListener = this@Builder.onBeforeDismissAnimListener
                this.onAfterDismissAnimListener = this@Builder.onAfterDismissAnimListener
                this.onBeforeExpandAnimListener = this@Builder.onBeforeExpandAnimListener
            }
        }
    }
}