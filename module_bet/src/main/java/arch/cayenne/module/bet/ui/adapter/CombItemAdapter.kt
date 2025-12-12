package arch.cayenne.module.bet.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems2
import arch.cayenne.module.bet.databinding.ItemComboDetail2Binding
import arch.cayenne.module.bet.ui.compare.ComboDetailItemCompare

class CombItemAdapter :
    BaseAdapter<ParameterItems2, CombinationItemViewHolder, ItemComboDetail2Binding>(
        ComboDetailItemCompare()
    ) {
    override fun convertPlus(
        holder: CombinationItemViewHolder,
        binding: ItemComboDetail2Binding,
        position: Int
    ) {
        holder.init(getItem(position), position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemComboDetail2Binding {
        return ItemComboDetail2Binding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemComboDetail2Binding,
        viewType: Int
    ): CombinationItemViewHolder {
        return CombinationItemViewHolder(binding)
    }
}

class CombinationItemViewHolder(private val mBinding: ItemComboDetail2Binding) : BaseViewHolder(mBinding) {

    fun init(bean: ParameterItems2, position: Int) {
        with(mBinding) {
            tvTabCombo.text = bean.combo
            tvTabBet.text = bean.money
            tvTabWin.text = bean.winMoney
            tvTabOdds.text = bean.odds
            if (position % 2 != 0) {
                clRoot.setBackgroundColor(Color.TRANSPARENT)
            } else {
                clRoot.setBackgroundResource(R.drawable.shape_combination)
            }
        }
    }
}