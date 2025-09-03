package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.data.constants.AnimationConstants
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentDatePickerBinding
import arch.cayenne.module.betslip.ui.adapter.DatePickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.DatePickerViewModel
import arch.cayenne.module.betslip.utisl.BetSlipViewHelper
import java.util.Calendar
import kotlin.reflect.KClass

class DatePickerFragment private constructor() :
    BasePreLoadBottomSheetFragment<DatePickerViewModel, FragmentDatePickerBinding>() {

    companion object {
        private const val TAG = "DatePickerFragment"

        fun create(fragment: Fragment): DatePickerFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? DatePickerFragment
            return if (f == null) {
                val newF = DatePickerFragment()
                newF.customAttach(fragment, TAG)
                newF
            } else {
                f
            }
        }

        fun show(
            fragment: Fragment,
            defaultDate: BetSlipDateFilterEnum,
            customTime: Long? = null
        ): DatePickerFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? DatePickerFragment
            val newF = f ?: create(fragment)
            newF.setDate(defaultDate, customTime)
            newF.customShow()
            return newF
        }
    }

    override val vbClass: KClass<FragmentDatePickerBinding> = FragmentDatePickerBinding::class
    override val vmClass: KClass<DatePickerViewModel> = DatePickerViewModel::class

    private val datePickerAdapter by lazy {
        DatePickerAdapter(object :
            DatePickerAdapter.OnDateClickListener {
            override fun onCustomClick() {
                mViewModel.turnToDatePicker()
            }

            override fun onDateClick(position: Int) {
                mViewModel.setSelected(position)
            }

            override fun onCancelClick() {
                mViewModel.cancel()
            }
        })
    }

    override fun enterAnimation(): Animation {
        val slideIn = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 1f,  // fromYDelta = 100%p
            Animation.RELATIVE_TO_PARENT, 0f   // toYDelta = 0
        ).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            interpolator = LinearInterpolator()
        }
        return slideIn
    }

    override fun initView(savedInstanceState: Bundle?) {
        isGestureEnable = false
        val layoutManager = GridLayoutManager(requireContext(), 30) // 每行3格
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = datePickerAdapter.currentList[position].title
                return when {
                    item.length > 4 -> 16
                    else -> 10
                }
            }
        }
        mBinding.rvDate.layoutManager = layoutManager
        mBinding.rvDate.adapter = datePickerAdapter
        (mBinding.rvDate.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvDate.itemAnimator = null
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            val page = mViewModel.pageListener.value
            if (page == DatePickerViewModel.Page.DATE) {
                mViewModel.backToTimePicker()
            } else {
                dismiss()
            }
        }
        mBinding.tvConfirm.setOnClickListener {
            val page = mViewModel.pageListener.value
            val bundle = Bundle()
            if (page == DatePickerViewModel.Page.DATE) {
                bundle.apply {
                    putString(Config.VALUE_SELECTED_DATE, BetSlipDateFilterEnum.CUSTOM.name)
                    putLong(Config.VALUE_SELECTED_MILLISECOND, mViewModel.getCustomTime)
                }
            } else {
                val date = mViewModel.getSelectedDate()
                bundle.apply {
                    putString(Config.VALUE_SELECTED_DATE, date.name)
                }
            }
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, bundle)
            dismiss()
        }
        mBinding.yearPicker.setOnValueChangedListener { _, _, newVal ->
            val month = mBinding.monthPicker.value
            val day = mBinding.dayPicker.value
            updateDayPicker(newVal, month, day)
        }
        mBinding.monthPicker.setOnValueChangedListener { _, _, newVal ->
            val year = mBinding.yearPicker.value
            val day = mBinding.dayPicker.value
            updateDayPicker(year, newVal, day)
        }
        mBinding.dayPicker.setOnValueChangedListener { _, _, newVal ->
            val year = mBinding.yearPicker.value
            val month = mBinding.monthPicker.value

            val calendar = mViewModel.calendar
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, newVal)
        }
        setOnEndListener {
            val page = mViewModel.pageListener.value
            if (page == DatePickerViewModel.Page.DATE) {
                mViewModel.backToTimePicker()
            }
            if (arguments?.containsKey(Config.VALUE_SELECTED_DATE) == false) {
                // 如果沒有選擇日期，則清除結果
                parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle())
            }
        }
    }

    private fun setDate(defaultDate: BetSlipDateFilterEnum, customTime: Long? = null) {
        if (defaultDate == BetSlipDateFilterEnum.CUSTOM) {
            if (customTime != null) {
                mViewModel.setCustomTime(customTime)
            }
        }
        mViewModel.setSelected(defaultDate.ordinal)
    }

    override fun customShow() {
        arguments = null
        super.customShow()
    }

    override fun customHide() {
        if (arguments == null || requireArguments().isEmpty) {
            // 如果沒有選擇日期，則清除結果
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle())
        }
        super.customHide()
    }

    override suspend fun createObserver() {
        mViewModel.dateTitleListener.observe(viewLifecycleOwner) {
            datePickerAdapter.submitList(it)
        }
        mViewModel.customTimeListener.observe(viewLifecycleOwner) { time ->
            time?.let {
                val c = mViewModel.calendar
                initYearPicker(c)
                initMonthPicker(c)
                initDayPicker(c)
            }
        }
        mViewModel.pageListener.observe(viewLifecycleOwner) { page ->
            if (page == DatePickerViewModel.Page.DATE) {
                BetSlipViewHelper.collapseView(mBinding.root) {
                    mBinding.rvDate.isVisible = false
                    mBinding.layoutTimePicker.isVisible = true
                    val height = mBinding.root.height.toFloat()
                    BetSlipViewHelper.expandView(mBinding.root, height)
                }
            } else {
                BetSlipViewHelper.collapseView(mBinding.root) {
                    mBinding.rvDate.isVisible = true
                    mBinding.layoutTimePicker.isVisible = false
                    val height = mBinding.root.height.toFloat()
                    BetSlipViewHelper.expandView(mBinding.root, height)
                }
            }
        }
    }

    private fun initYearPicker(calendar: Calendar) {
        mBinding.yearPicker.apply {
            wrapSelectorWheel = false

            val curYear = calendar.get(Calendar.YEAR)
            val minYear = curYear - 10
            val maxYear = Calendar.getInstance().get(Calendar.YEAR)
            val year = (minYear..maxYear).map { getString(R.string.date_picker_year).format(it) }
                .toTypedArray()
            minValue = curYear - 10
            maxValue = maxYear
            displayedValues = year
            value = curYear
        }

    }

    private fun initMonthPicker(calendar: Calendar) {
        mBinding.monthPicker.apply {
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

    private fun initDayPicker(calendar: Calendar) {
        mBinding.dayPicker.apply {
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

    private fun updateDayPicker(year: Int, month: Int, day: Int) {
        val calendar = mViewModel.calendar
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val newDay = if (day > maxDay) maxDay else day
        mBinding.dayPicker.displayedValues = null
        calendar.set(Calendar.DAY_OF_MONTH, newDay)
        initDayPicker(calendar)
    }
}