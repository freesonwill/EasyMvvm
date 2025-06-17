package arch.cayenne.module.betslip.ui.viewholder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.BetSlipExpandedEnum
import arch.cayenne.module.betslip.databinding.ItemBetslipMoreLayoutBinding
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter
import arch.cayenne.module.betslip.ui.adapter.BetSlipSelectionAdapter
import arch.cayenne.module.betslip.ui.helper.BetTipsHelper
import arch.cayenne.module.betslip.utisl.BetSlipAdapterViewHolderInterface
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.database.entity.BetSlipSelectionData

abstract class BaseBetSlipViewHolder<VB: ViewBinding>(binding: ViewBinding, betSlipType: BetSlipEnum) : BaseViewHolder(binding), BetSlipAdapterViewHolderInterface {

    companion object {
        private const val EXPANDED_SIZE = 3
    }

    protected val mBinding: VB get() = binding as VB

    protected val adapter: BetSlipSelectionAdapter by lazy {
        BetSlipSelectionAdapter(betSlipType)
    }
    private var betSlipListener: BetSlipAdapter.BetSlipListener? = null
    private var expandedEnum = BetSlipExpandedEnum.NONE

    protected val moneySymbol: String
        get() = betSlipListener?.getMoneySymbol() ?: ""

    fun setLiveListener(listener: BetSlipAdapter.BetSlipLiveListener?) {
        adapter.setLiveListener(listener)
    }

    fun setBetSlipListener(listener: BetSlipAdapter.BetSlipListener?) {
        betSlipListener = listener
    }

    protected fun initItemView(recyclerView: RecyclerView) {
        val manager = LinearLayoutManager(recyclerView.context)
        recyclerView.also {
            it.layoutManager = manager
            it.itemAnimator = null
            it.adapter = adapter
        }
    }

    protected fun submitItemData(
        data: List<BetSlipSelectionData>,
    ) {
        if (data.size >= EXPANDED_SIZE && expandedEnum == BetSlipExpandedEnum.NONE) {
            expandedEnum = BetSlipExpandedEnum.COLLAPSED
            adapter.submitList(data.subList(0, EXPANDED_SIZE - 1))
        } else if (expandedEnum == BetSlipExpandedEnum.COLLAPSED) {
            expandedEnum = BetSlipExpandedEnum.EXPANDED
            adapter.submitList(data)
        } else if (expandedEnum == BetSlipExpandedEnum.EXPANDED) {
            expandedEnum = BetSlipExpandedEnum.COLLAPSED
            adapter.submitList(data.subList(0, EXPANDED_SIZE - 1))
        } else {
            expandedEnum = BetSlipExpandedEnum.NONE
            adapter.submitList(data)
        }
    }

    protected fun setGradientLayout(binding: ItemBetslipMoreLayoutBinding, anchorView: View) {
        val enum = expandedEnum
        binding.root.isVisible = enum != BetSlipExpandedEnum.NONE
        if (enum != BetSlipExpandedEnum.NONE) {
            when (enum) {
                BetSlipExpandedEnum.EXPANDED -> {
                    binding.tvMore.text = getString(R.string.fold_up)
                    binding.ivArrow.setImageDrawable(
                        SkinnableResourceManager.getDrawable(
                            binding.root.context, R.drawable.icon_circle_arrow_up
                        ))
                    binding.ivGradient.isVisible = false
                    binding.root.layoutParams = (binding.root.layoutParams as ConstraintLayout.LayoutParams).apply {
                        bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                        topToBottom = anchorView.id
                    }
                }
                BetSlipExpandedEnum.COLLAPSED -> {
                    binding.tvMore.text = getString(R.string.see_more)
                    binding.ivArrow.setImageDrawable(
                        SkinnableResourceManager.getDrawable(
                            binding.root.context, R.drawable.icon_circle_arrow_down
                        ))
                    binding.ivGradient.isVisible = true
                    binding.root.layoutParams = (binding.root.layoutParams as ConstraintLayout.LayoutParams).apply {
                        bottomToBottom = anchorView.id
                        topToBottom = ConstraintLayout.LayoutParams.UNSET
                    }
                }
                else -> {}
            }
        }
    }

    protected fun showBetTip(attachView: View) {
        val helper = BetTipsHelper()
        helper.showTips(attachView)
    }
}