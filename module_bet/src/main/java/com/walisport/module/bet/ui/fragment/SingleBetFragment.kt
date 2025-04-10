package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.common.utils.ViewUtils
import com.walisport.lib.database.entity.BetBean
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentSingleBetBinding
import com.walisport.module.bet.ui.adapter.BetSheetAdapter
import com.walisport.module.bet.ui.custom.NumberKeyboardView
import com.walisport.module.bet.viewmodel.SingleBetViewModel
import com.walisport.module.bet.viewmodel.NumberCalculatorViewModel
import kotlin.reflect.KClass

class SingleBetFragment : BaseFragment<SingleBetViewModel, FragmentSingleBetBinding>() {

    override val vbClass: KClass<FragmentSingleBetBinding> = FragmentSingleBetBinding::class
    override val vmClass: KClass<SingleBetViewModel> = SingleBetViewModel::class

    private lateinit var betSheetAdapter: BetSheetAdapter

    override fun initView(savedInstanceState: Bundle?) {
        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        mBinding.etMoney.requestFocus()

        mBinding.etMoney.hint = getString(R.string.et_money_hint).format(
            NumberCalculatorViewModel.MIN_MONEY,
            NumberCalculatorViewModel.MAX_MONEY
        )

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

        betSheetAdapter = BetSheetAdapter(object : BetSheetAdapter.OnBetSheetClickListener {
            override fun onDeleteClick(item: BetBean) {

            }
        })
//        mBinding.rvBet.adapter = betSheetAdapter
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {

        }
        mBinding.btnBack.setOnClickListener {
            mViewModel.backNumber()
        }
        mBinding.btnClear.setOnClickListener {
            mViewModel.clearNumber()
        }
        mBinding.btnDouble.setOnClickListener {
            mViewModel.doubleNumber()
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
        mBinding.btnCollusion.setOnClickListener {

        }
        mBinding.clBet.setOnClickListener {
            mViewModel.sendBet()
        }
        mBinding.btnReserve.setOnClickListener {
            mViewModel.onBetSheetListener.value?.first()?.let {
                val location = IntArray(2)
                mBinding.btnReserve.getLocationInWindow(location)
                ReserveDialogFragment.newInstance(
                    location.first() + mBinding.btnReserve.width / 2,
                    location.last() + mBinding.btnReserve.height,
                    rateNumber = it.odds
                ).show(childFragmentManager)
            }

        }
    }

    override fun createObserver() {
        mViewModel.onEditMoney.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            betSheetAdapter.submitList(it)
        }
        mViewModel.onBetWinMoney.observe(viewLifecycleOwner) {
            val money = getString(R.string.btn_bet_win_money).format(it)
            mBinding.tvBetMoney.text = money
        }
    }
}