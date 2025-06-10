package arch.cayenne.module.home.ui.adapter

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding

class TournamentHeaderViewHolder(
    private val mBinding: ItemTournamentHeaderBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: TournamentListItem.Header) {
        with(mBinding) {
            if (item.letter == '*') {
                ivHeaderHot.visibility = View.VISIBLE
                tvHeaderName.text = getString(R.string.tournament_section_title_hot)
            } else {
                ivHeaderHot.visibility = View.GONE
                tvHeaderName.text = item.letter.toString()
            }
        }
    }
}