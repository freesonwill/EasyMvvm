package arch.cayenne.module.home.ui.adapter

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BaseGameSupplierData
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.data.GameSupplierListItem
import com.bumptech.glide.Glide
import com.walisport.module.hall.databinding.ItemSupplierSectionBinding
import com.walisport.module.hall.R
class GameSupplierViewHolder(
    private val mBinding: ItemSupplierSectionBinding
) : BaseViewHolder(mBinding) {
    fun bind(
        item: GameSupplierListItem.GameSupplierItem,
        isSelected: Boolean = false,
        onClick: (BaseGameSupplierData) -> Unit
    ) {
        with(mBinding) {
            Glide.with(root)
                .load(item.tournament.icon.ifEmpty { arch.cayenne.lib.common.R.drawable.ic_wali_demo })
                .placeholder(arch.cayenne.lib.common.R.drawable.ic_wali_demo)
                .error(arch.cayenne.lib.common.R.drawable.ic_wali_demo)
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
            // 根據 TournamentListType 調整顯示內容
            if (isSelected) {
                ivSelected.visibility = View.VISIBLE
                root.setBackgroundColor(
                    SkinnableResourceManager.getColor(
                        root.context,
                        arch.cayenne.lib.common.R.color.color_003A42
                    )
                )
                tvSectionName.setTextColor(
                    SkinnableResourceManager.getColor(
                        root.context,
                        arch.cayenne.lib.common.R.color.color_00E0E5
                    )
                )
            } else {
                ivSelected.visibility = View.GONE
                root.setBackgroundColor(
                    SkinnableResourceManager.getColor(
                        root.context,
                        arch.cayenne.lib.common.R.color.color_1E1E1E
                    )
                )
                tvSectionName.setTextColor(
                    SkinnableResourceManager.getColor(
                        root.context,
                        arch.cayenne.lib.common.R.color.color_FFFFFF
                    )
                )
            }

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