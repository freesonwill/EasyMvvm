package arch.cayenne.module.bet.util

import androidx.core.view.isVisible
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding

internal object ViewHelper {

    fun bindBetSheet(bean: BetSelectionBean, binding: ItemBetSheetBinding) {
        val odds = "@${bean.odds.getOdds()}"
        binding.tvOdds.text = odds

        binding.tvSelectionName.text = bean.name
        binding.tvMarket.text = bean.marketName
        binding.tvMatchName.text = bean.matchName
        binding.tvLeagueName.text = bean.leagueName

        binding.tvStatus.isVisible = bean.isPlaying
        binding.tvBetStop.isVisible = bean.isBetStop
    }
}