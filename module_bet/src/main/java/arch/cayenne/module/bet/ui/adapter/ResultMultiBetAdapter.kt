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

class ResultMultiBetAdapter(private val listener: ResultMultiBetListener) :
    BaseAdapter<BetDetailBean, BaseViewHolder, ItemResultMultiBetBinding>(
        BetResultDetailCompare()
    ) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemResultMultiBetBinding,
        position: Int
    ) {
        val item = getItem(holder.adapterPosition)
        val combo = holder.getString(R.string.title_combo_bet_odds).format(listener.getBetSize(), item.combo)
        binding.tvCombo.text = combo
        binding.tvOdds.text = item.sumOdds.getOdds()
        val money = "\$${item.inputMoney.getMoney()}"
        binding.tvBetMoney.text = money

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

    interface ResultMultiBetListener {
        fun getBetSize(): Int
    }
}