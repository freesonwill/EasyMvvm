package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.ui.BaseBottomSheetFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentBetSheetBinding
import com.walisport.module.bet.ui.custom.NumberKeyboardView
import com.walisport.module.bet.viewmodel.NumberCalculatorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BetSheetFragment : BaseBottomSheetFragment<FragmentBetSheetBinding>() {

    override val mBinding: FragmentBetSheetBinding by viewBind()
    private val mViewModel: NumberCalculatorViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.etMoney.requestFocus()
        mBinding.etMoney.hint = getString(R.string.et_money_hint).format(NumberCalculatorViewModel.MIN_MONEY, NumberCalculatorViewModel.MAX_MONEY)

        mBinding.numberKeyboard.setOnCalculatorClickListener(object : NumberKeyboardView.OnCalculatorClickListener {
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
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnBack.setOnClickListener {
            mViewModel.back()
        }
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearMoney()
        }
        mBinding.btnDouble.setOnClickListener {
            mViewModel.double()
        }
        mBinding.btn100.setOnClickListener {
            mViewModel.setNumber(100)
        }
        mBinding.btn500.setOnClickListener {
            mViewModel.setNumber(500)
        }
        mBinding.btn1000.setOnClickListener {
            mViewModel.setNumber(1000)
        }
        mBinding.btn2000.setOnClickListener {
            mViewModel.setNumber(2000)
        }
        mBinding.btn5000.setOnClickListener {
            mViewModel.setNumber(5000)
        }
    }

    override fun createObserver() {
        mViewModel.onEditMoney.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
    }
}