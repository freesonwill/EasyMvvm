package arch.cayenne.module.bet.ui.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemComboDetail1Binding
import arch.cayenne.module.bet.databinding.ItemComboDetail2Binding
import arch.cayenne.module.bet.ui.compare.ParameterUICompare
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterUI
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems2
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterUIItem

class ComboDetailAdapter : BaseAdapter<ParameterUIItem, BaseViewHolder, ViewBinding>(ParameterUICompare()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            ParameterUI.TYPE_GROUP -> ItemComboDetail1Binding.inflate(inflater, parent, false)
            ParameterUI.TYPE_CHILD -> ItemComboDetail2Binding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("unknown type: $viewType")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return when (viewType) {
            ParameterUI.TYPE_GROUP -> GroupVH(binding as ItemComboDetail1Binding)
            ParameterUI.TYPE_CHILD -> ChildVH(binding as ItemComboDetail2Binding)
            else -> throw IllegalArgumentException("unknown type: $viewType")
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GroupVH -> holder.bind(item.group!!,position)
            is ChildVH -> holder.bind(item.child!!,item.childIndexInGroup)
        }
    }

    class GroupVH(private val mBinding: ItemComboDetail1Binding) : BaseViewHolder(mBinding) {

        fun bind(bean: ParameterItems,position: Int) {
            with(mBinding) {
                tvTitle.text = bean.title
                val money = bean.items.getOrNull(0)?.moneyStr
                tvTabBet.isVisible = !TextUtils.isEmpty(money)
                tvTabWin.isVisible = !TextUtils.isEmpty(money)
            }
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            if (position > 0) {
                params.topMargin = 30.dp2px
            } else {
                params.topMargin = 0
            }
        }
    }

    class ChildVH(private val mBinding: ItemComboDetail2Binding) : BaseViewHolder(mBinding) {

        fun bind(bean: ParameterItems2, position: Int) {
            with(mBinding) {
                tvTabCombo.text = bean.comboStr
                tvTabBet.text = bean.moneyStr
                tvTabWin.text = bean.winMoneyStr
                tvTabOdds.text = bean.oddsStr
                if (position % 2 != 0) {
                    clRoot.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    clRoot.setBackgroundResource(R.drawable.shape_combination)
                }
            }
        }
    }
}


