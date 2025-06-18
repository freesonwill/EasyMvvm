package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.databinding.ItemTournamentSectionBinding
import arch.cayenne.module.home.ui.compare.TournamentSectionCompare

class TournamentSectionAdapter(
    private val onTournamentClick: (BaseTournamentData) -> Unit
) : BaseAdapter<TournamentListItem, BaseViewHolder, ViewBinding>(TournamentSectionCompare()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TournamentListItem.Header -> TYPE_HEADER
            is TournamentListItem.TournamentItem -> TYPE_ITEM
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            TYPE_HEADER -> ItemTournamentHeaderBinding.inflate(inflater, parent, false)
            TYPE_ITEM -> ItemTournamentSectionBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> TournamentHeaderViewHolder(binding as ItemTournamentHeaderBinding)
            TYPE_ITEM -> TournamentItemViewHolder(binding as ItemTournamentSectionBinding)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (val item = getItem(position)) {
            is TournamentListItem.Header -> {
                (holder as TournamentHeaderViewHolder).bind(item)
            }

            is TournamentListItem.TournamentItem -> {
                (holder as TournamentItemViewHolder).bind(item, onTournamentClick)
            }

        }
    }
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}