package arch.cayenne.module.handicap.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.handicap.R
import arch.cayenne.module.handicap.compare.CornerBallCompare
import arch.cayenne.module.handicap.data.AnswerType
import arch.cayenne.module.handicap.data.CornerBallBean
import arch.cayenne.module.handicap.databinding.ItemCornerLayoutBinding

class CornerBallAdapter : BaseAdapter<CornerBallBean, BaseViewHolder, ItemCornerLayoutBinding>(
    CornerBallCompare()
) {
    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemCornerLayoutBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvCornerName.text = item.type
        binding.tvHomeEvent.text = item.homeTip
        binding.tvAwayEvent.text = item.awayTip
        binding.tvHomeType.setTextColor(
            getBtnTextColor(holder.itemView.context, item.homeType)
        )
        binding.tvHomeType.text = getTypeText(holder.itemView.context, item.homeType)
        binding.tvHomeType.background = getBackground(holder.itemView.context, item.homeType)
        binding.tvAwayType.setTextColor(
            getBtnTextColor(holder.itemView.context, item.awayType)
        )
        binding.tvAwayType.text = getTypeText(holder.itemView.context, item.awayType)
        binding.tvAwayType.background = getBackground(holder.itemView.context, item.awayType)
        binding.tvGameScore.text = String.format("%d-%d", item.homeScore, item.awayScore)
        binding.tvLetBallMsg.text = item.msg
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCornerLayoutBinding {
        return ItemCornerLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemCornerLayoutBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }

    private fun getBtnTextColor(context: Context, type: Int): Int {
        return when (type) {
            AnswerType.WIN_ALL.type -> SkinnableResourceManager.getColor(
                context,
                R.color.text_green
            )
            AnswerType.LOSE_ALL.type -> SkinnableResourceManager.getColor(context, R.color.text_red)
            else -> 0
        }
    }

    private fun getBackground(context: Context, type: Int): Drawable? {
        return when (type) {
            AnswerType.WIN_ALL.type -> AppCompatResources.getDrawable(
                context,
                R.drawable.bg_shape_win
            )
            AnswerType.LOSE_ALL.type -> AppCompatResources.getDrawable(
                context,
                R.drawable.bg_shape_lose
            )
            else -> null
        }
    }

    private fun getTypeText(context: Context, type: Int): String {
        return when (type) {
            AnswerType.WIN_ALL.type -> context.getString(R.string.win_all)
            AnswerType.LOSE_ALL.type -> context.getString(R.string.lose_all)
            else -> ""
        }
    }
}