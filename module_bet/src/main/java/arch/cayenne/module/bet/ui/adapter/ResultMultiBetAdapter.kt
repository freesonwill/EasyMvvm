package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemResultMultiBetBinding
import arch.cayenne.module.bet.ui.compare.BetResultDetailCompare

class ResultMultiBetAdapter :
    BaseAdapter<BetDetailBean, BaseViewHolder, ItemResultMultiBetBinding>(
        BetResultDetailCompare()
    ) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemResultMultiBetBinding,
        position: Int
    ) {
        val item = getItem(holder.adapterPosition)
        val combo = holder.getString(R.string.title_combo_bet_odds).format(item.comboK, item.comboV)
        binding.tvCombo.text = combo
        val odds ="@${item.sumOdds.getOdds()}"
        binding.tvOdds.text = odds
        val money = "\$${item.inputMoney.getMoney()}"
        binding.tvBetMoney.text = money

        val multi = "${item.count} x"
        binding.tvPlus.text = multi

    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemResultMultiBetBinding {
        return ItemResultMultiBetBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemResultMultiBetBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}