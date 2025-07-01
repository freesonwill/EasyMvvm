package arch.cayenne.module.betslip.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
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
    BaseBottomSheetFragment<DatePickerViewModel, FragmentDatePickerBinding>() {

    companion object {
        private const val KEY_DATE = "key_date"
        fun newInstance(defaultDate: BetSlipDateFilterEnum, customTime: Long? = null): DatePickerFragment {
            return DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_DATE, defaultDate.name)
                    customTime?.let {
                        putLong(Config.VALUE_SELECTED_MILLISECOND, it)
                    }
                }
            }
        }
    }

    override val vbClass: KClass<FragmentDatePickerBinding> = FragmentDatePickerBinding::class
    override val vmClass: KClass<DatePickerViewModel> = DatePickerViewModel::class
    private val resultBundle by lazy {
        Bundle()
    }

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

    override fun initView(savedInstanceState: Bundle?) {
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
            if (page == DatePickerViewModel.Page.DATE) {
                resultBundle.apply {
                    putString(Config.VALUE_SELECTED_DATE, BetSlipDateFilterEnum.CUSTOM.name)
                    putLong(Config.VALUE_SELECTED_MILLISECOND, mViewModel.getCustomTime)
                }
            } else {
                val date = mViewModel.getSelectedDate()
                resultBundle.apply {
                    putString(Config.VALUE_SELECTED_DATE, date.name)
                }
            }
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
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

    override fun initData() {
        super.initData()
        requireArguments().getString(KEY_DATE)?.let {
            val date = BetSlipDateFilterEnum.valueOf(it)
            if (date == BetSlipDateFilterEnum.CUSTOM) {
                if (requireArguments().containsKey(Config.VALUE_SELECTED_MILLISECOND)) {
                    val time = requireArguments().getLong(Config.VALUE_SELECTED_MILLISECOND)
                    mViewModel.setCustomTime(time)
                }
            }
            mViewModel.setSelected(date.ordinal)
        }
    }

    override fun createObserver() {
        mViewModel.dateTitleListener.observe(viewLifecycleOwner) {
            datePickerAdapter.submitList(it)
        }
        mViewModel.customTimeListener.observe(viewLifecycleOwner) { time ->
            val c = mViewModel.calendar
            initYearPicker(c)
            initMonthPicker(c)
            initDayPicker(c)
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


    override fun onDismiss(dialog: DialogInterface) {
        if (!resultBundle.containsKey(Config.VALUE_SELECTED_DATE)) {
            // 如果沒有選擇日期，則清除結果
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
        }
        super.onDismiss(dialog)
    }
}