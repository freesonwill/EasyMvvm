package arch.cayenne.module.bet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemCombinationTitleBinding

class CombinationAdapter(private val type: Int = 1) :
    RecyclerView.Adapter<CombinationViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CombinationViewHolder {
        val binding =
            ItemCombinationTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if (type == 2) {
            binding.clRoot.setPadding(0, 15.dp2px, 0, 0)
        }
        return CombinationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CombinationViewHolder,
        position: Int
    ) {
        holder.bind(type)
    }

    override fun getItemCount(): Int = 1
}

class CombinationViewHolder(val item: ItemCombinationTitleBinding) : BaseViewHolder(item) {

    companion object {
        private const val TYPE_SINGLE = 1 //所有单关注单
        private const val TYPE_TWO = 2 //所有2串1注单
        private const val TYPE_THREE = 3 //所有3串1注单
    }

    fun bind(type: Int) {
        when (type) {
            TYPE_SINGLE -> {
                item.tvGameTitle.text = R.string.tab_title_single.getString()
                item.tvTabBet.isVisible = true
                item.tvTabWin.isVisible = true
            }

            TYPE_TWO -> {
                item.tvGameTitle.text = R.string.tab_title_two.getString()
                item.tvTabBet.isVisible = true
                item.tvTabWin.isVisible = true
            }

            TYPE_THREE -> {
                item.tvGameTitle.text = R.string.tab_title_three.getString()
                item.tvTabBet.isVisible = false
                item.tvTabWin.isVisible = false
            }
        }
    }
}
