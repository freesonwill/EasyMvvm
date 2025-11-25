package arch.cayenne.module.order.ui.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderGameBinding
import arch.cayenne.module.order.data.model.RecordsBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

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
        loadImage(itemView.context, item.img, mBinding.ivGameLogo)
    }

    private fun loadImage(context: Context, url: String, image: ImageView) {
        val options = RequestOptions().transforms(RoundedCorners(40.dp2px))
        Glide.with(context).load(url).apply(options).into(image)
    }

    fun hideLine(position: Int, itemCount: Int) {
        mBinding.viewLine.isVisible = position != itemCount - 1
    }
}