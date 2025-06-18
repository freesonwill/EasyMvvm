package com.walisport.module.search.ui.fragment

import android.animation.ValueAnimator
import android.graphics.Color
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
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchDatePickerBinding
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
                calendarView.clearSingleSelect()
                sendResult(null)
                collapseView()
            }
            tvConfirm.clickNoRepeat {
                sendResult()
                collapseView()
            }
            maskView.clickNoRepeat {
                sendResult(selectedDate)
                collapseView()
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
                    duration = 300
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
                duration = 300
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
        collapseView()
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