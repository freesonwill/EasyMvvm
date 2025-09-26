package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemResultMultiBetBinding
import arch.cayenne.module.bet.ui.compare.BetResultDetailCompare

class ResultMultiBetAdapter(private val listener: OnResultMultiBetListener) :
    BaseAdapter<BetDetailBean, BaseViewHolder, ItemResultMultiBetBinding>(
        BetResultDetailCompare()
    ) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemResultMultiBetBinding,
        position: Int
    ) {
        val item = getItem(holder.absoluteAdapterPosition)
        val combo = holder.getString(R.string.title_combo_bet_odds).format(item.comboK, item.comboV)
        val odds ="$combo @${item.odds.getDisplayOdds()}"

        binding.tvCombo.text = odds
        val money = "${listener.getMoneySymbol()}${item.inputMoney.getFormalMoney()}"
        val multi = "${item.count} x $money"
        binding.tvBetMoney.text = multi
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

    interface OnResultMultiBetListener {
        fun getMoneySymbol(): String
    }
}