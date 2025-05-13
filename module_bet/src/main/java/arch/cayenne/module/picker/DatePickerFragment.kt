package arch.cayenne.module.picker

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.bet.databinding.FragmentDatePickerBinding
import kotlin.reflect.KClass

class DatePickerFragment private constructor(): BaseBottomSheetFragment<DatePickerViewModel, FragmentDatePickerBinding>() {

    companion object {
        fun newInstance(): DatePickerFragment {
            return DatePickerFragment()
        }
    }
    override val vbClass: KClass<FragmentDatePickerBinding> = FragmentDatePickerBinding::class
    override val vmClass: KClass<DatePickerViewModel> = DatePickerViewModel::class

    private val datePickerAdapter by lazy {
        DatePickerAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvDate.adapter = datePickerAdapter
    }

    override fun initListener() {
        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.dateTitleListener.observe(viewLifecycleOwner) {
            datePickerAdapter.submitList(it)
        }
    }
}