package arch.cayenne.module.bet.util

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.OddsStatusEnum
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
        binding.tvBetStop.isVisible = !bean.isActive

        val oddsColor = when (bean.oddsStatus) {
            OddsStatusEnum.UP -> ContextCompat.getColor(binding.root.context, arch.cayenne.module.bet.R.color.green)
            OddsStatusEnum.DOWN -> ContextCompat.getColor(binding.root.context, arch.cayenne.module.bet.R.color.red)
            else -> null
        }
        if (oddsColor != null) {
            val originColor = binding.tvOdds.currentTextColor
            binding.tvOdds.setTextColor(oddsColor)
            binding.tvOdds.postDelayed(2_000L) {
                binding.tvOdds.setTextColor(originColor)
            }
        }
    }
}