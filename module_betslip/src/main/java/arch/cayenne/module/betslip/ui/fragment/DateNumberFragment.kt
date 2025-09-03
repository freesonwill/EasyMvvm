package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentDateNumberBinding
import arch.cayenne.module.betslip.ui.fragment.DatePickerFragment.Direction
import arch.cayenne.module.betslip.ui.viewmodel.DateNumberViewModel
import java.util.Calendar
import kotlin.reflect.KClass

class DateNumberFragment: BasePreLoadBottomSheetFragment<DateNumberViewModel, FragmentDateNumberBinding>() {

    companion object {
        private const val TAG = "DateNumberFragment"

        fun create(fragment: Fragment): DateNumberFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? DateNumberFragment
            return if (f == null) {
                val newF = DateNumberFragment()
                newF.customAttach(fragment, TAG)
                newF
            } else {
                f
            }
        }

        fun find(fragment: Fragment): DateNumberFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? DateNumberFragment
            return f ?: create(fragment)
        }
    }

    override val vbClass: KClass<FragmentDateNumberBinding> = FragmentDateNumberBinding::class
    override val vmClass: KClass<DateNumberViewModel> = DateNumberViewModel::class
    private var direction = Direction.TIME

    override fun initView(savedInstanceState: Bundle?) {
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
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            parentFragment?.let { parentFragment ->
                getHideAnimator()?.let { animator ->
                    direction = Direction.DATE
                    DatePickerFragment.find(parentFragment).customShow(animator)
                }
            }
        }
        mBinding.tvConfirm.setOnClickListener {
            parentFragment?.let { parentFragment ->
                val bundle = Bundle().apply {
                    arguments = this
                }
                bundle.apply {
                    putString(Config.VALUE_SELECTED_DATE, BetSlipDateFilterEnum.CUSTOM.name)
                    putLong(Config.VALUE_SELECTED_MILLISECOND, mViewModel.getCustomTime)
                }
                parentFragment.parentFragmentManager.setFragmentResult(Config.KEY_RESULT, bundle)
                dismiss()
            }
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.customTimeListener.observe(viewLifecycleOwner) { time ->
            val c = mViewModel.calendar
            initYearPicker(c)
            initMonthPicker(c)
            initDayPicker(c)
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

    fun reset() {
        mViewModel.setCustomTime(null)
    }

    override fun setCustomExpendSetting() {
        super.setCustomExpendSetting()
        arguments = null
        direction = Direction.TIME
    }

    override fun customHide() {
        if (arguments == null && direction == Direction.TIME) {
            parentFragment?.parentFragmentManager?.setFragmentResult(Config.KEY_RESULT, Bundle())
        }
        super.customHide()
    }
}