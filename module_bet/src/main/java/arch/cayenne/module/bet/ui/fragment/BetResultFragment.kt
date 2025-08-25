package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
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
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetResultBinding
import arch.cayenne.module.bet.ui.adapter.BetSelectionAdapter
import arch.cayenne.module.bet.ui.adapter.ResultMultiBetAdapter
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class BetResultFragment private constructor(): BaseBottomSheetFragment<BetResultViewModel, FragmentBetResultBinding>(), BetResultToastView.Block {

    companion object {
        fun newInstance(): BetResultFragment {
            return BetResultFragment()
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

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvComboOdds.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        mBinding.rvComboOdds.itemAnimator = null

        mBinding.rvBet.adapter = betSelectionAdapter
        mBinding.rvComboOdds.adapter = detailAdapter

        val decoration = BetSheetDecoration(6.dp2px, 12.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)
    }

    override fun initListener() {
        mBinding.btnContinueBet.setOnClickListener {
            lifecycleScope.launch {
                mViewModel.continueBet()?.let { type ->
                    BetSheetFragment.show(requireActivity(), getHideAnimator())
                }
            }
        }
        mBinding.btnConfirm.setOnClickListener {
            dismiss()
            mViewModel.sendDone()
        }
    }

    override suspend fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            mBinding.rvComboOdds.isVisible = it.size > 1
            betSelectionAdapter.submitList(it) {
                mBinding.rvBet.post {
                    adjustLayoutHeight(it.size > 2)
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
        mViewModel.betType.observe(viewLifecycleOwner) {
            mBinding.tvMaxWin.text = if (mViewModel.betType.value == BetTypeEnum.SINGLE) {
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
        val total = "$symbols${data.sumOf { it.inputMoney }.getFormalMoney()}"
        mBinding.tvAmountMoney.text = total
        val win =
            "$symbols${
                data.sumOf {
                    it.inputMoney.getMoney((if (data.size == 1) it.sumOdds.getDisplayOdds() else it.sumOdds.getOdds()).toOdds())
                        .toMoney()
                }.getFormalMoney()
            }"
        mBinding.tvMaxWinMoney.text = win
    }

    private fun setComboOdds(data: List<BetDetailBean>) {
        detailAdapter.submitList(data)
    }

    private fun adjustLayoutHeight(full: Boolean) {
        if (full) {
            val screenHeight = resources.displayMetrics.heightPixels
            val maxFragmentHeight = (screenHeight * 0.75).toInt()
            mBinding.root.minHeight = maxFragmentHeight
        } else {
            val layoutParams = mBinding.rvBet.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            mBinding.root.minHeight = 0
            mBinding.rvBet.layoutParams = layoutParams
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.sendDone()
    }
}