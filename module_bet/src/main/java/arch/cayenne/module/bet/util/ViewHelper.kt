package arch.cayenne.module.bet.util

import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding

internal object ViewHelper {

    fun bindBetSheet(bean: BetBean, binding: ItemBetSheetBinding) {
        val odds = "@${bean.selectionLiteBean.odds}"
        binding.tvOdds.text = odds

        binding.tvSelectionName.text = bean.selectionLiteBean.name
        binding.tvMarket.text = bean.selectionLiteBean.marketName
        binding.tvMatchName.text = bean.matchName
        binding.tvLeagueName.text = bean.leagueName

        binding.ivDelete.isVisible = bean.betType == BetTypeEnum.COMBO && bean.status == BetStatusEnum.PENDING_BET
        binding.tvStatus.isVisible = bean.isPlaying
        binding.tvBetStop.isVisible = bean.isBetStop
    }
}