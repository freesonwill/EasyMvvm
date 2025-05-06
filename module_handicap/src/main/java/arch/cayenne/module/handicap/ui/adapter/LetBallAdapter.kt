package arch.cayenne.module.handicap.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.handicap.R
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.module.handicap.compare.LetBallCompare
import arch.cayenne.module.handicap.data.LetBallBean
import arch.cayenne.module.handicap.databinding.ItemLetBallBinding
import arch.cayenne.module.handicap.databinding.ItemLetBallSmallBinding

class LetBallAdapter :
    BaseAdapter<LetBallBean, BaseViewHolder, ItemLetBallBinding>(
        LetBallCompare()
    ) {

    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemLetBallBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvItemLetName.text = item.type
        binding.tvItemLetTip.text = item.tips
        binding.layLetBall.removeAllViews()
        for (i in 0 until item.list.size) {
            val itemBinding = ItemLetBallSmallBinding.inflate(LayoutInflater.from(holder.itemView.context))
            val home = item.list[i].homeType
            val away = item.list[i].awayType
            itemBinding.tvHomeType.text = getTypeText(holder.itemView.context, home)
            itemBinding.tvHomeType.setTextColor(
                getBtnTextColor(holder.itemView.context, home)
            )
            itemBinding.tvHomeType.background =
                getBackground(holder.itemView.context, home)
            itemBinding.tvAwayType.text =
                getTypeText(holder.itemView.context, away)
            itemBinding.tvAwayType.background =
                getBackground(holder.itemView.context, away)
            itemBinding.tvAwayType.setTextColor(
                getBtnTextColor(holder.itemView.context, away)
            )
            itemBinding.tvGameScore.text = String.format("%d-%d", item.list[i].homeScore, item.list[i].awayScore)
            itemBinding.tvLetBallMsg.text = item.list[i].msg
            if (i == item.list.size - 1) {
                itemBinding.itemLine.visibility = View.GONE
            } else {
                itemBinding.itemLine.visibility = View.VISIBLE
            }
            binding.layLetBall.addView(itemBinding.root)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemLetBallBinding {
        return ItemLetBallBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemLetBallBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    //1全赢  2全输  3赢一半  4输一半  5退本金
    private fun getTypeText(context: Context, type: Int): String {
        return when (type) {
            1 -> context.getString(R.string.win_all)
            2 -> context.getString(R.string.lose_all)
            3 -> context.getString(R.string.half_win)
            4 -> context.getString(R.string.half_lose)
            5 -> context.getString(R.string.seed_money)
            else -> ""
        }
    }

    private fun getBackground(context: Context, type: Int): Drawable? {
        return when (type) {
            1 -> AppCompatResources.getDrawable(context, R.drawable.bg_shape_win)
            2 -> AppCompatResources.getDrawable(context, R.drawable.bg_shape_lose)
            3 -> AppCompatResources.getDrawable(context, R.drawable.bg_shape_win)
            4 -> AppCompatResources.getDrawable(context, R.drawable.bg_shape_lose)
            5 -> AppCompatResources.getDrawable(context, R.drawable.bg_shape_half)
            else -> null
        }
    }

    private fun getBtnTextColor(context: Context, type: Int): Int {
        return when (type) {
            1 -> SportSkinResourceManager.getColor(context, R.color.text_green)
            2 -> SportSkinResourceManager.getColor(context, R.color.text_red)
            3 -> SportSkinResourceManager.getColor(context, R.color.text_green)
            4 -> SportSkinResourceManager.getColor(context, R.color.text_red)
            5 -> SportSkinResourceManager.getColor(context, R.color.text_gray)
            else -> 0
        }
    }
}