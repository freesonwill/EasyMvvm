package arch.cayenne.module.betslip.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.NumberPicker
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentTimePickerBinding
import arch.cayenne.module.betslip.ui.viewmodel.TimePickerViewModel
import java.util.Calendar
import kotlin.reflect.KClass

class TimePickerFragment private constructor() :
    BaseBottomSheetFragment<TimePickerViewModel, FragmentTimePickerBinding>() {

    companion object {
        fun newInstance(time: Long? = null): TimePickerFragment {
            return TimePickerFragment().apply {
                if (time != null) {
                    arguments = Bundle().apply {
                        putLong(Config.VALUE_SELECTED_DATE, time)
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
        val time = arguments?.getLong(Config.VALUE_SELECTED_DATE)
        if (time != null) {
            calendar.timeInMillis = time
        }

        initYearPicker()
        initMonthPicker()
        initDayPicker()
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }
        mBinding.tvConfirm.setOnClickListener {
            val time = calendar.timeInMillis
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
                putLong(Config.VALUE_SELECTED_DATE, time)
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
            hidePickerDivider(this)
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
            hidePickerDivider(this)
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
            hidePickerDivider(this)
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
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (day > maxDay) {
            mBinding.dayPicker.value = maxDay
            mBinding.dayPicker.maxValue = maxDay
            calendar.set(year, month - 1, maxDay)
        } else {
            mBinding.dayPicker.value = day
            mBinding.dayPicker.maxValue = maxDay
            calendar.set(year, month - 1, day)
        }
    }

    private fun hidePickerDivider(picker: NumberPicker) {
        try {
            val pickerFields = NumberPicker::class.java.declaredFields
            for (field in pickerFields) {
                if ("mSelectionDivider" == field.name) {
                    field.isAccessible = true
                    field.set(picker, ColorDrawable(Color.TRANSPARENT))
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}