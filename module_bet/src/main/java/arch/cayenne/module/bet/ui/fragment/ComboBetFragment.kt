package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.databinding.FragmentComboBetBinding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

/**
 * 串关投注
 */
class ComboBetFragment : BaseFragment<ComboBetViewModel, FragmentComboBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentComboBetBinding> = FragmentComboBetBinding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class

    private var isFullScreen = false

    private val betSelectionAdapter by lazy {
        BetSelectionAdapter(object : BetSelectionAdapter.OnBetSelectionClickListener {
            override fun onDeleteClick(item: BetSelectionBean) {
                mViewModel.removeSelection(item.selectionId)
            }
        })
    }

    private val comboMultiBetAdapter by lazy {
        ComboMultiBetAdapter(object : ComboMultiBetAdapter.OnComboMultiBetClickListener {
            override fun onEditMoneyClick(serialValue: Int, locationX: Int, locationY: Int) {
                mViewModel.onComboMultiBetBeanListener.value?.find { it.serialValue == serialValue }
                    ?.let {
                        childFragmentManager.setFragmentResultListener(
                            KEY_RESULT,
                            viewLifecycleOwner
                        ) { resultKey, bundle ->
                            childFragmentManager.clearFragmentResultListener(KEY_RESULT)
                            if (resultKey == KEY_RESULT) {
                                val money = bundle.getLong(VALUE_MONEY_INPUT, 0L)
                                mViewModel.updateMultiBetMoney(serialValue, money)
                            }
                        }
                        val currentMoney = it.inputMoney
                        val minAmount = it.minAmount
                        val maxAmount = it.maxAmount
                        ComboBetMoneyKeyboardDialogFragment.newInstance(
                            locationX,
                            locationY,
                            currentMoney,
                            minAmount,
                            maxAmount
                        ).show(childFragmentManager)
                    }
            }

            override fun getMoneySymbol(): String {
                return mViewModel.moneySymbol
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvMultiBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvMultiBet.itemAnimator = null

        mBinding.rvBet.adapter = betSelectionAdapter

        val decoration = BetSheetDecoration(6.dp2px, 12.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        mBinding.rvMultiBet.adapter = comboMultiBetAdapter
        setSumBetMoney(emptyList())
        setMultiLayoutHeight()
        setLayoutMaxHeight()
    }

    override fun initListener() {
        mBinding.ivClose.apply { addScaleOnTouchAnimation() }.setOnClickListener {
            dismiss()
        }
        mBinding.btnDelete.root.setOnClickListener {
            CommonDialog.newInstance(
                title = "",
                message = getString(R.string.title_dialog_remove),
                okText = getString(R.string.btn_confirm),
                cancelText = getString(R.string.btn_cancel)
            ).apply {
                setOnOkClickListener {
                    mViewModel.removeAll()
                }
            }.show(childFragmentManager)
        }
        mBinding.llMultiBetCollapse.setOnClickListener {
            mViewModel.toggleMultiLayoutExpend()
        }
        mBinding.clBet.setOnClickListener {
            if (mViewModel.getSumBetAmount() > mViewModel.balance) {
                showToast(getString(arch.cayenne.lib.common.R.string.toast_over_remaining))
            } else if (!mViewModel.checkOddsPass()) {
                mViewModel.oddsChangeListener.value?.toastRes?.let {
                    showToast(getString(it))
                }
            } else {
                val isSuccess = mViewModel.sendBet()
                if (isSuccess) {
                    navToResult()
                }
            }
        }
        mBinding.clOddsChange.setOnClickListener { v ->
            showOddsChangeDialog()
        }
    }

    override fun createObserverAtState(): Lifecycle.State = Lifecycle.State.RESUMED
    override suspend fun createObserver() {
        mViewModel.onBetListListener.observe(viewLifecycleOwner) {
            if (it.size > 1) {
                val lastSize = betSelectionAdapter.itemCount
                betSelectionAdapter.submitList(it) {
                    if (it.size > lastSize) {
                        mBinding.rvBet.scrollToPosition(0)
                    }
                }
            }
        }
        mViewModel.onComboMultiBetBeanListener.observe(viewLifecycleOwner) { data ->
            if (data.isEmpty()) return@observe
            val lastSize = comboMultiBetAdapter.itemCount
            comboMultiBetAdapter.submitList(data) {
                if (data.size > lastSize) {
                    mBinding.rvMultiBet.scrollToPosition(0)
                }
                if (data.size <= 1) {
                    mBinding.rvMultiBet.layoutParams = mBinding.rvMultiBet.layoutParams.apply {
                        this.height = getMultiItemHeight()
                    }
                    if (!isFullScreen) {
                        restoreBetLayoutPosition()
                        mBinding.rvMultiBet.post {
                            adjustLayoutHeight()
                        }
                    }
                }
            }
            setSumBetMoney(data)
        }
        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            val money = "${mViewModel.moneySymbol} ${(it?.balance?:0L).getFormalMoney()}"
            mBinding.tvBalance.text = money
        }
        mViewModel.onForceUpdateListener.observe(viewLifecycleOwner) {
            if (it && !isFullScreen) {
                forceUpdateLayout()
            }
        }
        mViewModel.onMultiLayoutExpendListener.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_collapse)
                val drawable = SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_combo_bet_ham_down)
                mBinding.ivMultiBetExpand.setImageDrawable(drawable)
            } else {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_expand)
                val drawable = SkinnableResourceManager.getDrawable(requireContext(), R.drawable.icon_combo_bet_ham)
                mBinding.ivMultiBetExpand.setImageDrawable(drawable)
            }
            if (mViewModel.onBetListListener.value != null && mViewModel.onComboMultiBetBeanListener.value != null) {
                setMultiLayoutExpandedHeight(it)
            }
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

    private fun forceUpdateLayout() {
        if (betSelectionAdapter.itemCount == 0 || mViewModel.onBetListListener.value?.size == 1) return
        adjustLayoutHeight()
    }

    private fun getScreenHeight(): Int? {
        // 检查 context 是否不为空
        return context?.resources?.displayMetrics?.heightPixels
    }

    private fun adjustLayoutHeight() {
        if (isDetached || isRemoving || !isAdded) return
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()

        val topTitleHeight =
            mBinding.clTitleBet.height + (mBinding.clTitleBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val rvMultiBetItemHeight = getMultiItemHeight() * (mViewModel.onComboMultiBetBeanListener.value?.size ?: 0).coerceAtMost(1)
        val multiBetHeight =
            mBinding.clMultiBetTitle.height + (mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin + rvMultiBetItemHeight
        val bottomButtonHeight =
            mBinding.clBottomButton.height + (mBinding.clBottomButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin


        val betSheetHeight = getBetItemHeight() * (mViewModel.onBetListListener.value?.size ?: 2).coerceAtLeast(2)

        val oddsChangeHeight = mBinding.clOddsChange.height + (mBinding.clOddsChange.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val contentHeight = betSheetHeight + topTitleHeight + multiBetHeight + bottomButtonHeight + oddsChangeHeight
        val isFull =
            contentHeight >= maxFragmentHeight

        if (isFull) {
            mBinding.root.minHeight = maxFragmentHeight
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = 0
            mBinding.rvBet.layoutParams = layoutParams
        } else {
            mBinding.root.minHeight = screenHeight / 2
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.rvBet.layoutParams = layoutParams
            }

        }
    }

    private fun setMultiLayoutHeight() {

        mBinding.rvMultiBet.layoutParams = mBinding.rvMultiBet.layoutParams.apply {
            this.height = getMultiItemHeight()
        }
    }

    private fun setLayoutMaxHeight() {
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                adjustInitLayoutHeight()
            }
        })
    }

    private fun adjustInitLayoutHeight() {
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()

        val topTitleHeight =
            mBinding.clTitleBet.measuredHeight + (mBinding.clTitleBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val rvMultiBetItemHeight = getMultiItemHeight()
        val multiBetHeight =
            mBinding.clMultiBetTitle.measuredHeight + (mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin + rvMultiBetItemHeight
        val bottomButtonHeight =
            mBinding.clBottomButton.measuredHeight + (mBinding.clBottomButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val betSheetHeight = getBetItemHeight() * 2

        val oddsChangeHeight = mBinding.clOddsChange.height + (mBinding.clOddsChange.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val contentHeight = betSheetHeight + topTitleHeight + multiBetHeight + bottomButtonHeight + oddsChangeHeight
        val isFull =
            contentHeight >= maxFragmentHeight

        if (isFull) {
            isFullScreen = true
            mBinding.root.minHeight = maxFragmentHeight
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = 0
            val clMultiBetLp = mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams

            val totalMargin =
                layoutParams.bottomMargin + mBinding.clMultiBet.height + clMultiBetLp.bottomMargin

            layoutParams.bottomToTop = mBinding.clBottomButton.id
            layoutParams.bottomMargin = totalMargin
            mBinding.rvBet.layoutParams = layoutParams
        } else {
            mBinding.root.minHeight = screenHeight / 2
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.rvBet.layoutParams = layoutParams
            }
        }
    }

    private fun setMultiLayoutExpandedHeight(isExpanded: Boolean) {
        val adapter = mBinding.rvMultiBet.adapter ?: return
        if (adapter.itemCount <= 1) {
            return
        }
        val currentHeight = mBinding.rvMultiBet.height
        mBinding.rvMultiBet.post {
            val itemHeight = if (isExpanded) getRealMultiItemHeight() else getMultiItemHeight()
            val targetHeight = itemHeight * if (isExpanded) adapter.itemCount.coerceAtMost(3) else adapter.itemCount.coerceAtMost(1)

            val animator = ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                duration = 200
                interpolator = LinearInterpolator()

                addUpdateListener {
                    val height = it.animatedValue as Int
                    mBinding.rvMultiBet.layoutParams = mBinding.rvMultiBet.layoutParams.apply {
                        this.height = height
                    }
                }

                if (isExpanded) {
                    doOnStart {
                        adjustBetLayoutPositionForExpanded()
                    }
                    doOnEnd {
                        mBinding.rvMultiBet.scrollToPosition(0)
                    }
                } else {
                    doOnEnd {
                        mBinding.rvMultiBet.scrollToPosition(0)
                        restoreBetLayoutPosition()
                    }
                }
            }

            animator.start()
        }
    }

    private fun setSumBetMoney(data: List<ComboMultiBetBean>) {
        val sumMoney = data.sumOf { it.amount }
        val money = "${mViewModel.moneySymbol}${sumMoney.getFormalMoney()}"
        mBinding.tvSumBetMoney.text = money

        val winMoney = data.sumOf { it.maxWinMoney }
        mBinding.tvBetMoneyHint.isVisible = winMoney != 0L
        mBinding.tvBetMoney.isVisible = winMoney != 0L
        val sumWinMoney = "${mViewModel.moneySymbol}${winMoney.getFormalMoney()}"
        mBinding.tvBetMoney.text = sumWinMoney
    }

    private fun getMultiItemHeight(): Int {
//        val layoutManager = mBinding.rvMultiBet.layoutManager as? LinearLayoutManager
//        val firstVisibleItemView =
//            layoutManager?.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
//        return if (firstVisibleItemView == null) {
//            90.dp2px
//        } else {
//            // 不知道為什麼高度會少bottom空白間距
//            firstVisibleItemView.height + 11.dp2px
//        }
        return 90.dp2px
    }

    private fun getRealMultiItemHeight(): Int {
        val layoutManager = mBinding.rvMultiBet.layoutManager as? LinearLayoutManager
        val firstVisibleItemView =
            layoutManager?.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
        return if (firstVisibleItemView == null) {
            getMultiItemHeight()
        } else {
            // 不知道為什麼高度會少bottom空白間距
            firstVisibleItemView.height + 12.dp2px
        }
    }

    private fun getBetItemHeight(): Int {
        val rvBetLayoutManager = mBinding.rvBet.layoutManager as? LinearLayoutManager
        val rvBetFirstVisibleItemView =
            rvBetLayoutManager?.findViewByPosition(rvBetLayoutManager.findFirstVisibleItemPosition())
        return if (rvBetFirstVisibleItemView == null) {
            140.dp2px
        } else {
            rvBetFirstVisibleItemView.height + 12.dp2px
        }
    }

    private fun adjustBetLayoutPositionForExpanded() {
        if (comboMultiBetAdapter.itemCount <= 1) return
        val rvBetLp = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
        val clMultiBetLp = mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams

        val totalMargin =
            rvBetLp.bottomMargin + mBinding.clMultiBet.height + clMultiBetLp.bottomMargin

        rvBetLp.bottomToTop = mBinding.clBottomButton.id
        rvBetLp.bottomMargin = totalMargin
        mBinding.rvBet.layoutParams = rvBetLp
    }

    private fun restoreBetLayoutPosition() {
        val rvBetLp = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
        rvBetLp.bottomToTop = mBinding.clMultiBet.id
        rvBetLp.bottomMargin = 0
        mBinding.rvBet.layoutParams = rvBetLp
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

    override fun doCustomHideEnd() {
        mViewModel.onBetListListener.value?.let {
            if (it.isNotEmpty()) {
                mViewModel.setExpandMultiLayout(false)
                mViewModel.clearBetMoney()
            }
        }

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
}