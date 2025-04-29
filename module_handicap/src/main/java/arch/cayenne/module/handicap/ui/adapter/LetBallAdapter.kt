package arch.cayenne.module.handicap.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.module.handicap.R
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
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
            val view = ItemLetBallSmallBinding.inflate(
                LayoutInflater.from(holder.itemView.context),
                null,
                false
            )
            val homeType = view.root.findViewById<AppCompatTextView>(R.id.tv_home_type)
            val awayType = view.root.findViewById<AppCompatTextView>(R.id.tv_away_type)
            val score = view.root.findViewById<AppCompatTextView>(R.id.tv_game_score)
            val tips = view.root.findViewById<AppCompatTextView>(R.id.tv_let_ball_msg)
            homeType.text = item.list[i].homeType
            awayType.text = item.list[i].awayType
            score.text = String.format("%d-%d", item.list[i].homeScore, item.list[i].awayScore)
            tips.text = item.list[i].msg
            binding.layLetBall.addView(view.root)
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

}