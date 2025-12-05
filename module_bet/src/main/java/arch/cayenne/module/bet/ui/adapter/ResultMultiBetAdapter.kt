package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.ComboMultiBetBean
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
        val item = getItem(position)
        binding.tvCombo.apply {
            text = when(item.serialValue){
                ComboMultiBetBean.SERIAL_VALUE_SUPER -> R.string.title_combo_bet_super.getString()
                else -> {
                    val combo = holder.getString(R.string.title_combo_bet_odds).format(item.comboK, item.comboV)
                    combo
                }
            }
            setTextColorRes(
                if(item.count == 1)
                    arch.cayenne.lib.common.R.color.color_C0C0C0
                else
                    arch.cayenne.lib.common.R.color.color_8FBEE9
            )
            clickNoRepeat {
                listener.onCombinationDetailClick(item.serialValue)
            }
        }
        binding.tvOdds.text = if(item.count == 1) "@${item.odds.getDisplayOdds()}" else ""
        val money = "${listener.getMoneySymbol()}${item.inputMoney.getFormalMoney()}"
        val multi = item.count.let {if(it == 1) money else "${it}x $money" }
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
        fun onCombinationDetailClick(serialValue: Int)
    }
}