package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.data.constants.AnimationConstants
import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentDatePickerBinding
import arch.cayenne.module.betslip.ui.adapter.DatePickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.DatePickerViewModel
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

        fun find(
            fragment: Fragment,
            defaultDate: BetSlipDateFilterEnum? = null,
            customTime: Long? = null
        ): DatePickerFragment {
            val newF = if (fragment.tag == TAG) {
                fragment as DatePickerFragment
            } else {
                val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? DatePickerFragment
                f ?: create(fragment)
            }
            defaultDate?.let {
                newF.setDate(it, customTime)
            }
            return newF
        }
    }

    override val vbClass: KClass<FragmentDatePickerBinding> = FragmentDatePickerBinding::class
    override val vmClass: KClass<DatePickerViewModel> = DatePickerViewModel::class

    private val datePickerAdapter by lazy {
        DatePickerAdapter(object :
            DatePickerAdapter.OnDateClickListener {
            override fun onCustomClick() {
                DateNumberFragment.find(this@DatePickerFragment).apply {
                    getHideAnimator()?.let { animator ->
                        customShow(animator)
                    }
                }
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
            dismiss()
        }
        mBinding.tvConfirm.setOnClickListener {
            val bundle = Bundle()
            val date = mViewModel.getSelectedDate()
            bundle.apply {
                putString(Config.VALUE_SELECTED_DATE, date.name)
            }
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, bundle)
            dismiss()
        }
        setOnEndListener {
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
    }

    override fun onResume() {
        super.onResume()
        DateNumberFragment.create(this)
    }
}