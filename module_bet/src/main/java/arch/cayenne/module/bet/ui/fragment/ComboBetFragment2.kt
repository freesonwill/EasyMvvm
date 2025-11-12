package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.LinearInterpolator
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
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
import arch.cayenne.lib.common.utils.ext.locationOnScreen
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.databinding.FragmentComboBet2Binding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
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

    private val comboMultiBetAdapter by lazy {
        ComboMultiBetAdapter(object : ComboMultiBetAdapter.OnComboMultiBetClickListener {
            override fun onEditMoneyClick(serialValue: Int, locationX: Int, locationY: Int) {
                mViewModel.onComboMultiBetBeanListener.value?.find { it.serialValue == serialValue }
                    ?.let {
                        //获取输入的钱，并更新
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
                        //显示弹窗
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
                    } ?: let {
                        "cannot find $serialValue in onComboMultiBetBeanListener:${mViewModel.onComboMultiBetBeanListener.value}"
                            .also { showToast(it) }
                            .loge(TAG)
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
                val location = it.locationOnScreen
                val x = location.first() + it.width / 2
                val y = location.last()
                val item = mViewModel.firstComboMultiBetBeanLD.value ?: return@setOnClickListener
                comboMultiBetAdapter.onComboMultiBetClickListener.onEditMoneyClick(item.serialValue, x, y)
            }
        }
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
        mBinding.clMultiBetTitle.setOnClickListener {
            if(comboMultiBetAdapter.itemCount == 0){
                showToast(R.string.toast_no_more_bet_combs.getString())
                return@setOnClickListener
            }
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
        mViewModel.firstComboMultiBetBeanLD.observe(viewLifecycleOwner) { item->
            with(mBinding.firstMultiItem) {
                val moneySymbol = mViewModel.moneySymbol
                tvTitleCombo.text = let {
                    val combo = getString(R.string.title_combo_bet_odds).format(item.comboK, item.comboV)
                    combo
                }
                tvMulti.text = let { "@${item.sumOdds.getOdds()}" }
                etMoney.setText( let {
                    if (item.inputMoney > 0) {
                        val money = "$moneySymbol ${item.inputMoney.getMoney()}"
                        money
                    } else {
                       ""
                    }
                })
                val moneyHint = "$moneySymbol ${getString(R.string.et_money_hint).format(item.minAmount.getMoney(), item.maxAmount.getMoney())}"
                etMoney.hint = moneyHint
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

    private fun adjustLayoutHeight() {
        if (isDetached || isRemoving || !isAdded) return
    }

    private fun setSumBetMoney(data: List<ComboMultiBetBean>) {
        val sumMoney = data.sumOf { it.amount }
        val money = "${mViewModel.moneySymbol}${sumMoney.getFormalMoney()}"

        val winMoney = data.sumOf { it.maxWinMoney }
        mBinding.tvBetMoneyHint.isVisible = winMoney != 0L
        mBinding.tvBetMoney.isVisible = winMoney != 0L
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