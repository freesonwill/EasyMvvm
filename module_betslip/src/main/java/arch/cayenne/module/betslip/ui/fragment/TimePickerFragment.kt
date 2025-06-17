package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentTimePickerBinding
import arch.cayenne.module.betslip.ui.viewmodel.TimePickerViewModel
import java.util.Calendar
import kotlin.reflect.KClass

class TimePickerFragment private constructor() :
    BaseBottomSheetFragment<TimePickerViewModel, FragmentTimePickerBinding>() {

    companion object {
        private const val KEY_ORIGINAL_TIME = "key_original_time"
        private const val KEY_ORIGINAL_TYPE = "key_original_type"
        fun newInstance(time: Long? = null, originalTime: Long? = null, originalType: BetSlipDateFilterEnum? = null): TimePickerFragment {
            return TimePickerFragment().apply {
                arguments = Bundle().apply {
                    time?.let {
                        putLong(Config.VALUE_SELECTED_DATE, it)
                    }
                    originalTime?.let {
                        putLong(KEY_ORIGINAL_TIME, it)
                    }
                    originalType?.let {
                        putString(KEY_ORIGINAL_TYPE, it.name)
                    }
                }
            }
        }
    }

    override val vbClass: KClass<FragmentTimePickerBinding> = FragmentTimePickerBinding::class
    override val vmClass: KClass<TimePickerViewModel> = TimePickerViewModel::class
    private val calendar by lazy {
        Calendar.getInstance()
    }

    override fun initView(savedInstanceState: Bundle?) {
        val time = arguments?.takeIf { it.containsKey(Config.VALUE_SELECTED_DATE) }
            ?.getLong(Config.VALUE_SELECTED_DATE)
            ?: System.currentTimeMillis()
        calendar.timeInMillis = time

        initYearPicker()
        initMonthPicker()
        initDayPicker()
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            val originalTime = arguments?.getLong(KEY_ORIGINAL_TIME)
            val originalType = arguments?.getString(KEY_ORIGINAL_TYPE)?.let { 
                BetSlipDateFilterEnum.valueOf(it) 
            } ?: BetSlipDateFilterEnum.CUSTOM
            DatePickerFragment.newInstance(originalType, originalTime)
                .show(parentFragmentManager)
            dismiss()
        }
        mBinding.tvConfirm.setOnClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val time = calendar.timeInMillis
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
                putString(Config.VALUE_SELECTED_DATE, BetSlipDateFilterEnum.CUSTOM.name)
                putLong(Config.VALUE_SELECTED_MILLISECOND, time)
            })
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
    }

    private fun initYearPicker() {
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

    private fun initMonthPicker() {
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

    private fun initDayPicker() {
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
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val newDay = if (day > maxDay) maxDay else day
        mBinding.dayPicker.displayedValues = null
        calendar.set(Calendar.DAY_OF_MONTH, newDay)
        initDayPicker()
    }
}