package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
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

class BetResultFragment : BaseFragment<BetResultViewModel, FragmentBetResultBinding>(),
    BetSheetListener {
    override val vbClass: KClass<FragmentBetResultBinding> = FragmentBetResultBinding::class
    override val vmClass: KClass<BetResultViewModel> = BetResultViewModel::class
    private val betSelectionAdapter by lazy { BetSelectionAdapter() }
    private val detailAdapter by lazy { ResultMultiBetAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvComboOdds.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        mBinding.rvBet.adapter = betSelectionAdapter
        mBinding.rvComboOdds.adapter = detailAdapter

        val decoration = BetSheetDecoration(6.dp2px, 12.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)
    }

    override fun initListener() {
        mBinding.btnContinueBet.setOnClickListener {
            lifecycleScope.launch {
                mViewModel.continueBet()?.let { type ->
                    when (type) {
                        BetTypeEnum.SINGLE, BetTypeEnum.RESERVE -> navigate(BetResultFragmentDirections.actionBetResultFragmentToSingleBetFragment(), null)
                        BetTypeEnum.COMBO -> navigate(BetResultFragmentDirections.actionBetResultFragmentToComboBetFragment(), null)
                    }
                }
            }
        }
        mBinding.btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            mBinding.rvComboOdds.isVisible = it.size > 1
            betSelectionAdapter.submitList(it) {
                mBinding.rvBet.post {
                    adjustLayoutHeight(it.size > 2)
                }
            }
            mBinding.tvMaxWin.text = if (it.size == 1 && mViewModel.type == BetTypeEnum.SINGLE) {
                getString(R.string.title_result_win_single_bet)
            } else {
                getString(R.string.title_result_win_combo_bet)
            }
        }
        mViewModel.onDetailListener.observe(viewLifecycleOwner) {
            setAmount(it)
            setComboOdds(it)
        }
        mViewModel.onBetModeListener.observe(viewLifecycleOwner) {
            setBetMode(it.first, it.second)
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
        mBinding.tvHint.isVisible = true
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_pending)
        mBinding.tvTitle.text = if (type == BetTypeEnum.RESERVE) {
            getString(R.string.title_result_pending_reserve)
        } else {
            getString(R.string.title_result_pending_bet)
        }
    }

    private fun setComplete(type: BetTypeEnum) {
        mBinding.tvHint.isVisible = false
        mBinding.ivTitle.setImageResource(R.mipmap.icon_bet_result_success)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_success_reserve)
        } else {
            mBinding.tvTitle.text = getString(arch.cayenne.lib.common.R.string.title_result_success_bet)
        }
        mBinding.btnContinueBet.isEnabled = true
        mBinding.btnContinueBet.setTextColor(ContextCompat.getColor(requireContext(), arch.cayenne.lib.common.R.color.brand_color))
    }

    private fun setFail(type: BetTypeEnum) {
        mBinding.tvHint.isVisible = false
        mBinding.ivTitle.setImageResource(arch.cayenne.lib.common.R.mipmap.icon_bet_result_fail)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getString(R.string.title_result_fail_reserve)
        } else {
            mBinding.tvTitle.text = getString(arch.cayenne.lib.common.R.string.title_result_fail_bet)
        }
        mBinding.btnContinueBet.isEnabled = true
        mBinding.btnContinueBet.setTextColor(ContextCompat.getColor(requireContext(), arch.cayenne.lib.common.R.color.brand_color))
    }

    private fun setAmount(data: List<BetDetailBean>) {
        val total = "\$${data.sumOf { it.inputMoney }.getMoney()}"
        mBinding.tvAmountMoney.text = total
        val win = "\$${data.sumOf { it.inputMoney.getMoney(it.sumOdds).toMoney() }.getMoney()}"
        mBinding.tvMaxWinMoney.text = win
    }

    private fun setComboOdds(data: List<BetDetailBean>) {
        detailAdapter.submitList(data)
    }

    private fun adjustLayoutHeight(full: Boolean) {
        if (full)  {
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

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.betResultFragment)
    }
}