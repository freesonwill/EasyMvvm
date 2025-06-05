package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
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
                CommonDialog.newInstance(
                    title = "",
                    message = getString(R.string.title_dialog_remove),
                    okText = getString(R.string.btn_confirm),
                    cancelText = getString(R.string.btn_cancel)
                ).apply {
                    setOnOkClickListener {
                        mViewModel.removeSelection(item.selectionId)
                    }
                }.show(childFragmentManager)
            }
        })
    }

    private val comboMultiBetAdapter by lazy {
        ComboMultiBetAdapter(object : ComboMultiBetAdapter.OnComboMultiBetClickListener {
            override fun onEditMoneyClick(id: Int, locationX: Int, locationY: Int) {
                mViewModel.onComboMultiBetBeanListener.value?.find { it.combo == id }?.let {
                    childFragmentManager.setFragmentResultListener(
                        KEY_RESULT,
                        viewLifecycleOwner
                    ) { resultKey, bundle ->
                        childFragmentManager.clearFragmentResultListener(KEY_RESULT)
                        if (resultKey == KEY_RESULT) {
                            val money = bundle.getLong(VALUE_MONEY_INPUT, 0L)
                            mViewModel.updateMultiBetMoney(id, money)
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

            override fun getSize(): Int {
                return mViewModel.onBetListListener.value?.size ?: 0
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        (mBinding.rvMultiBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        (mBinding.rvBet.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        mBinding.rvBet.adapter = betSelectionAdapter

        val decoration = BetSheetDecoration(6.dp2px)
        mBinding.rvBet.addItemDecoration(decoration)

        mBinding.rvMultiBet.adapter = comboMultiBetAdapter
        setBetSheetView()
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
            } else if (it.size == 1) {
                navigate(
                    ComboBetFragmentDirections.actionComboBetFragmentToSingleBetFragment(),
                    null
                )
            } else {
                betSelectionAdapter.submitList(it)
                mBinding.clBet.isEnabled = it.all { bean -> bean.isActive && bean.isParlay }
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
        mViewModel.onBalanceListener.observe(viewLifecycleOwner) {
            val money = "${CurrencySymbols.CNY} ${it.getFormalMoney()}"
            mBinding.tvBalance.text = money
        }
    }

    private fun setSumBetMoney(data: List<ComboMultiBetBean>) {
        val sumMoney = data.sumOf { it.amount }
        val money = "\$${sumMoney.getMoney()}"
        mBinding.tvSumBetMoney.text = money

        val winMoney = data.sumOf { it.maxWinMoney }
        val sumWinMoney =
            getString(R.string.btn_bet_win_money).format(CurrencySymbols.CNY, winMoney.getMoney())
        mBinding.tvBetMoney.text = sumWinMoney
    }

    private fun setBetSheetView() {
        mBinding.root.post {
            val paddingBottom = mBinding.clMultiBet.height + 22.dp2px
            mBinding.rvBet.setPadding(0, 0, 0, paddingBottom)
        }
    }

    override fun dismiss(key: String, value: String) {
        mViewModel.saveInputMoney()
        sendResult(key, value, R.id.comboBetFragment)
    }
}