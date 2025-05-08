package arch.cayenne.module.home.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.home.data.TournamentListItem

class TournamentSectionCompare : DiffUtil.ItemCallback<TournamentListItem>() {
    override fun areItemsTheSame(
        oldItem: TournamentListItem,
        newItem: TournamentListItem
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: TournamentListItem,
        newItem: TournamentListItem
    ): Boolean = oldItem == newItem

}