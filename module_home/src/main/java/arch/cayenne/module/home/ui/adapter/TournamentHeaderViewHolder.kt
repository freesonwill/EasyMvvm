package arch.cayenne.module.home.ui.viewholder

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding

class TournamentHeaderViewHolder(
    private val mBinding: ItemTournamentHeaderBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: TournamentListItem.Header, position: Int) {
        with(mBinding) {
            (if (position == 0) View.VISIBLE else View.GONE).also { ivHeaderHot.visibility = it }
            tvHeaderName.text = item.letter.toString()
        }
    }
}