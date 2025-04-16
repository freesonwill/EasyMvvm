package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.sendResult
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetResultBinding
import arch.cayenne.module.bet.ui.adapter.BetSheetAdapter
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import com.bumptech.glide.Glide
import kotlin.reflect.KClass

class BetResultFragment : BaseFragment<BetResultViewModel, FragmentBetResultBinding>(),
    BetSheetListener {
    override val vbClass: KClass<FragmentBetResultBinding> = FragmentBetResultBinding::class
    override val vmClass: KClass<BetResultViewModel> = BetResultViewModel::class
    private val args: BetResultFragmentArgs by navArgs()
    private val betSheetAdapter by lazy { BetSheetAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        val id = args.id
        mViewModel.setBetSheet(if (id == -1) null else id)

        mBinding.rvBet.adapter = betSheetAdapter
    }

    override fun initListener() {
        mBinding.btnConfirm.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
        mViewModel.onBetSheetListener.observe(viewLifecycleOwner) {
            betSheetAdapter.submitList(it)
        }
        mViewModel.onBetModeListener.observe(viewLifecycleOwner) {
            setBetMode(it.first, it.second)
        }
    }

    private fun setBetMode(type: BetTypeEnum, status: BetStatusEnum) {
        mBinding.rvComboOdds.isVisible = type == BetTypeEnum.COMBO
        mBinding.tvHint.isVisible = status == BetStatusEnum.BETTING
        when (status) {
            BetStatusEnum.FAIL -> setFail(type)
            BetStatusEnum.BETTING -> setPending(type)
            BetStatusEnum.COMPLETE -> setComplete(type)
            else -> {}
        }
    }

    private fun setPending(type: BetTypeEnum) {
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_pending).into(mBinding.ivTitle)
        mBinding.tvTitle.text = if (type == BetTypeEnum.RESERVE) {
            getString(R.string.title_result_pending_reserve)
        } else {
            getString(R.string.title_result_pending_bet)
        }
    }

    private fun setComplete(type: BetTypeEnum) {
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_success).into(mBinding.ivTitle)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getText(R.string.title_result_success_reserve)
        } else {
            mBinding.tvTitle.text = getText(R.string.title_result_success_bet)
        }
    }

    private fun setFail(type: BetTypeEnum) {
        Glide.with(requireContext()).load(R.mipmap.icon_bet_result_fail).into(mBinding.ivTitle)
        if (type == BetTypeEnum.RESERVE) {
            mBinding.tvTitle.text = getText(R.string.title_result_fail_reserve)
        } else {
            mBinding.tvTitle.text = getText(R.string.title_result_fail_bet)
        }
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.betResultFragment)
    }
}