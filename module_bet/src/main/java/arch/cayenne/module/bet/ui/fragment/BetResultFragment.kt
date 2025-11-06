package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.skin.data.SkinMsgType
import arch.cayenne.lib.skin.widget.biz.ISkinnableBiz
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetResultBinding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ResultMultiBetAdapter
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * 投注结果弹窗页
 */

class BetResultFragment :
    BasePreLoadBottomSheetFragment<BetResultViewModel, FragmentBetResultBinding>(),
    BetResultToastView.Block {

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
    private var isExpand: Boolean = false //是否折叠
    private var isFull: Boolean? = null
    private var temp: List<BetSelectionBean>? = null
    override fun initView(savedInstanceState: Bundle?) {
        isHorizontalGestureEnable = false
        isVerticalGestureEnable = false
        (mBinding.rvComboOdds.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvBet.itemAnimator = null
        mBinding.rvComboOdds.itemAnimator = null
        mBinding.rvBet.adapter = betSelectionAdapter
        mBinding.rvComboOdds.adapter = detailAdapter
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        mBinding.root.maxHeight = maxFragmentHeight
    }

    override fun initListener() {
        mBinding.tvBetAgain.setOnClickListener {
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
        mBinding.llExpand.clickNoRepeat {
            isExpand = !isExpand
            if (isExpand) { //折叠状态，显示展开n个盘口按钮
                betSelectionAdapter.submitList(temp?.take(1)) {
                    mBinding.rvBet.post {
                        expandImageView(mBinding.ivOrderExpand, false) {
                            mBinding.tvOrderCount.text =
                                getString(R.string.title_result_count).format(temp!!.size)
                        }
                        calculateLayoutHeight(temp!!.size)
                    }
                }
            } else { //展开状态，显示收起按钮
                betSelectionAdapter.submitList(temp) {
                    mBinding.rvBet.post {
                        expandImageView(mBinding.ivOrderExpand, true) {
                            mBinding.tvOrderCount.text = R.string.title_result_expand.getString()
                        }
                        calculateLayoutHeight(temp!!.size)
                    }
                }
            }
        }
    }

    override suspend fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            this.temp = it
            mBinding.rvComboOdds.isVisible = it.size > 1
            if (it.size > 2) {
                betSelectionAdapter.submitList(it.take(1)) {
                    mBinding.rvBet.post {
                        isExpand = true
                        mBinding.llExpand.isVisible = true
                        mBinding.tvOrderCount.text =
                            getString(R.string.title_result_count).format(it.size)
                        calculateLayoutHeight(1)
                    }
                }
            } else {
                betSelectionAdapter.submitList(it) {
                    mBinding.rvBet.post {
                        isExpand = false
                        mBinding.llExpand.isVisible = false
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
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_pending)
        mBinding.tvTitle.text = if (type == BetTypeEnum.RESERVE) {
            getString(R.string.title_result_pending_reserve)
        } else {
            getString(R.string.title_result_pending_bet)
        }
        mBinding.btnCheckBet.isVisible = false
    }

    private fun setComplete(type: BetTypeEnum) {
        mBinding.tvBetAgain.text = getString(R.string.btn_result_continue_bet)
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_success)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_success_reserve)
        } else {
            mBinding.tvTitle.text = getString(R.string.title_result_bet_suc)
        }
        mBinding.btnCheckBet.isVisible = true
    }

    private fun setFail(type: BetTypeEnum) {
        mBinding.tvBetAgain.text = getString(R.string.title_result_bet_again)
        mBinding.ivTitle.setImageResource(arch.cayenne.lib.common.R.mipmap.icon_bet_result_fail)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_fail_reserve)
        } else {
            mBinding.tvTitle.text =
                getString(arch.cayenne.lib.common.R.string.title_result_fail_bet)
        }
        val param = mBinding.btnConfirm.layoutParams
        if (param is ConstraintLayout.LayoutParams) {
            param.leftMargin = 0
        }
        mBinding.btnConfirm.layoutParams = param
        mBinding.btnCheckBet.isVisible = false
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
        val comboOddsHeight =
            (if (size == 0) 0 else getComboOddsItemHeight() * size) + (if (size == 0) 0 else (mBinding.rvComboOdds.layoutParams as LinearLayout.LayoutParams).bottomMargin)
        val betMoneyHeight =
            mBinding.clComboBetMoney.height + (mBinding.clComboBetMoney.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val buttonHeight =
            mBinding.btnConfirm.height + (mBinding.btnConfirm.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        val selectionHeight =
            getSelectionItemHeight() * size + (mBinding.llOrderList.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val totalHeight =
            topHeight + comboOddsHeight + betMoneyHeight + buttonHeight + selectionHeight
        adjustLayoutHeight(totalHeight > maxFragmentHeight)
    }

    private fun adjustLayoutHeight(full: Boolean) {
        if (this.isFull == full) return
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.75).toInt()
        mBinding.root.minHeight = if (full) maxFragmentHeight else 0
        val layoutParams = mBinding.nsv.layoutParams
        layoutParams.height = if (full) 0 else ViewGroup.LayoutParams.WRAP_CONTENT
        mBinding.nsv.layoutParams = layoutParams
        this.isFull = full
    }

    private fun getScreenHeight(): Int? {
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

    private fun expandImageView(iv: ImageView, expand: Boolean, onEnd: (() -> Unit)? = null) {
        if (expand) {
            ObjectAnimator.ofFloat(iv, "rotation", 0f, 180f)
                .also {
                    it.interpolator = LinearInterpolator()
                    it.duration = 100
                    it.addListener(onEnd = {
                        onEnd?.invoke()
                    })
                    it.start()
                }
        } else {
            ObjectAnimator.ofFloat(iv, "rotation", 180f, 0f)
                .also {
                    it.interpolator = LinearInterpolator()
                    it.duration = 100
                    it.addListener(onEnd = {
                        onEnd?.invoke()
                    })
                    it.start()
                }
        }
    }
}