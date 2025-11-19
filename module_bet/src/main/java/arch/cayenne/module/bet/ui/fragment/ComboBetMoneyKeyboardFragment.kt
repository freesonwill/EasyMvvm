package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.constants.QuickAmountEnum
import arch.cayenne.lib.common.data.constants.QuickAmountKeyboardEnum
import arch.cayenne.lib.common.ui.adapter.QuickAmountAdapter
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.setOnClickOrLongPressListener
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.databinding.FragmentComboBetMoneyKeyboardDialogBinding
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import kotlin.reflect.KClass

/**
 * @date: 2025/11/14 20:50
 * @description:
 */
class ComboBetMoneyKeyboardFragment:BaseFragment<ComboBetMoneyKeyboardDialogViewModel, FragmentComboBetMoneyKeyboardDialogBinding>() {
    companion object {
        private const val ET_MONEY = "etMoney"
        private const val TV_MONEY = "tvMoney"
        private const val CURRENT_MONEY_NUMBER = "currentMoneyNumber"
        private const val MIN_NUMBER = "minNumber"
        private const val MAX_NUMBER = "maxNumber"

        fun newInstance(@IdRes etMoney:Int,@IdRes tvMoney:Int, currentMoney: Long, minNumber: Long, maxNumber: Long): ComboBetMoneyKeyboardDialogFragment {
            val b = Bundle().apply {
                putInt(ET_MONEY, etMoney)
                putInt(TV_MONEY, tvMoney)
                putLong(CURRENT_MONEY_NUMBER, currentMoney)
                putLong(MIN_NUMBER, minNumber)
                putLong(MAX_NUMBER, maxNumber)
            }
            return ComboBetMoneyKeyboardDialogFragment().apply {
                arguments = b
            }
        }
    }
    private lateinit var etMoney:EditText
    private lateinit var tvMoney:TextView
    private val resultBundle: Bundle by lazy {
        Bundle()
    }

    override val vbClass: KClass<FragmentComboBetMoneyKeyboardDialogBinding>
        get() = FragmentComboBetMoneyKeyboardDialogBinding::class
    override val vmClass: KClass<ComboBetMoneyKeyboardDialogViewModel>
        get() = ComboBetMoneyKeyboardDialogViewModel::class

    private val quickAmountAdapter: QuickAmountAdapter by lazy {
        QuickAmountAdapter(QuickAmountKeyboardEnum.COMBO) {
            mViewModel.setNumber(it)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        etMoney = requireActivity().findViewById(requireArguments().getInt(ET_MONEY))
        tvMoney = requireActivity().findViewById(requireArguments().getInt(TV_MONEY))

        ViewUtils.hideKeyboard(requireContext(), etMoney)
        initKeyboard()

        mBinding.rvQuickAmount.adapter = quickAmountAdapter
        quickAmountAdapter.submitList(QuickAmountEnum.entries)

        mBinding.numberKeyboard.setOnCalculatorClickListener(object :
            NumberKeyboardView.OnCalculatorClickListener {
            override fun onNumberClick(number: Int) {
                mViewModel.addNumber(number)
            }

            override fun onDotClick() {
                mViewModel.setDot()
            }

            override fun onOtherClick() {
                mViewModel.setMaxMoney()
            }

            override fun getOtherText(): String {
                return getString(R.string.btn_max)
            }
        })
        mBinding.numberKeyboard.setOtherTextSize(13f)
    }

    override fun initListener() {
        mBinding.btnBack.setOnClickOrLongPressListener (onClick = {
            mViewModel.backNumber()
        }, onLongPressRepeat = {
            mViewModel.backNumber()
        })
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearNumber()
        }
    }

    override suspend fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            etMoney.setText(it)
            val length = it.length
            etMoney.setSelection(length)
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            etMoney.hint = getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            it.msg?.let { msg ->
                showToast(msg)
            }
        }
        mViewModel.onCurrencyListener.observe(viewLifecycleOwner) {
            tvMoney.text = CurrencySymbols.getSymbol(it)
        }
    }

    private fun initKeyboard() {
        val minNumber = requireArguments().getLong(MIN_NUMBER, -1L)
        val maxNumber = requireArguments().getLong(MAX_NUMBER, -1L)
        if (minNumber != -1L && maxNumber != -1L) {
            mViewModel.setNumberLimit(minNumber, maxNumber)
        }

        val currentMoney = requireArguments().getLong(CURRENT_MONEY_NUMBER, 0L)
        if (currentMoney != 0L) {
            mViewModel.setNumber(currentMoney.getMoney())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setFragmentResult(KEY_RESULT, resultBundle.apply {
            if (!this.containsKey(VALUE_MONEY_INPUT)) {
                putLong(
                    VALUE_MONEY_INPUT, requireArguments().getLong(CURRENT_MONEY_NUMBER, 0L))
            }
        })
    }
}