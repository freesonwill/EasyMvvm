package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.fragment.ReserveDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config
import arch.cayenne.module.bet.databinding.FragmentSingleBetBinding
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import kotlin.reflect.KClass

class SingleBetFragment : BaseFragment<SingleBetViewModel, FragmentSingleBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentSingleBetBinding> = FragmentSingleBetBinding::class
    override val vmClass: KClass<SingleBetViewModel> = SingleBetViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney) {
            showKeyboard()
        }
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
        mBinding.ivClose.apply { addScaleOnTouchAnimation() }.setOnClickListener {
            val type = mViewModel.betTypeListener.value
            if (type != BetTypeEnum.COMBO) {
                mViewModel.removeBet()
            }
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
            sendBet()
        }
        mBinding.btnReserve.setOnClickListener {
            mViewModel.onBetSheetListener.value?.let {
                showReserveOddsDialog(it.odds)
            }
        }
        mBinding.tvCancelReserve.setOnClickListener {
            mViewModel.onReserveOddsListener.value?.let { odds ->
                showReserveOddsDialog(odds)
            }
        }
        mBinding.ivCancelReserve.setOnClickListener {
            mViewModel.removeReserve()
        }
        mBinding.btnCollapse.setOnClickListener {
            hideKeyboard()
        }
        mBinding.clMoney.setOnClickListener {
            showKeyboard()
        }
        mBinding.btnDelete.setOnClickListener {
            mViewModel.removeBet()
            dismiss()
        }
    }

    override fun createObserverAtState(): Lifecycle.State = Lifecycle.State.RESUMED
    override suspend fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
        }
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            setBetData(it)
        }
        mViewModel.onBetWinMoney.observe(viewLifecycleOwner) {
            mBinding.tvBetMoney.isVisible = it.isNotEmpty() && it != "0"
            val money = getString(R.string.btn_bet_win_money).format(mViewModel.moneySymbol, it)
            mBinding.tvBetMoney.text = money
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            mBinding.etMoney.hint =
                getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) {
            it.msg?.let { msg ->
                showToast(msg)
            }
        }
        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            val money = "${mViewModel.moneySymbol} ${it.balance.getFormalMoney()}"
            mBinding.tvBalance.text = money
            mBinding.tvMoney.text = mViewModel.moneySymbol
        }

        mViewModel.onReserveOddsListener.observe(viewLifecycleOwner) { odds ->
            mBinding.btnReserve.isVisible = odds == null
            mBinding.clCancelReserve.isVisible = odds != null
            if (odds == null) {
                mBinding.tvBetHint.text = getString(R.string.btn_bet_hint)
            } else {
                mBinding.tvBetHint.text = getString(R.string.title_reserve)
                val value = "@${odds.getOdds()}"
                mBinding.tvCancelReserve.text = value
            }
        }
        mViewModel.betTypeListener.observe(viewLifecycleOwner) { type ->
            when (type) {
                BetTypeEnum.SINGLE, BetTypeEnum.RESERVE -> {
                    mBinding.btnCollusion.visibility = View.VISIBLE
                    mBinding.btnDelete.visibility = View.INVISIBLE
                    mBinding.ivClose.setImageDrawable(SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_page_close))
                }

                BetTypeEnum.COMBO -> {
                    mBinding.btnCollusion.visibility = View.INVISIBLE
                    mBinding.btnDelete.visibility = View.VISIBLE
                    mBinding.ivClose.setImageDrawable(SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_collapse))
                }

                else -> {}
            }
        }
        mViewModel.onCanBetListener.observe(viewLifecycleOwner) {
            mBinding.clBet.isEnabled = it
        }
        mViewModel.networkConnectedEvent.observeEvent(viewLifecycleOwner, this) {
            if (it is DataState.NetworkUnavailable) {
                showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
            }
        }
    }

    private fun setBetData(data: BetSelectionBean) {
        ViewHelper.bindBetSheet(data, mBinding.layoutBet)
        mBinding.btnCollusion.isEnabled = data.isParlay
        mBinding.clBet.isEnabled = data.isActive
        mBinding.layoutBet.ivDelete.isVisible = false
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.singleBetFragment)
    }

    override fun showExitAnim(key: String, value: String) {
        sendResult(key, value, R.id.singleBetFragment)
    }

    override fun doCustomHideEnd() {
        mViewModel.removeReserve()
        mViewModel.clearNumber()
    }

    private fun hideKeyboard() {
        ViewUtils.collapseView(mBinding.clKeyboard, mBinding.ivFakerView)
        mBinding.etMoney.clearFocus()
        mBinding.clMoney.isFocusableInTouchMode = false
        mBinding.clMoney.isFocusable = false
    }

    private fun showKeyboard() {
        if (!mBinding.clKeyboard.isVisible) {
            ViewUtils.expandView(mBinding.clKeyboard, mBinding.ivFakerView)
            mBinding.etMoney.requestFocus()
            mBinding.clMoney.isFocusableInTouchMode = true
            mBinding.clMoney.isFocusable = true
        }
    }

    private fun sendBet() {
        val minAmount = mViewModel.minMoney
        val curAmount = mViewModel.editValue.toMoney()
        if (curAmount < minAmount) {
            showToast(getString(R.string.hint_less_min_amount))
        } else {
            val isSuccess = mViewModel.sendBet()
            if (isSuccess) {
                mViewModel.onBetSheetListener.removeObservers(viewLifecycleOwner)
                showExitAnim(value = Config.VALUE_SINGLE_TO_RESULT)
            }
        }
    }

    private fun showReserveOddsDialog(odds: Int) {
        childFragmentManager.clearFragmentResultListener(ReserveDialogFragment.KEY_RESULT)
        childFragmentManager.setFragmentResultListener(
            ReserveDialogFragment.KEY_RESULT,
            viewLifecycleOwner
        ) { _, bundle ->
            childFragmentManager.clearFragmentResultListener(ReserveDialogFragment.KEY_RESULT)
            if (bundle.getString(ReserveDialogFragment.KEY_RESULT) == ReserveDialogFragment.VALUE_RESERVE_COMPLETE) {
                val newOdds = bundle.getInt(ReserveDialogFragment.KEY_ODDS_RESULT)
                mViewModel.saveToReserve(newOdds)
            }
        }
        val location = IntArray(2)
        mBinding.btnReserve.getLocationInWindow(location)
        ReserveDialogFragment.newInstance(
            location.first() + mBinding.btnReserve.width / 2,
            location.last(),
            mBinding.btnReserve.height,
            odds = odds
        ).show(childFragmentManager)
    }
}