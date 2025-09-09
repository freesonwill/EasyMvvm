package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.animation.IInterpolatorOption
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.fragment.ReserveDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentSingleBetBinding
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import kotlinx.coroutines.launch
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
        setMaxHeight()
    }

    private fun setMaxHeight() {
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val screenHeight = getScreenHeight() ?: return
                val maxHeight = (screenHeight * 0.75f).toInt()
                val actualHeight = mBinding.main.height
                val scale = maxHeight.toFloat() / actualHeight.toFloat()
                if (actualHeight > maxHeight) {
                    mBinding.main.pivotY = actualHeight.toFloat()
                    mBinding.main.scaleY = scale
                    mBinding.root.maxHeight = maxHeight
                }
            }
        })
    }

    private fun getScreenHeight(): Int? {
        // 检查 context 是否不为空
        return context?.resources?.displayMetrics?.heightPixels
    }

    override fun initListener() {
        mBinding.ivClose.apply { addScaleOnTouchAnimation() }.setOnClickListener {
            val type = mViewModel.betTypeListener.value
            if (type == BetTypeEnum.COMBO) {
                dismiss()
            } else {
                mViewModel.removeBet()
            }
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
            lifecycleScope.launch {
                mViewModel.saveToCombo()
                dismiss()
            }
        }
        mBinding.clBet.setOnClickListener {
            sendBet()
        }
        mBinding.btnReserve.setOnClickListener {
            mViewModel.onBetSheetListener.value?.let {
                showReserveOddsDialog(it.odds.getDisplayOdds().toOdds())
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
        }
        mBinding.tvOddsChange.setOnClickListener { v ->
            showOddsChangeDialog()
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
            setBetButtonByOdds(mViewModel.onReserveOddsListener.value, it.odds)
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
            if (it != null) {
                val money = "${mViewModel.moneySymbol} ${it.balance.getFormalMoney()}"
                mBinding.tvBalance.text = money
                mBinding.tvMoney.text = mViewModel.moneySymbol
            }
        }

        mViewModel.onReserveOddsListener.observe(viewLifecycleOwner) { odds ->
            val currentOdds = mViewModel.onBetSheetListener.value?.odds ?: 0
            mBinding.btnReserve.isVisible = odds == null
            mBinding.clCancelReserve.isVisible = odds != null
            if (odds != null) {
                val value = "@${odds.getOdds()}"
                mBinding.tvCancelReserve.text = value
            }
            setBetButtonByOdds(odds, currentOdds)
        }
        mViewModel.betTypeListener.observe(viewLifecycleOwner) { type ->
            setBetTypeLayout(type)
        }
        mViewModel.onCanBetListener.observe(viewLifecycleOwner) {
            mBinding.clBet.isEnabled = it
        }
        mViewModel.networkConnectedEvent.observeEvent(viewLifecycleOwner, this) {
            if (it is DataState.NetworkUnavailable) {
                showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
            }
        }
        mViewModel.oddsChangeListener.observe(viewLifecycleOwner) {
            mBinding.tvOddsChange.text = SkinnableResourceManager.getString(requireContext(), it.textRes)
        }
    }

    private fun setBetTypeLayout(type: BetTypeEnum?) {
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

    private fun setBetData(data: BetSelectionBean) {
        ViewHelper.bindBetSheet(1, data, mBinding.layoutBet)
        mBinding.btnCollusion.isEnabled = data.isParlay
        mBinding.clBet.isEnabled = data.isActive
        mBinding.layoutBet.ivDelete.isVisible = false
    }

    private fun setBetButtonByOdds(reserveOdds: Int?, currentOdds: Int) {
        if (reserveOdds == null || reserveOdds == currentOdds) {
            mBinding.tvBetHint.text = getString(R.string.btn_bet_hint)
        } else {
            mBinding.tvBetHint.text = getString(R.string.title_reserve)
        }
    }

    override fun dismiss(key: String, value: String) {
        parentFragmentManager.setFragmentResult(key, Bundle().apply {
            putString(key, value)
        })
    }

    override fun navToResult(key: String, value: String) {
        parentFragmentManager.setFragmentResult(key, Bundle().apply {
            putString(key, value)
        })
    }

    override fun doCustomShow() {
        mBinding.etMoney.requestFocus()
    }

    override fun doCustomHideEnd() {
        mViewModel.removeReserve()
        mViewModel.clearNumber()
        showKeyboard()
    }

    private fun hideKeyboard() {
        ViewUtils.collapseView(mBinding.clKeyboard, mBinding.ivFakerView)
        mBinding.clMoney.isFocusableInTouchMode = false
        mBinding.clMoney.isFocusable = false
    }

    private fun showKeyboard() {
        if (!mBinding.clKeyboard.isVisible) {
            ViewUtils.expandView(mBinding.clKeyboard, mBinding.ivFakerView)
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
                navToResult()
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
            location.last() - ViewUtils.getStatusBarHeight(requireContext()),
            mBinding.btnReserve.height,
            odds = odds
        ).show(childFragmentManager)
    }

    private fun showOddsChangeDialog() {
        val globalRect = Rect()
        mBinding.clOddsChange.getGlobalVisibleRect(globalRect)
        val f = OddsChangeDialogFragment.instance(globalRect)

        val animator = ObjectAnimator.ofFloat(mBinding.ivOddsChange, "rotation", 0f, 180f)
        animator.duration = 200 // 旋轉持續時間，單位毫秒
        animator.interpolator = LinearInterpolator() // 線性插值器，讓旋轉更平滑
        f.setOnDismissListener {
            animator.reverse()
        }
        f.show(childFragmentManager)
        animator.start()
    }
}