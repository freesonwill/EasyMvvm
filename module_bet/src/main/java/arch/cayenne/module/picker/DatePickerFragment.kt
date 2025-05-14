package arch.cayenne.module.picker

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
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
        DatePickerAdapter(object : DatePickerAdapter.OnDateClickListener {
            override fun onCustomClick() {

            }

            override fun onDateClick(position: Int) {
                mViewModel.setSelected(position)
            }

            override fun onCancelClick() {
                mViewModel.cancel()
            }
        } )
    }

    override fun initView(savedInstanceState: Bundle?) {
        val layoutManager = GridLayoutManager(requireContext(), 30) // 每行3格
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = datePickerAdapter.currentList[position].date
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
    }

    override fun createObserver() {
        mViewModel.dateTitleListener.observe(viewLifecycleOwner) {
            datePickerAdapter.submitList(it)
        }
    }
}