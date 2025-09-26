package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetResultBinding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ResultMultiBetAdapter
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class BetResultFragment : BasePreLoadBottomSheetFragment<BetResultViewModel, FragmentBetResultBinding>(), BetResultToastView.Block {

    companion object {

        private const val TAG = "BetResultFragment"

        fun create(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetResultFragment().customAttach(activity, TAG)
            }
        }

        fun show(activity: FragmentActivity, withOtherSheetHide: ObjectAnimator? = null) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                BetResultFragment().show(manager, TAG)
            } else if (f is BasePreLoadBottomSheetFragment<*, *>) {
                if (withOtherSheetHide == null) {
                    f.customShow()
                } else {
                    f.customShow(withOtherSheetHide)
                }
            }
        }

        fun find(activity: FragmentActivity): BetResultFragment? {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            return f as? BetResultFragment
        }
    }
    override val vbClass: KClass<FragmentBetResultBinding> = FragmentBetResultBinding::class
    override val vmClass: KClass<BetResultViewModel> = BetResultViewModel::class
    private val betSelectionAdapter by lazy { BetSelectionAdapter() }
    private val detailAdapter by lazy {
        ResultMultiBetAdapter(object : ResultMultiBetAdapter.OnResultMultiBetListener {
            override fun getMoneySymbol(): String {
                return mViewModel.moneySymbol
            }
        })
    }
    private var isFull: Boolean? = null

    override fun initView(savedInstanceState: Bundle?) {
        isHorizontalGestureEnable = false
        isVerticalGestureEnable = false
        (mBinding.rvComboOdds.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvComboOdds.itemAnimator = null

        mBinding.rvBet.adapter = betSelectionAdapter
        mBinding.rvComboOdds.adapter = detailAdapter

        val decoration = BetSheetDecoration(6.dp2px, 12.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        val screenHeight = resources.displayMetrics.heightPixels
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        mBinding.root.maxHeight = maxFragmentHeight
    }

    override fun initListener() {
        mBinding.btnContinueBet.setOnClickListener {
            clearAllObserve()
            lifecycleScope.launch {
                mViewModel.continueBet()?.let {
                    BetSheetFragment.show(requireActivity(), getHideAnimator())
                }
            }
        }
        mBinding.btnConfirm.setOnClickListener {
            clearAllObserve()
            dismiss()
            mViewModel.sendDone()
        }
        setOnEndListener {
            mViewModel.sendDone()
            lifecycleScope.launch {
                createObserver()
            }
        }
    }

    override suspend fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            mBinding.rvComboOdds.isVisible = it.size > 1
            betSelectionAdapter.submitList(it) {
                mBinding.rvBet.post {
                    if (it.size in 1..3) {
                        calculateLayoutHeight(it.size)
                    }
                }
            }
        }
        mViewModel.onDetailListener.observe(viewLifecycleOwner) {
            setAmount(it)
            setComboOdds(it)
        }
        mViewModel.onBetModeListener.observe(viewLifecycleOwner) {
            setBetMode(it.first, it.second)
        }
        mViewModel.onBetType.observe(viewLifecycleOwner) {
            mBinding.tvMaxWin.text = if (mViewModel.onBetType.value == BetTypeEnum.SINGLE) {
                getString(R.string.title_result_win_single_bet)
            } else {
                getString(R.string.title_result_win_combo_bet)
            }
        }
    }

    private fun setBetMode(type: BetTypeEnum, status: BetResultStatusEnum) {
        when (status) {
            BetResultStatusEnum.REJECT, BetResultStatusEnum.CANCEL -> setFail(type)
            BetResultStatusEnum.SUCCESS_BET -> setComplete(type)
            else -> {
                setPending(type)
            }
        }
    }

    private fun setPending(type: BetTypeEnum) {
        mBinding.tvHint.text = getString(R.string.title_result_hint)
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_pending)
        mBinding.tvTitle.text = if (type == BetTypeEnum.RESERVE) {
            getString(R.string.title_result_pending_reserve)
        } else {
            getString(R.string.title_result_pending_bet)
        }
        mBinding.btnContinueBet.isEnabled = false
        mBinding.btnContinueBet.alpha = 0.5f
    }

    private fun setComplete(type: BetTypeEnum) {
        mBinding.tvHint.text = getString(R.string.title_result_hint_complete)
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_success)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_success_reserve)
        } else {
            mBinding.tvTitle.text =
                getString(arch.cayenne.lib.common.R.string.title_result_success_bet)
        }
        mBinding.btnContinueBet.isEnabled = true
        mBinding.btnContinueBet.alpha = 1.0f
    }

    private fun setFail(type: BetTypeEnum) {
        mBinding.tvHint.text = getString(R.string.title_result_hint_complete)
        mBinding.ivTitle.setImageResource(arch.cayenne.lib.common.R.mipmap.icon_bet_result_fail)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_fail_reserve)
        } else {
            mBinding.tvTitle.text =
                getString(arch.cayenne.lib.common.R.string.title_result_fail_bet)
        }
        mBinding.btnContinueBet.isEnabled = true
        mBinding.btnContinueBet.alpha = 1.0f
    }

    private fun setAmount(data: List<BetDetailBean>) {
        val symbols = mViewModel.moneySymbol
        val total = "$symbols${data.sumOf { it.inputMoney * it.count }.getFormalMoney()}"
        mBinding.tvAmountMoney.text = total
        val win =
            "$symbols${
                data.sumOf {
                    it.inputMoney.getMoney((if (data.size == 1) it.sumOdds.getDisplayOdds() else (it.odds * it.count).getOdds()).toOdds())
                        .toMoney()
                }.getFormalMoney()
            }"
        mBinding.tvMaxWinMoney.text = win
    }

    private fun setComboOdds(data: List<BetDetailBean>) {
        detailAdapter.submitList(data)
    }

    private fun calculateLayoutHeight(size: Int) {
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()

        val topHeight = mBinding.llTop.height
        val hintHeight = mBinding.tvHint.height + (mBinding.tvHint.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val comboOddsHeight = (if (size == 0) 0 else getComboOddsItemHeight() * size) + (if (size == 0) 0 else (mBinding.rvComboOdds.layoutParams as ConstraintLayout.LayoutParams).bottomMargin)
        val betMoneyHeight = mBinding.clComboBetMoney.height + (mBinding.clComboBetMoney.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val buttonHeight = mBinding.btnContinueBet.height + (mBinding.btnContinueBet.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val selectionHeight = getSelectionItemHeight() * size + (mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val totalHeight = topHeight + hintHeight + comboOddsHeight + betMoneyHeight + buttonHeight + selectionHeight
        adjustLayoutHeight(totalHeight > maxFragmentHeight)
    }

    private fun adjustLayoutHeight(full: Boolean) {
        if (this.isFull == full) return
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        if (full) {
            mBinding.root.minHeight = maxFragmentHeight
            val layoutParams = mBinding.rvBet.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = 0
            mBinding.rvBet.layoutParams = layoutParams
        } else {
            mBinding.root.minHeight = 0
            val layoutParams = mBinding.rvBet.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            mBinding.rvBet.layoutParams = layoutParams
        }
        this.isFull = full
    }

    private fun getScreenHeight(): Int? {
        // 检查 context 是否不为空
        return context?.resources?.displayMetrics?.heightPixels
    }

    private fun getComboOddsItemHeight(): Int {
        val layoutManager = mBinding.rvComboOdds.layoutManager as? LinearLayoutManager
        val firstVisibleItemView =
            layoutManager?.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
         // 不知道為什麼高度會少bottom空白間距
        return firstVisibleItemView?.height ?: 36.dp2px
    }

    private fun getSelectionItemHeight(): Int {
        val layoutManager = mBinding.rvBet.layoutManager as? LinearLayoutManager
        val firstVisibleItemView =
            layoutManager?.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
        // 不知道為什麼高度會少bottom空白間距
        return firstVisibleItemView?.height ?: 140.dp2px
    }

    private fun clearAllObserve() {
        mViewModel.onBetType.removeObservers(viewLifecycleOwner)
        mViewModel.onBetModeListener.removeObservers(viewLifecycleOwner)
        mViewModel.onBetSheetListener.removeObservers(viewLifecycleOwner)
        mViewModel.onDetailListener.removeObservers(viewLifecycleOwner)
    }

    fun forceUpdateSkin() {
        fun updateView(root: ViewGroup) {
            if (root is ISkinnableBiz) {
                root.updateSkin(SkinMsgType.SELF)
            }
            root.forEach {
                if (it is ViewGroup) {
                    updateView(it)
                } else if (it is ISkinnableBiz) {
                    it.updateSkin(SkinMsgType.SELF)
                }
            }
        }
        updateView(mBinding.root)
    }
}