package arch.cayenne.module.handicap.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.module.handicap.R
import arch.cayenne.module.handicap.compare.BigSmallCompare
import arch.cayenne.module.handicap.data.AnswerType
import arch.cayenne.module.handicap.data.BigSmallBean
import arch.cayenne.module.handicap.databinding.ItemBigSmallBinding
import arch.cayenne.module.handicap.databinding.ItemBigSmallLayBinding

class BigSmallAdapter :
    BaseAdapter<BigSmallBean, BaseViewHolder, ItemBigSmallBinding>(BigSmallCompare()) {

    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemBigSmallBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvItemLetName.text = item.type
        binding.tvItemLetTip.text = item.tips
        binding.layLetBall.removeAllViews()
        for (i in 0 until item.list.size) {
            val itemBinding =
                ItemBigSmallLayBinding.inflate(LayoutInflater.from(holder.itemView.context))
            val home = item.list[i].homeType
            val away = item.list[i].awayType
            val homeTip = item.list[i].homeTip
            val awayTip = item.list[i].awayTip
            val msg = item.list[i].msg
            itemBinding.tvLetBallMsg.text = msg
            itemBinding.tvHomeEvent.text = homeTip
            itemBinding.tvAwayEvent.text = awayTip
            itemBinding.tvHomeType.text = getTypeText(holder.itemView.context, home)
            itemBinding.tvHomeType.setTextColor(
                getBtnTextColor(holder.itemView.context, home)
            )
            itemBinding.tvHomeType.background =
                getBackground(holder.itemView.context, home)
            itemBinding.tvAwayType.text = getTypeText(holder.itemView.context, away)
            itemBinding.tvAwayType.setTextColor(
                getBtnTextColor(holder.itemView.context, away)
            )
            itemBinding.tvAwayType.background =
                getBackground(holder.itemView.context, away)
            itemBinding.tvGameScore.text =
                String.format("%d-%d", item.list[i].homeScore, item.list[i].awayScore)
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
    ): ItemBigSmallBinding {
        return ItemBigSmallBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemBigSmallBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    //1全赢  2全输  3赢一半  4输一半  5退本金
    private fun getTypeText(context: Context, type: Int): String {
        return when (type) {
            AnswerType.WIN_ALL.type -> context.getString(R.string.win_all)
            AnswerType.LOSE_ALL.type -> context.getString(R.string.lose_all)
            AnswerType.WIN_HALF.type -> context.getString(R.string.half_win)
            AnswerType.LOSE_HALF.type -> context.getString(R.string.half_lose)
            AnswerType.SEED_MONEY.type -> context.getString(R.string.seed_money)
            else -> ""
        }
    }

    private fun getBackground(context: Context, type: Int): Drawable? {
        return when (type) {
            AnswerType.WIN_ALL.type -> SportSkinResourceManager.getDrawable(
                context,
                R.drawable.bg_shape_win
            )
            AnswerType.LOSE_ALL.type -> SportSkinResourceManager.getDrawable(
                context,
                R.drawable.bg_shape_lose
            )
            AnswerType.WIN_HALF.type -> SportSkinResourceManager.getDrawable(
                context,
                R.drawable.bg_shape_win
            )
            AnswerType.LOSE_HALF.type -> SportSkinResourceManager.getDrawable(
                context,
                R.drawable.bg_shape_lose
            )
            AnswerType.SEED_MONEY.type -> SportSkinResourceManager.getDrawable(
                context,
                R.drawable.bg_shape_half
            )
            else -> null
        }
    }

    private fun getBtnTextColor(context: Context, type: Int): Int {
        return when (type) {
            AnswerType.WIN_ALL.type -> SportSkinResourceManager.getColor(
                context,
                R.color.text_green
            )
            AnswerType.LOSE_ALL.type -> SportSkinResourceManager.getColor(context, R.color.text_red)
            AnswerType.WIN_HALF.type -> SportSkinResourceManager.getColor(
                context,
                R.color.text_green
            )
            AnswerType.LOSE_HALF.type -> SportSkinResourceManager.getColor(
                context,
                R.color.text_red
            )
            AnswerType.SEED_MONEY.type -> SportSkinResourceManager.getColor(
                context,
                R.color.text_gray
            )
            else -> 0
        }
    }
}