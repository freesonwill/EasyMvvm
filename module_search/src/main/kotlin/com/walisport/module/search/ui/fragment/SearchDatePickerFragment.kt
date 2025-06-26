package com.walisport.module.search.ui.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
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
import com.walisport.module.search.ui.viewmodel.SearchDatePickerViewModel
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.reflect.KClass

class SearchDatePickerFragment private constructor(): BaseFragment<SearchDatePickerViewModel, FragmentSearchDatePickerBinding>() {
    override val vbClass: KClass<FragmentSearchDatePickerBinding>
        get() = FragmentSearchDatePickerBinding::class
    override val vmClass: KClass<SearchDatePickerViewModel>
        get() = SearchDatePickerViewModel::class

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()

    private val defaultAnimDuration = 300L

    // 回傳結果的Bundle
    private val resultBundle by lazy { Bundle() }
    private val selectedDate by lazy {
        if (requireArguments().containsKey(DATE_PICKER_RESULT_TIME_IN_MILLIS)) {
            requireArguments().getLong(DATE_PICKER_RESULT_TIME_IN_MILLIS)
        } else {
            null
        }
    }

    companion object {
        const val DATE_PICKER_RESULT_KEY = "DATE_PICKER_RESULT_KEY"
        const val DATE_PICKER_RESULT_START = "DATE_PICKER_RESULT_START"
        const val DATE_PICKER_RESULT_END = "DATE_PICKER_RESULT_END"
        const val DATE_PICKER_RESULT_TIME_IN_MILLIS = "DATE_PICKER_RESULT_TIME_IN_MILLIS"
        private const val MARGIN_TOP = "MARGIN_TOP"
        private const val MARGIN_START = "MARGIN_START"
        private const val MARGIN_END = "MARGIN_END"
        fun newInstance(marginTop: Int = 0, marginStart: Int = 0, marginEnd: Int  = 0, selectedDate: Long? = null): SearchDatePickerFragment {
            return SearchDatePickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(MARGIN_TOP, marginTop)
                    putInt(MARGIN_START, marginStart)
                    putInt(MARGIN_END, marginEnd)
                    selectedDate?.let {
                        putLong(DATE_PICKER_RESULT_TIME_IN_MILLIS, it)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            with(mBinding.clCalendar) {
                visibility = View.INVISIBLE
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    topMargin = requireArguments().getInt(MARGIN_TOP, 0)
                    leftMargin = requireArguments().getInt(MARGIN_START, 0)
                    rightMargin = requireArguments().getInt(MARGIN_END, 0)
                }

            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            calendarView.apply {
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

                setAllMode()
                setOnMonthChangeListener { year, month ->
                    setCalendarTitle(year, month)
                }
                updateWeekBarLocale()
                setWeeColor(
                    Color.TRANSPARENT,
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_week_text_color)
                )
                setTextColor(
                    Color.parseColor("#ff0000"),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_current_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_other_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_current_month_text_color),
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_other_month_text_color)
                )
                setSelectedColor(
                    SkinnableResourceManager.getColor(requireContext(), R.color.search_calendar_selected_theme_color),
                    Color.WHITE,
                    Color.TRANSPARENT
                )
                setCalendarTitle(curYear, curMonth)
            }
            maskView.background = createMaskGradient()
            setMaskViewAlpha(true)
            expandView()
        }
    }

    override fun initListener() {
        with(mBinding) {
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
                setMaskViewAlpha(false)
                collapseView()
            }
            tvConfirm.clickNoRepeat {
                mViewModel.setMaskClickable(false)
                sendResult()
                setMaskViewAlpha(false)
                collapseView()
            }
            maskView.clickNoRepeat {
                close()
            }
        }
    }

    override fun createObserver() {
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

    private fun createMaskGradient(): Drawable {
        val defaultColor = 0x80000000
        val defaultStartAt = 0.3f
        val defaultStopAt = 0.8f
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
                    }
                    start()
                }
            }
        }
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

    fun close() {
        if(mViewModel.isMaskClickable) {
            sendResult(selectedDate)
            setMaskViewAlpha(false)
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
            parentFragmentManager.setFragmentResult(DATE_PICKER_RESULT_KEY, resultBundle)
            mBinding.clCalendar.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun sendResult(date: Long? = mBinding.calendarView.selectedCalendar.timeInMillis) {
        with(date) {
            this?.let {
                resultBundle.putLong(DATE_PICKER_RESULT_START, toDateStartTime())
                resultBundle.putLong(DATE_PICKER_RESULT_END, toDateEndTime())
                resultBundle.putLong(DATE_PICKER_RESULT_TIME_IN_MILLIS, this)
            }
        }
    }
}