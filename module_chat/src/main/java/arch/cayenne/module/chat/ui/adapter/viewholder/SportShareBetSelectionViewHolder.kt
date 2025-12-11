package arch.cayenne.module.chat.ui.adapter.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.OrderSelectionBean
import arch.cayenne.module.chat.databinding.ItemShareBetSelectionLayoutBinding
import com.bumptech.glide.Glide

/**
 * @author: wenxi
 * @date: 11/12/25 10:49
 * @description:
 */
class SportShareBetSelectionViewHolder (private val mBinding: ItemShareBetSelectionLayoutBinding): BaseViewHolder(mBinding) {

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
}