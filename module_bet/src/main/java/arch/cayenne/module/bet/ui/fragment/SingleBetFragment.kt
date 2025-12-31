package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.BuildConfig
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.data.constants.QuickAmountEnum
import arch.cayenne.lib.common.ui.adapter.QuickAmountAdapter
import arch.cayenne.lib.common.ui.fragment.ReserveDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.isGreaterThanValue
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.getMaxLength
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_TO_RESULT
import arch.cayenne.module.bet.databinding.FragmentSingleBetBinding
import arch.cayenne.module.bet.util.ViewHelper
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KClass

/**
 * 单关投注
 */
class SingleBetFragment : BaseFragment<SingleBetViewModel, FragmentSingleBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentSingleBetBinding> = FragmentSingleBetBinding::class
    override val vmClass: KClass<SingleBetViewModel> = SingleBetViewModel::class

    private val quickAmountAdapter: QuickAmountAdapter by lazy {
        QuickAmountAdapter {
            mViewModel.setNumber(it)
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        ViewUtils.hideKeyboard(requireContext(), mBinding.etMoney) {
            showKeyboard()
        }
        mBinding.clKeyboard.setRvQuickAmountAdapter(quickAmountAdapter)
        quickAmountAdapter.submitList(QuickAmountEnum.entries)
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

        mBinding.btnCollusion.setOnClickListener {
            lifecycleScope.launch {
                mViewModel.saveToCombo()
                dismiss()
            }
        }
        mBinding.clBet.setOnClickListener {
            launch { sendBet() }
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

        mBinding.clKeyboard.apply {
            mViewModel.setMaxLength(mBinding.etMoney.getMaxLength())
            setOnBackListener(onClick = {
                mViewModel.backNumber()
            }, onLongPressRepeat = {
                mViewModel.backNumber()
            })

            setOnClearListener {
                mViewModel.clearNumber()
            }
            setOnHideKeyboardListener {
                mBinding.clMoney.isFocusableInTouchMode = false
                mBinding.clMoney.isFocusable = false
            }
            setOnShowKeyboardListener {
                mBinding.clMoney.isFocusableInTouchMode = true
                mBinding.clMoney.isFocusable = true
            }
            setOnCalculatorClickListener(object :NumberKeyboardView.OnCalculatorClickListener {
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

        mBinding.clMoney.setOnClickListener {
            showKeyboard()
        }
        mBinding.btnDelete.root.setOnClickListener {
            mViewModel.removeBet()
        }
        mBinding.clOddsChange.setOnClickListener {
            showOddsChangeDialog()
        }

        //Todo: for qaTest only
        if(BuildConfig.BUILD_TYPE == "qatest") {
            mBinding.clTitleBet.setOnLongClickListener{
                launch { mViewModel.mockBetData() }
                true
            }
        }
    }

    override fun createObserverAtState(): Lifecycle.State = Lifecycle.State.RESUMED
    override suspend fun createObserver() {
        mViewModel.sendBetting.observe(viewLifecycleOwner) {
            mBinding.groupBetText.isVisible = !it
            mBinding.progressBar.isVisible = it
        }
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
            mBinding.tvBetMoney.isVisible = (it.isNotEmpty() && it != "0") && !mViewModel.sendBetting.value!!
            if(mBinding.tvBetMoney.isVisible){
                val result = String.format(Locale.ROOT,"%.2f", it.toFloat())
                val money = getString(R.string.btn_bet_win_money).format(mViewModel.moneySymbol, result)
                mBinding.tvBetMoney.setCurrencyText(money,mViewModel.moneySymbol.let { it to CurrencySymbols.getSymbolIcon(it) })
            }
        }
        mViewModel.onNumberLimit.observe(viewLifecycleOwner) {
            mBinding.etMoney.hintCursor =
                getString(R.string.et_money_hint).format(it.first.getMoney(), it.second.getMoney())
        }

        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            if (it != null) {
                val money = "${mViewModel.moneySymbol} ${it.balance.getFormalMoney(false)}"
                mBinding.tvBalance.setCurrencyText(money,mViewModel.moneySymbol.let { it to CurrencySymbols.getSymbolIcon(it) },-3)
                mBinding.tvMoney.setCurrencyText(mViewModel.moneySymbol,mViewModel.moneySymbol.let { it to CurrencySymbols.getSymbolIcon(it) },-4)
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
        mViewModel.networkConnectedEvent.observeEvent(viewLifecycleOwner, this) {
            if (it is DataState.NetworkUnavailable) {
                showToast(getString(arch.cayenne.lib.common.R.string.toast_server_disconnected))
            }
        }
        mViewModel.oddsChangeListener.observe(viewLifecycleOwner) {
            mBinding.tvOddsChange.text = SkinnableResourceManager.getString(requireContext(), it.textRes)
        }
    }
    /**
     * 设置带有货币图标的文本
     *
     * @param text
     * @param replacePair
     */
    private fun TextView.setCurrencyText(
        text: String,
        replacePair: Pair<String, Int?>,
        offset: Int = 0
    ) {
        val ss = SpannableString(text)
        val (str, icon)  = replacePair
        val start = text.indexOf(str)
        if (icon != null && start >= 0) {
            val drawable = ContextCompat.getDrawable(context, icon)!!
            val size = (textSize * 1.1f).toInt()
            drawable.setBounds(0, 0, size, size)

            ss.setSpan(object : ImageSpan(drawable, ALIGN_BASELINE) {
                override fun draw(
                    canvas: Canvas,
                    text: CharSequence,
                    start: Int,
                    end: Int,
                    x: Float,
                    top: Int,
                    y: Int,
                    bottom: Int,
                    paint: Paint
                ) {
                    val fm = paint.fontMetricsInt
                    val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.height() / 2 + offset
                    canvas.save()
                    canvas.translate(x, transY.toFloat())
                    drawable.draw(canvas)
                    canvas.restore()
                }
            },
                start,
                start + str.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        setText(ss)
    }
    private fun setBetTypeLayout(type: BetTypeEnum?) {
        when (type) {
            BetTypeEnum.SINGLE, BetTypeEnum.RESERVE -> {
                mBinding.btnCollusion.visibility = View.VISIBLE
                mBinding.btnDelete.root.visibility = View.INVISIBLE
                mBinding.ivClose.setImageDrawable(SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_page_close))
            }

            BetTypeEnum.COMBO -> {
                mBinding.btnCollusion.visibility = View.INVISIBLE
                mBinding.btnDelete.root.visibility = View.VISIBLE
                mBinding.ivClose.setImageDrawable(SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_collapse))
            }

            else -> {}
        }
    }

    private fun setBetData(data: BetSelectionBean) {
        ViewHelper.bindBetSheet(1, data, mBinding.layoutBet)
        mBinding.btnCollusion.isEnabled = data.isParlay
        mBinding.tvCollusionHint.alpha = if (data.isParlay) 1.0f else 0.3f
        mBinding.ivCollusionHint.alpha = if (data.isParlay) 1.0f else 0.3f
        /*mBinding.clBet.isEnabled = data.isActive
        mBinding.tvBetHint.alpha = if (data.isActive) 1.0f else 0.3f
        mBinding.tvBetMoney.alpha = if (data.isActive) 0.7f else 0.1f*/
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

    override fun navToResult(vararg others: Pair<String, Any?>) {
        parentFragmentManager.setFragmentResult(KEY_RESULT, bundleOf(KEY_RESULT to VALUE_TO_RESULT,*others))
    }

    override fun doCustomShow() {
        mBinding.root.post {
            mBinding.etMoney.isCursorVisible = true
            mBinding.etMoney.isFocusableInTouchMode = true
            mBinding.etMoney.isFocusable = true
            mBinding.etMoney.requestFocus()
        }
    }

    override fun doCustomHideEnd() {
        mViewModel.removeReserve()
        mViewModel.clearNumber()
        showKeyboard()
    }

    private fun showKeyboard() {
        mBinding.clKeyboard.showKeyBoard()
    }

    private suspend fun sendBet() {
        mViewModel.onBetSheetListener.value?.let {
            if (!it.isActive) {
                showToast(getString(R.string.hint_bet_inactive))
                return
            }
        }
        val curAmount = mViewModel.editValue
        if(curAmount.isBlank()) {
            showToast(getString(R.string.hint_empty_amount))
            return
        }
        if (curAmount.isGreaterThanValue(mViewModel.maxMoney.getMoney())) {
            showToast(getString(arch.cayenne.lib.common.R.string.toast_over_max))
            return
        }
        val amount = curAmount.toMoney()
        val minNumber = mViewModel.minMoney
        val balance = mViewModel.balance
        if (minNumber > amount) {
            showToast(getString(R.string.hint_less_min_amount))
        } else if (amount > balance) {
            showToast(getString(arch.cayenne.lib.common.R.string.toast_over_remaining))
        } else if (!mViewModel.checkOddsPass()) {
            mViewModel.oddsChangeListener.value?.toastRes?.let {
                showToast(getString(it))
            }
        } else {
            val isSuccess = mViewModel.sendBet()
            if (isSuccess.isFailure) {
                showToast(R.string.toast_bet_failure.getString())
                return
            }
            navToResult()
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
            (location.last() - 17f.dp2px).toInt(),
            mBinding.btnReserve.height,
            odds = odds
        ).show(childFragmentManager)
    }

    private fun showOddsChangeDialog() {
        val location = IntArray(2)
        mBinding.clOddsChange.getLocationInWindow(location)

        val x = location.first()
        val y = location.last() - ViewUtils.getStatusBarHeight(requireContext())
        val width = mBinding.clOddsChange.width
        val height = mBinding.clOddsChange.height
        val rect = Rect(x, y, x + width, y + height)
        val f = OddsChangeDialogFragment.instance(rect)

        val animator = ObjectAnimator.ofFloat(mBinding.ivOddsChange, "rotation", 0f, 180f)
        animator.duration = 100 // 旋轉持續時間，單位毫秒
        animator.interpolator = LinearInterpolator() // 線性插值器，讓旋轉更平滑
        f.setOnDismissListener {
            animator.reverse()
        }
        f.show(childFragmentManager)
        animator.start()
    }

    override fun getBlockingSlideView(): View {
        return mBinding.clKeyboard
    }
}