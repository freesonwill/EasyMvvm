package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.ui.adapter.compare.MatchItemCompare

/**
 * @author:
 * @date: 2025/5/23 上午11:30
 * @description:
 */
class FavoriteListAdapter: BaseAdapter<MatchWithMarkets, MatchItemViewHolder, ItemMatchCardBinding>(
    MatchItemCompare()
) {
    override fun convertPlus(
        holder: MatchItemViewHolder,
        binding: ItemMatchCardBinding,
        position: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemMatchCardBinding {
        TODO("Not yet implemented")
    }

    override fun createViewHolder(
        binding: ItemMatchCardBinding,
        viewType: Int,
    ): MatchItemViewHolder {
        TODO("Not yet implemented")
    }
}