package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.sendResult
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.module.bet.data.Config.KEY_RESULT
import arch.cayenne.module.bet.data.Config.VALUE_MONEY_INPUT
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
import arch.cayenne.module.bet.databinding.FragmentComboBetBinding
import arch.cayenne.module.bet.ui.adapter.BetSheetAdapter
import arch.cayenne.module.bet.ui.adapter.ComboMultiBetAdapter
import arch.cayenne.module.bet.util.BetSheetDecoration
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import kotlin.reflect.KClass

class ComboBetFragment : BaseFragment<ComboBetViewModel, FragmentComboBetBinding>(),
    BetSheetListener {

    override val vbClass: KClass<FragmentComboBetBinding> = FragmentComboBetBinding::class
    override val vmClass: KClass<ComboBetViewModel> = ComboBetViewModel::class

    private val betSheetAdapter by lazy {
        BetSheetAdapter(object : BetSheetAdapter.OnBetSheetClickListener {
            override fun onDeleteClick(item: BetBean) {
                CommonDialog.newInstance(
                    title = "",
                    message = getString(R.string.title_dialog_remove),
                    okText = getString(R.string.btn_confirm),
                    cancelText = getString(R.string.btn_cancel)
                ).apply {
                    setOnOkClickListener {
                        mViewModel.removeBet(item.matchId)
                    }
                }.show(childFragmentManager)
            }
        })
    }

    private val comboMultiBetAdapter by lazy {
        ComboMultiBetAdapter(object : ComboMultiBetAdapter.OnComboMultiBetClickListener {
            override fun onEditMoneyClick(id: Int, locationX: Int, locationY: Int) {
                mViewModel.onComboMultiBetBeanListener.value?.find { it.combo == id }?.let {
                    childFragmentManager.setFragmentResultListener(KEY_RESULT, viewLifecycleOwner) { resultKey, bundle ->
                        parentFragmentManager.clearFragmentResultListener(KEY_RESULT)
                        if (resultKey == KEY_RESULT) {
                            val money = bundle.getString(VALUE_MONEY_INPUT, "")
                            mViewModel.updateMultiBetMoney(id, money.toValue())
                        }
                    }
                    val currentMoney = if (it.inputMoney == 0) "" else it.inputMoney.getMoney()
                    val minAmount = it.minAmount
                    val maxAmount = it.maxAmount
                    ComboBetMoneyKeyboardDialogFragment.newInstance(locationX, locationY, currentMoney, minAmount, maxAmount).show(childFragmentManager)
                }
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvMultiBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        mBinding.rvBet.adapter = betSheetAdapter

        val decoration = BetSheetDecoration(6.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        mBinding.rvMultiBet.adapter = comboMultiBetAdapter
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
        mBinding.btnDelete.setOnClickListener {
            mViewModel.removeAll()
        }
        mBinding.llMultiBetCollapse.setOnClickListener {
            comboMultiBetAdapter.toggleExpand()
            if (comboMultiBetAdapter.isExpanded) {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_collapse)
            } else {
                mBinding.tvMultiBetExpand.text = getString(R.string.title_combo_bet_odds_expand)
            }
        }
        mBinding.clBet.setOnClickListener {
            mViewModel.sendBet()
            navigate(ComboBetFragmentDirections.actionComboBetFragmentToBetResultFragment())
        }
    }

    override fun createObserver() {
        mViewModel.onBetListListener.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                dismiss()
            } else if (it.size > 1) {
                betSheetAdapter.submitList(it)
            } else {
                navigate(
                    ComboBetFragmentDirections.actionComboBetFragmentToSingleBetFragment(),
                    null
                )
            }
        }
        var hasLockBetSheetView = false
        mViewModel.onComboMultiBetBeanListener.observe(viewLifecycleOwner) {
            comboMultiBetAdapter.submitList(it) {
                if (!hasLockBetSheetView) {
                    hasLockBetSheetView = true
                    setBetSheetView()
                }
            }
            setSumBetMoney(it)
        }
    }

    private fun setSumBetMoney(data: List<ComboMultiBetBean>) {
        val sumMoney = data.sumOf { it.amount.toValue() }
        val money = "\$${sumMoney.getMoney()}"
        mBinding.tvSumBetMoney.text = money
    }

    private fun setBetSheetView() {
        mBinding.root.post {
            val paddingBottom = mBinding.clMultiBet.height + 22.dp2px
            mBinding.rvBet.setPadding(0, 0, 0, paddingBottom)
        }
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.comboBetFragment)
    }
}