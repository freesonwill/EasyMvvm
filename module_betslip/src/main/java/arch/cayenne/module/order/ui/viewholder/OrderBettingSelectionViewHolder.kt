package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingSelectionBinding
import com.bumptech.glide.Glide

class OrderBettingSelectionViewHolder(private val mBinding: ItemOrderSportBettingSelectionBinding): BaseViewHolder(mBinding) {

    fun init(item: OrderSelectionBean, isSingle: Boolean) {
        mBinding.tvSelectionName.text = item.selectionName
        mBinding.tvStatus.isVisible = item.inPlay
        mBinding.tvMarket.text = item.marketName
        val odds = "@${if (isSingle) item.odds.getDisplayOdds() else item.odds.getOdds()}"
        mBinding.tvOdds.text = odds

        Glide.with(itemView.context).load(SportEnum.getSportEnumById(item.matchBasic.sportId)?.resId ?: SportEnum.Default.resId).into(mBinding.ivBall)
        mBinding.tvLeagueName.text = item.matchBasic.matchName

        mBinding.tvReserve.isVisible = false

        val isLive = isShowLiveButton(item.matchBasic.status)
        mBinding.ivLive.isVisible = isLive
        mBinding.tvLive.isVisible = isLive

        setResultStatus(item.status)
    }

    private fun isShowLiveButton(status: Int): Boolean {
        return status == 1 ||
                status == 4 ||
                status == 5 ||
                status == 6 ||
                status == 8
    }

    fun hideLine(isFirst: Boolean) {
        mBinding.topLine.isVisible = !isFirst
    }

    private fun setResultStatus(status: Int) {
        mBinding.groupResult.isVisible = status == 4 || status == 5
        when (status) {
            0, 6 -> {
                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_dot))
            }
            // 贏
            1 -> {
                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_win))
            }
            // 平
            2 -> {
                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_none))
            }
            // 輸
            3 -> {
                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_lose))
            }
            // 輸一半
            4 -> {
                mBinding.tvResult.setText(R.string.order_lose_half)

                mBinding.tvResult.backgroundTintList = SkinnableResourceManager.getColorStateList(itemView.context, arch.cayenne.lib.common.R.color.color_A50111)

                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_lose_half))
            }
            // 贏一半
            5 -> {
                mBinding.tvResult.setText(R.string.order_win_half)

                mBinding.tvResult.backgroundTintList = SkinnableResourceManager.getColorStateList(itemView.context, arch.cayenne.lib.common.R.color.color_00B001)

                mBinding.ivGameResult.setImageDrawable(SkinnableResourceManager.getDrawable(itemView.context, R.drawable.icon_order_status_win_half))
            }
        }
    }
}