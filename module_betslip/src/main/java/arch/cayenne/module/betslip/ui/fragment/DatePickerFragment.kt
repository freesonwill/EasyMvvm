package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentDatePickerBinding
import arch.cayenne.module.betslip.ui.adapter.DatePickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.DatePickerViewModel
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
                TimePickerFragment.newInstance(mViewModel.customTime).show(parentFragmentManager)
                dismiss()
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
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }
        mBinding.tvConfirm.setOnClickListener {
            val date = mViewModel.getSelectedDate()
            resultBundle.apply {
                putString(Config.VALUE_SELECTED_DATE, date.name)
                if (date == BetSlipDateFilterEnum.CUSTOM) {
                    putLong(Config.VALUE_SELECTED_MILLISECOND, mViewModel.customTime ?: 0L)
                }
            }
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
            dismiss()
        }
    }

    override fun initData() {
        super.initData()
        requireArguments().getString(KEY_DATE)?.let {
            val date = BetSlipDateFilterEnum.valueOf(it)
            if (date == BetSlipDateFilterEnum.CUSTOM) {
                if (requireArguments().containsKey(Config.VALUE_SELECTED_MILLISECOND)) {
                    val time = requireArguments().getLong(Config.VALUE_SELECTED_MILLISECOND)
                    mViewModel.customTime = time
                }
            } else {
                val position = date.ordinal
                mViewModel.setSelected(position)
            }
        }
    }

    override fun createObserver() {
        mViewModel.dateTitleListener.observe(viewLifecycleOwner) {
            datePickerAdapter.submitList(it)
        }
    }
}