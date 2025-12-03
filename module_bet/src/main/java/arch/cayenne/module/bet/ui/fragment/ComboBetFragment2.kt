package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.Parameter
import arch.cayenne.module.bet.databinding.FragmentComboBet2Binding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
import arch.cayenne.module.bet.ui.custom.BetMoneyKeyboard
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

/**
 * 串关投注
 */
class ComboBetFragment2 : BaseFragment<ComboBetViewModel, FragmentComboBet2Binding>(),
    BetSheetListener {
    override val vbClass: KClass<FragmentComboBet2Binding> = FragmentComboBet2Binding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class
    private var isFullScreen = false

    private val betSelectionAdapter by lazy {
        BetSelectionAdapter(object : BetSelectionAdapter.OnBetSelectionClickListener {
            override fun onDeleteClick(item: BetSelectionBean) {
                mViewModel.removeSelection(item.selectionId)
            }
        })
    }

    private val keyboard:BetMoneyKeyboard by lazy {
        BetMoneyKeyboard(requireContext()).apply { id = R.id.main }
    }


    private val comboMultiBetAdapter by lazy {
        ComboMultiBetAdapter(object : ComboMultiBetAdapter.OnComboMultiBetClickListener {
            override fun onEditMoneyClick2(serialValue: Int, editText: EditText, tvMoney: TextView, addViewAction:(keyboard:BetMoneyKeyboard)->Unit) {
                "aaaa---serialValue:$serialValue,keyboard.serialValue:${keyboard.serialValue}".logd(TAG)
                if(keyboard.serialValue == serialValue) return
                val bean = mViewModel.onComboMultiBetBeanListener.value?.find { it.serialValue == serialValue }
                if(bean != null) {
                    (keyboard.parent as? ViewGroup)?.removeView(keyboard)
                    keyboard.visibility = View.GONE
                    val currentMoney = bean.inputMoney
                    val minAmount = bean.minAmount
                    val maxAmount = bean.maxAmount
                    keyboard.bind(viewLifecycleOwner,serialValue, editText, tvMoney, true, currentMoney, minAmount, maxAmount,
                        onMoneyChange = { serialV,money->
                            mViewModel.updateMultiBetMoney(serialV, money)
                        }
                    )
                    addViewAction(keyboard)
                    keyboard.visibility = View.VISIBLE
                    val duration = 150L
                    keyboard.showKeyBoard(duration, onEnd = {
                        val targetScrollY = calculateScrollY(mBinding.nsBet, keyboard)
                        if(targetScrollY != null){
                            val speed = 1f* keyboard.height / duration //保存速度一致
                            val d = (targetScrollY/speed).toInt()
                            //"speed---$speed--duration:$duration".logd(TAG)
                            mBinding.nsBet.smoothScrollTo(0, targetScrollY, d)
                        }
                    })
                } else {
                    "cannot find $serialValue in onComboMultiBetBeanListener:${mViewModel.onComboMultiBetBeanListener.value}"
                        .also { showToast(it) }
                        .loge(TAG)
                }
            }

            override fun getMoneySymbol(): String {
                return mViewModel.moneySymbol
            }

            override fun onCombinationDetailClick(serialValue: Int) {
                val data = mViewModel.onComboMultiBetBeanListener.value?.find { it.serialValue == serialValue }
                    ?: error("can not find serialValue:$serialValue in ${ mViewModel.onComboMultiBetBeanListener.value }")
                val items = mViewModel.splitComboIntoSingles(data)
                CombinationFragment.newInstance(
                    Parameter(
                    title = data.title(),
                    titleTips = data.titleTips(),
                    items = items
                )
                ).show(childFragmentManager)
            }

            fun calculateScrollY(nestedScrollView: NestedScrollView, targetView: View):Int? {
                // 计算 targetView 相对于 NestedScrollView 的 top
                var top = 0
                var v: View? = targetView
                while (v != null && v != nestedScrollView) {
                    top += v.top
                    val p = v.parent
                    v = if (p is View) p else null
                }
                val targetHeight = targetView.run {
                    measure(0,0)
                    measuredHeight
                }
                val bottom = top + targetHeight
                val scrollY = nestedScrollView.scrollY
                val visibleTop = scrollY
                val visibleBottom = nestedScrollView.height + scrollY
                // 目标滚动位置 = targetView.bottom - NestedScrollView 可视高度
                val targetScrollY = bottom - nestedScrollView.height
                //"aaaa---targetHeight:$targetHeight,bottom:$bottom,visibleBottom:$visibleBottom,targetScrollY:$targetScrollY,scrollY:$scrollY".logd(TAG)
                if(bottom > visibleBottom){
                    return targetScrollY
                } else {
                    return null
                }
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvMultiBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvMultiBet.itemAnimator = null

        mBinding.rvBet.adapter = betSelectionAdapter

        val decoration = BetSheetDecoration(0.dp2px, 0.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        mBinding.rvMultiBet.adapter = comboMultiBetAdapter
        setSumBetMoney(emptyList())
        mBinding.firstMultiItem.apply {
            etMoney.isFocusable = false //不弹出系统软键盘
        }
    }

    override fun initListener() {
        mBinding.firstMultiItem.apply {
            etMoney.setOnClickListener {
                val item = mViewModel.firstComboMultiBetBeanLD.value ?: return@setOnClickListener
                comboMultiBetAdapter.onComboMultiBetClickListener.onEditMoneyClick2(item.serialValue,this.etMoney,this.tvMoney, addViewAction = { keyboard ->
                    val lp = ConstraintLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                        topToBottom = edgeBottom.id
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    this@apply.root.addView(keyboard, lp)
                })
            }
        }
        mBinding.ivClose.apply { addScaleOnTouchAnimation() }.setOnClickListener {
            dismiss()
        }
        mBinding.btnDelete.root.setOnClickListener {
            ExitComboDialogFragment().apply {
                setOnOkClickListener {
                    mViewModel.exitComboBet()
                }
            }.show(childFragmentManager)
        }
        mBinding.clMultiBetTitle.setOnClickListener {
            if(comboMultiBetAdapter.itemCount == 0){
                showToast(R.string.toast_no_more_bet_combs.getString())
                return@setOnClickListener
            }
            mViewModel.toggleMultiLayoutExpend()
        }
        mBinding.clBet.setOnClickListener {
            mViewModel.checkAmountLimit()?.let {
                showToast(it)
                return@setOnClickListener
            }
            if (mViewModel.getSumBetAmount() > mViewModel.balance) {
                showToast(arch.cayenne.lib.common.R.string.toast_over_remaining.getString())
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
                    if (it.size > lastSize) { //扩充了，滚动0
                        mBinding.rvBet.scrollToPosition(0)
                    }
                }
            }
        }
        mViewModel.firstComboMultiBetBeanLD.observe(viewLifecycleOwner) { item->
            with(mBinding.firstMultiItem) {
                val moneySymbol = mViewModel.moneySymbol
                tvTitleCombo.text = let {
                    val combo = R.string.title_combo_bet_odds.getString().format(item.comboK, item.comboV)
                    combo
                }
                tvMulti.text = let { "@${item.sumOdds.getOdds()}" }
                val moneyHint = R.string.et_money_hint.getString().format(item.minAmount.getMoney(), item.maxAmount.getMoney())
                etMoney.hint = moneyHint
                tvMoney.text = moneySymbol
            }
        }
        mViewModel.remainingComboMultiBetBeansLD.observe(viewLifecycleOwner) { data ->
            comboMultiBetAdapter.submitList(data)
        }
        mViewModel.onComboMultiBetBeanListener.observe(viewLifecycleOwner){ data->
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
        mViewModel.onMultiLayoutExpendListener.observe(viewLifecycleOwner,object :Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    mBinding.tvMultiBetExpand.rotation = 180f
                    mBinding.rvMultiBet.isVisible = true
                    scroll2MoreCombBottom(mBinding.rvMultiBet,mBinding.nsBet)
                } else {
                    mBinding.tvMultiBetExpand.rotation = 0f
                    mBinding.rvMultiBet.isVisible = false
                }
            }

            //滚到到更多组合投注底部
            private fun scroll2MoreCombBottom(recyclerView: RecyclerView, scrollView:NestedScrollView){
                if(recyclerView.adapter!!.itemCount == 0) return
                val viewTreeObserver = scrollView.viewTreeObserver
                viewTreeObserver.addOnGlobalLayoutListener(object:OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val scrollY = scrollView[0].bottom
                        scrollView.smoothScrollTo(0, scrollY)
                    }
                })
            }
        })
        mViewModel.networkConnectedEvent.observeEvent(viewLifecycleOwner, this) {
            if (it is DataState.NetworkUnavailable) {
                showToast(arch.cayenne.lib.common.R.string.toast_server_disconnected.getString())
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

    private fun adjustLayoutHeight() {
        if (isDetached || isRemoving || !isAdded) return
    }

    private fun setSumBetMoney(data: List<ComboMultiBetBean>) {
        val sumMoney = data.sumOf { it.amount }
        val money = "${mViewModel.moneySymbol}${sumMoney.getFormalMoney()}"

        val winMoney = data.sumOf { it.maxWinMoney }
        val onlyBetOnMain = data.drop(1).all { it.inputMoney == 0L } //仅主投注的才显示预计投注

        mBinding.tvBetMoneyHint.isVisible = (winMoney != 0L) && onlyBetOnMain
        mBinding.tvBetMoney.isVisible = (winMoney != 0L) && onlyBetOnMain
        val sumWinMoney = "${mViewModel.moneySymbol}${winMoney.getFormalMoney()}"
        mBinding.tvBetMoney.text = sumWinMoney
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

    /**
     * 配料
     */
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