package arch.cayenne.module.bet.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.data.CombThreeListData
import arch.cayenne.module.bet.databinding.ItemCombinationThreeBinding
import arch.cayenne.module.bet.ui.compare.CombThreeListDataCompare

class CombThreeListAdapter(private val type: Int = 1) :
    BaseAdapter<CombThreeListData, CombThreeListViewHolder, ItemCombinationThreeBinding>(
        CombThreeListDataCompare()
    ) {
    override fun convertPlus(
        holder: CombThreeListViewHolder,
        binding: ItemCombinationThreeBinding,
        position: Int
    ) {
        holder.bind(getItem(position), position, type)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCombinationThreeBinding {
        return ItemCombinationThreeBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemCombinationThreeBinding,
        viewType: Int
    ): CombThreeListViewHolder {
        return CombThreeListViewHolder(binding)
    }
}

class CombThreeListViewHolder(val item: ItemCombinationThreeBinding) : BaseViewHolder(item) {

    companion object {
        private const val TYPE_SINGLE = 1 //所有单关注单
        private const val TYPE_TWO = 2 //所有2串1注单
        private const val TYPE_THREE = 3 //所有3串1注单
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    fun bind(data: CombThreeListData, position: Int, type: Int) {
        with(item) {
            when (type) {
                TYPE_SINGLE -> {
                    item.tvTabBet.isVisible = true
                    item.tvTabWin.isVisible = true
                    item.tvTabBet.text = String.format("$%.2f", data.bet)
                    item.tvTabWin.text = String.format("$%.2f", data.win)
                }

                TYPE_TWO -> {
                    item.tvTabBet.isVisible = true
                    item.tvTabWin.isVisible = true
                    item.tvTabBet.text = String.format("$%.2f", data.bet)
                    item.tvTabWin.text = String.format("$%.2f", data.win)
                }

                TYPE_THREE -> {
                    item.tvTabBet.isVisible = false
                    item.tvTabWin.isVisible = false
                }
            }
            if (position % 2 != 0) {
                item.clRoot.setBackgroundColor(Color.TRANSPARENT)
            } else {
                item.clRoot.setBackgroundResource(R.drawable.shape_combination)
            }
            tvGameSelection.text = data.selection
            tvGameOdds.text = "@${data.odds}"
        }
    }
}