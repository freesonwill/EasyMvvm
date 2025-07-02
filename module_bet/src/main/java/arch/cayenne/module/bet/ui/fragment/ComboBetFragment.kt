package arch.cayenne.module.bet.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.Config
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.databinding.FragmentComboBetBinding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

class ComboBetFragment : BaseFragment<ComboBetViewModel, FragmentComboBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentComboBetBinding> = FragmentComboBetBinding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class

    private val betSelectionAdapter by lazy {
        BetSelectionAdapter(object : BetSelectionAdapter.OnBetSelectionClickListener {
            override fun onDeleteClick(item: BetSelectionBean) {
//                CommonDialog.newInstance(
//                    title = "",
//                    message = getString(R.string.title_dialog_remove),
//                    okText = getString(R.string.btn_confirm),
//                    cancelText = getString(R.string.btn_cancel)
//                ).apply {
//                    setOnOkClickListener {
//                        mViewModel.removeSelection(item.selectionId)
//                    }
//                }.show(childFragmentManager)
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
                        val currentMoney = if (it.inputMoney == 0L) null else it.inputMoney
                        val minAmount = it.minAmount
                        val maxAmount = it.maxAmount
                        val remainingMoney = mViewModel.remainingBalance / it.count
                        ComboBetMoneyKeyboardDialogFragment.newInstance(
                            locationX,
                            locationY,
                            currentMoney,
                            minAmount,
                            maxAmount,
                            remainingMoney
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
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvBet.itemAnimator = null
        mBinding.rvMultiBet.itemAnimator = null

        mBinding.rvBet.adapter = betSelectionAdapter

        val decoration = BetSheetDecoration(6.dp2px, 12.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        mBinding.rvMultiBet.adapter = comboMultiBetAdapter
        mBinding.rvMultiBet.isNestedScrollingEnabled = false
        setSumBetMoney(emptyList())
        setMultiLayoutMaxHeight()
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnDelete.setOnClickListener {
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
            mViewModel.onBetListListener.removeObservers(viewLifecycleOwner)
            mViewModel.sendBet()
            navigate(ComboBetFragmentDirections.actionComboBetFragmentToBetResultFragment(), null)
        }
    }

    override fun createObserver() {
        mViewModel.onBetListListener.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                dismiss()
            } else if (it.size == 1) {
                navigate(
                    ComboBetFragmentDirections.actionComboBetFragmentToSingleBetFragment().apply {
                        this.arguments.putString(Config.KEY_NON_ANIM, "")
                    },
                    null
                )
            } else {
                betSelectionAdapter.submitList(it)
            }
        }
        mViewModel.onComboMultiBetBeanListener.observe(viewLifecycleOwner) { data ->
            val lastSize = comboMultiBetAdapter.itemCount
            comboMultiBetAdapter.submitList(data) {
                if (lastSize == 0 && data.isNotEmpty()) {
                    setMultiLayoutExpandedHeight(false)
                } else if (data.size <= 1) {
                    mBinding.rvMultiBet.layoutParams = mBinding.rvMultiBet.layoutParams.apply {
                        height = getMultiItemHeight()
                        restoreBetLayoutPosition()
                    }
                }
            }
            setSumBetMoney(data)
        }
        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            val money = "${mViewModel.moneySymbol} ${it.balance.getFormalMoney()}"
            mBinding.tvBalance.text = money
        }
        mViewModel.onCanBetListener.observe(viewLifecycleOwner) {
            mBinding.clBet.isEnabled = it
        }
        mViewModel.onForceUpdateListener.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        forceUpdateLayout()
                    }
                })
            }
        }
        mViewModel.onMultiLayoutExpendListener.observe(viewLifecycleOwner) {
            if (it) {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_collapse)
            } else {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_expand)
            }
            if (mViewModel.onBetListListener.value != null && mViewModel.onComboMultiBetBeanListener.value != null) {
                setMultiLayoutExpandedHeight(it)
            }
        }
    }

    private fun forceUpdateLayout() {
        if (betSelectionAdapter.itemCount == 0 || mViewModel.onBetListListener.value?.size == 1) return
        adjustLayoutHeight()
    }

    private fun adjustLayoutHeight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val maxFragmentHeight = (screenHeight * 0.75).toInt()

        val topTitleHeight =
            mBinding.clTitleBet.height + (mBinding.clTitleBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin

        val rvMultiBetItemHeight = getMultiItemHeight() * (mViewModel.onComboMultiBetBeanListener.value?.size ?: 0).coerceAtMost(1)
        val multiBetHeight =
            mBinding.clMultiBetTitle.height + (mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin + rvMultiBetItemHeight
        val bottomButtonHeight =
            mBinding.clBottomButton.height + (mBinding.clBottomButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin


        val betSheetHeight = getBetItemHeight() * (mViewModel.onBetListListener.value?.size ?: 1)

        val isFull =
            betSheetHeight + topTitleHeight + multiBetHeight + bottomButtonHeight >= maxFragmentHeight

        if (isFull) {
            mBinding.root.minHeight = maxFragmentHeight
        } else {
            mBinding.root.minHeight = 0
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.rvBet.layoutParams = layoutParams
            }

        }
    }

    // 獲取除串關方式外已使用的高度
    private fun getMultiLayoutMargin(): Int {
        val multiBottomMargin =
            (mBinding.clMultiBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val buttonBottomMargin =
            (mBinding.clBottomButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val buttonHeight = mBinding.clBottomButton.height
        val underMargin = multiBottomMargin + buttonBottomMargin + buttonHeight
        val topMargin = 22.dp2px
        return topMargin + underMargin
    }

    private fun setMultiLayoutMaxHeight() {
        val screenHeight = resources.displayMetrics.heightPixels
        val maxFragmentHeight = (screenHeight * 0.75).toInt()

        val totalMargin = getMultiLayoutMargin()

        val maxHeight = maxFragmentHeight - totalMargin
        mBinding.clMultiBet.maxHeight = maxHeight
    }

    private fun setMultiLayoutExpandedHeight(isExpanded: Boolean) {
        val adapter = mBinding.rvMultiBet.adapter ?: return
        if (adapter.itemCount <= 1) {
            return
        }
        val currentHeight = mBinding.rvMultiBet.height
        mBinding.rvMultiBet.post {
            val itemHeight = getMultiItemHeight()
            val targetHeight = itemHeight * if (isExpanded) adapter.itemCount.coerceAtMost(3) else 1

            val animator = ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                duration = 300
                interpolator = DecelerateInterpolator()

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
        val money = "${mViewModel.moneySymbol}${sumMoney.getMoney()}"
        mBinding.tvSumBetMoney.text = money

        val winMoney = data.sumOf { it.maxWinMoney }
        val sumWinMoney =
            getString(R.string.btn_bet_win_money).format(
                mViewModel.moneySymbol,
                winMoney.getMoney()
            )
        mBinding.tvBetMoney.text = sumWinMoney
    }

    private fun getMultiItemHeight(): Int {
        if (comboMultiBetAdapter.itemCount == 0) return 0
        val layoutManager = mBinding.rvMultiBet.layoutManager as? LinearLayoutManager
        val firstVisibleItemView =
            layoutManager?.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
        return if (firstVisibleItemView == null) {
            90.dp2px
        } else {
            // 不知道為什麼高度會少bottom(8dp)空白間距
            firstVisibleItemView.height + 8.dp2px
        }
    }

    private fun getBetItemHeight(): Int {
        val rvBetLayoutManager = mBinding.rvBet.layoutManager as? LinearLayoutManager
        val rvBetFirstVisibleItemView =
            rvBetLayoutManager?.findViewByPosition(rvBetLayoutManager.findFirstVisibleItemPosition())
        return rvBetFirstVisibleItemView?.height ?: 128.dp2px
    }

    private fun adjustBetLayoutPositionForExpanded() {
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
        mViewModel.saveInputMoney()
        sendResult(key, value, R.id.comboBetFragment)
    }

}