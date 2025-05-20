package arch.cayenne.module.home.ui.adapter

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ItemTournamentSectionBinding
import com.bumptech.glide.Glide

class TournamentItemViewHolder(
    private val mBinding: ItemTournamentSectionBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: TournamentListItem.TournamentItem, onClick: (Int) -> Unit) {
        with(mBinding) {
            Glide.with(root)
                .load(item.tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                .placeholder(R.drawable.ic_default_tournament)
                .error(R.drawable.ic_default_tournament)
                .into(tvSectionIcon)
            tvSectionName.text = item.tournament.name
            root.setOnClickListener {
                onClick(item.tournament.id)
            }
        }
    }
}