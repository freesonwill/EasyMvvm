package arch.cayenne.module.home.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.module.home.databinding.ItemChampionCardBinding

class ChampionItemViewHolder(
    private val mBinding: ItemChampionCardBinding,
    private val onChampionItemClickListener: OnChampionItemClickListener?
) : BaseViewHolder(mBinding) {
    private lateinit var oddsGridAdapter: ChampionGridAdapter

    fun init(data: MarketWithSelections) {
        oddsGridAdapter = ChampionGridAdapter { selection, _ ->
            onChampionItemClickListener?.onOddsCellClick(selection)
        }
        with(mBinding) {
            tvMarketName.text = data.market.marketName
            val spanCount = 2
            rvOddsGrid.apply {
                layoutManager = GridLayoutManager(root.context, spanCount)
                adapter = oddsGridAdapter
                val spacingOutSide = 12.dp2px
                val spacing = 7.dp2px
                if (itemDecorationCount > 0) {
                    removeItemDecorationAt(0)
                }
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position == RecyclerView.NO_POSITION) return

                        val column = position % spanCount
                        outRect.left = if(column == 0) { spacingOutSide } else { spacing / 2 }
                        outRect.right = if(column == 0) { spacing / 2 } else { spacingOutSide }
                        if (position >= spanCount) {
                            outRect.top = spacing
                        }
                    }
                })
            }
            oddsGridAdapter.submitList(data.selections)
        }
    }

    fun bindPayload(item: MarketWithSelections, payloads: List<Any>) {
        val market = item.market
        val selections = item.selections
        val changes = payloads.firstOrNull() as? Set<*> ?: return
        val forceLocked = market.defaultSelectionCount == 0 || selections.isEmpty()
        oddsGridAdapter.submitList(item.selections)
    }
}