package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.sendResult
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.IntExt.getOdds
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentSingleBetBinding
import arch.cayenne.module.bet.ui.custom.NumberKeyboardView
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.ReserveViewModel
import kotlin.reflect.KClass

class ReserveFragment : BaseFragment<ReserveViewModel, FragmentSingleBetBinding>(), BetSheetListener {

    override val vbClass: KClass<FragmentSingleBetBinding> = FragmentSingleBetBinding::class
    override val vmClass: KClass<ReserveViewModel> = ReserveViewModel::class
    private val args: ReserveFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        mBinding.etMoney.requestFocus()
        mBinding.tvBetHint.text = getString(R.string.title_reserve)

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

        mViewModel.setReserveBet(args.id)
    }

    override fun initListener() {
        mBinding.ivCancelReserve.setOnClickListener {
            mViewModel.removeReserve(args.id)
            navigate(ReserveFragmentDirections.actionReserveFragmentToSingleBetFragment())
        }

        mBinding.ivClose.setOnClickListener {
            mViewModel.removeBet()
            dismiss()
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
            mViewModel.setNumber(10000)
        }
        mBinding.btn500.setOnClickListener {
            mViewModel.setNumber(50000)
        }
        mBinding.btn1000.setOnClickListener {
            mViewModel.setNumber(100000)
        }
        mBinding.btn2000.setOnClickListener {
            mViewModel.setNumber(200000)
        }
        mBinding.btn5000.setOnClickListener {
            mViewModel.setNumber(500000)
        }
        mBinding.btnCollusion.setOnClickListener {
            mViewModel.saveToCombo()
            dismiss()
        }
        mBinding.clBet.setOnClickListener {
            mViewModel.sendReserve()
            val id = mViewModel.onReserveSheetListener.value?.matchId ?: -1
            navigate(ReserveFragmentDirections.actionReserveFragmentToBetResultFragment(id))
        }
    }

    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
        mViewModel.onReserveSheetListener.observe(viewLifecycleOwner) {
            setBetData(it)
        }
        mViewModel.onReserveWinMoney.observe(viewLifecycleOwner) {
            val money = getString(R.string.btn_bet_win_money).format(it)
            mBinding.tvBetMoney.text = money
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            mBinding.etMoney.hint = getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            // TODO show toast
        }
    }

    private fun setBetData(data: BetBean) {
        ViewHelper.bindBetSheet(data, mBinding.layoutBet)

        mBinding.btnReserve.isVisible = false
        mBinding.clCancelReserve.isVisible = true
        val odds = "@${data.reverseOdds?.getOdds()}"
        mBinding.tvCancelReserve.text = odds
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.reserveFragment)
    }
}