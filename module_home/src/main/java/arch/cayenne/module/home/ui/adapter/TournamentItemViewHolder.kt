package arch.cayenne.module.home.ui.adapter

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.databinding.ItemTournamentSectionBinding
import com.bumptech.glide.Glide

class TournamentItemViewHolder(
    private val mBinding: ItemTournamentSectionBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: TournamentListItem.TournamentItem, onClick: (BaseTournamentData) -> Unit) {
        with(mBinding) {
            Glide.with(root)
                .load(item.tournament.icon.ifEmpty { R.drawable.ic_default_tournament })
                .placeholder(R.drawable.ic_default_tournament)
                .error(R.drawable.ic_default_tournament)
                .into(tvSectionIcon)
            val spannable = SpannableString(item.tournament.name)
            if (item.highlightStart != null && item.highlightEnd != null && item.highlightStart >= 0 && item.highlightEnd <= spannable.length) {
                spannable.withColorRes(
                    context = root.context,
                    start = item.highlightStart,
                    end = item.highlightEnd,
                    colorRes = arch.cayenne.lib.common.R.color.search_btn
                )
            }
            tvSectionName.text = spannable
            root.setOnClickListener {
                onClick(item.tournament)
            }
        }
    }
}

fun SpannableString.withColorRes(
    context: Context,
    start: Int,
    end: Int,
    @ColorRes colorRes: Int
): SpannableString {
    val color = ContextCompat.getColor(context, colorRes)
    return this.apply {
        setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}