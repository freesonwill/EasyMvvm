package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetResultBinding
import arch.cayenne.module.bet.ui.adapter.BetSheetAdapter
import arch.cayenne.module.bet.ui.adapter.ResultMultiBetAdapter
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import com.bumptech.glide.Glide
import kotlin.reflect.KClass

class BetResultFragment : BaseFragment<BetResultViewModel, FragmentBetResultBinding>(),
    BetSheetListener {
    override val vbClass: KClass<FragmentBetResultBinding> = FragmentBetResultBinding::class
    override val vmClass: KClass<BetResultViewModel> = BetResultViewModel::class
    private val args: BetResultFragmentArgs by navArgs()
    private val betSheetAdapter by lazy { BetSheetAdapter() }
    private val detailAdapter by lazy { ResultMultiBetAdapter(
        object : ResultMultiBetAdapter.ResultMultiBetListener {
            override fun getBetSize(): Int {
                return mViewModel.onBetSheetListener.value?.size ?: 0
            }
        }
    ) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.setResultId(args.id)

        mBinding.rvBet.adapter = betSheetAdapter
        mBinding.rvComboOdds.adapter = detailAdapter
    }

    override fun initListener() {
        mBinding.btnContinueBet.setOnClickListener {

        }
        mBinding.btnConfirm.setOnClickListener {
            mViewModel.clearBetBean()
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            mBinding.rvComboOdds.isVisible = it.size > 1
            betSheetAdapter.submitList(it)
            mBinding.tvMaxWin.text = if (it.size == 1 && it.first().betType == BetTypeEnum.SINGLE) {
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
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_pending).into(mBinding.ivTitle)
        mBinding.tvTitle.text = if (type == BetTypeEnum.RESERVE) {
            getString(R.string.title_result_pending_reserve)
        } else {
            getString(R.string.title_result_pending_bet)
        }
    }

    private fun setComplete(type: BetTypeEnum) {
        mBinding.tvHint.isVisible = false
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_success).into(mBinding.ivTitle)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getText(R.string.title_result_success_reserve)
        } else {
            mBinding.tvTitle.text = getText(R.string.title_result_success_bet)
        }
    }

    private fun setFail(type: BetTypeEnum) {
        mBinding.tvHint.isVisible = false
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_fail).into(mBinding.ivTitle)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getText(R.string.title_result_fail_reserve)
        } else {
            mBinding.tvTitle.text = getText(R.string.title_result_fail_bet)
        }
    }

    private fun setAmount(data: List<BetResultDetailBean>) {
        val total = "\$${data.sumOf { it.inputMoney }.getMoney()}"
        mBinding.tvAmountMoney.text = total
        val win = "\$${data.sumOf { it.inputMoney.getMoney(it.sumOdds).toMoney() }.getMoney()}"
        mBinding.tvMaxWinMoney.text = win
    }

    private fun setComboOdds(data: List<BetResultDetailBean>) {
        detailAdapter.submitList(data)
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.betResultFragment)
    }
}