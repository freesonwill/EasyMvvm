package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.sendResult
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.IntExt.getOdds
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.Config.KEY_RESULT
import arch.cayenne.module.bet.Config.VALUE_RESERVE_COMPLETE
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentSingleBetBinding
import arch.cayenne.module.bet.ui.custom.NumberKeyboardView
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import kotlin.reflect.KClass

class SingleBetFragment : BaseFragment<SingleBetViewModel, FragmentSingleBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentSingleBetBinding> = FragmentSingleBetBinding::class
    override val vmClass: KClass<SingleBetViewModel> = SingleBetViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney)
        mBinding.etMoney.requestFocus()

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
    }

    override fun initListener() {
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
            mViewModel.saveToCombo()
            dismiss()
        }
        mBinding.clBet.setOnClickListener {
            mViewModel.sendBet()
            val id = mViewModel.onBetSheetListener.value?.gameId ?: -1
            navigate(SingleBetFragmentDirections.actionSingleBetFragmentToBetResultFragment(id))
        }
        mBinding.btnReserve.setOnClickListener {
            mViewModel.onBetSheetListener.value?.let {
                childFragmentManager.setFragmentResultListener(KEY_RESULT, viewLifecycleOwner) { _, bundle ->
                    childFragmentManager.clearFragmentResultListener(KEY_RESULT)
                    if (bundle.getString(KEY_RESULT) == VALUE_RESERVE_COMPLETE) {
                        navigate(SingleBetFragmentDirections.actionSingleBetFragmentToReserveFragment(it.gameId))
                    }
                }
                val location = IntArray(2)
                mBinding.btnReserve.getLocationInWindow(location)
                ReserveDialogFragment.newInstance(
                    location.first() + mBinding.btnReserve.width / 2,
                    location.last() + mBinding.btnReserve.height,
                    it.gameId,
                    odds = it.odds.getOdds()
                ).show(childFragmentManager)
            }

        }
    }

    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            setBetData(it)
        }
        mViewModel.onBetWinMoney.observe(viewLifecycleOwner) {
            val money = getString(R.string.btn_bet_win_money).format(it)
            mBinding.tvBetMoney.text = money
        }
    }

    private fun setBetData(data: BetBean) {
        ViewHelper.bindBetSheet(data, mBinding.layoutBet)
        mBinding.layoutBet.ivDelete.visibility = View.GONE
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.singleBetFragment)
    }
}