package arch.cayenne.module.order.ui.viewholder

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderGameBinding
import arch.cayenne.module.order.data.model.RecordsBean
import com.bumptech.glide.Glide

class OrderGameViewHolder(private val mBinding: ItemOrderGameBinding) : BaseViewHolder(mBinding) {

    @SuppressLint("SetTextI18n")
    fun init(item: RecordsBean) {
        mBinding.tvGameName.text = item.gameName
        mBinding.tvGameSupport.text = item.supplier
        mBinding.tvGameTime.text = item.time
        mBinding.tvGameWin.text = item.win
        mBinding.tvGameBelong.text = "牛牛"
        mBinding.tvGameMultiple.text = "584 x"
        mBinding.tvGameBet.text = R.string.title_bet.getString() + "  " + item.bet
        Glide.with(itemView.context).load(item.img).into(mBinding.ivGameLogo)
    }

    fun hideLine(position: Int, itemCount: Int) {
        mBinding.viewLine.isVisible = position != itemCount - 1
    }
}