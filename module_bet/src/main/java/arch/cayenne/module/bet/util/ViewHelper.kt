package arch.cayenne.module.bet.util

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.ViewExt.postDelayedSafely
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.OddsStatusEnum
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.bet.databinding.ItemBetSheetBinding
import org.koin.java.KoinJavaComponent.getKoin

internal object ViewHelper {

    private val manager: UserDataManager = getKoin().get(UserDataManager::class)

    fun bindBetSheet(size: Int, bean: BetSelectionBean, binding: ItemBetSheetBinding) {
        val odds = if (size == 1) {
            "@${bean.odds.getDisplayOdds(false)}"
        } else {
            "@${bean.odds.getOdds(false)}"
        }
        //Todo 验收后统一去掉
        if(odds == "@0.0"){
            "bindBetSheet:$odds,--${bean.odds}-size:$size--oddType:${manager.getValue(UserDataKey.KEY_ODDS, 0)}--bean:$bean".loge()
        }
        binding.tvOdds.text = odds
        binding.tvSelectionName.text = bean.name
        binding.tvMarket.text = bean.score.let {
            if (it.isBlank()) {
                bean.marketName
            } else {
                bean.marketName + "（%s）".format(it.replace(":", "-"))
            }
        }
        binding.tvMatchName.text = bean.matchName
        binding.tvLeagueName.text = bean.leagueName
        binding.tvStatus.isVisible = bean.isPlaying
        binding.tvBetStop.isVisible = !bean.isActive
        val oddsColor = when (bean.oddsStatus) {
            OddsStatusEnum.UP -> ContextCompat.getColor(
                binding.root.context,
                arch.cayenne.module.bet.R.color.green
            )

            OddsStatusEnum.DOWN -> ContextCompat.getColor(
                binding.root.context,
                arch.cayenne.module.bet.R.color.red
            )

            else -> null
        }
        //颜色根据oddsStatus变化
        val originColor = SkinnableResourceManager.getColor(
            binding.root.context,
            arch.cayenne.lib.common.R.color.color_00E0E5
        )
        if (oddsColor != null) {
            binding.tvOdds.setTextColor(oddsColor)
            binding.tvOdds.postDelayedSafely(2_000) {
                binding.tvOdds.setTextColor(originColor)
            }
        } else {
            binding.tvOdds.setTextColor(originColor)
        }
    }
}

fun collapseView(view: View, onEnd: (() -> Unit)? = null) {
    val height = view.height
    ObjectAnimator.ofFloat(view, "translationY", 0f, height.toFloat())
        .also {
            it.interpolator = LinearInterpolator()
            it.duration = 100
            it.addListener(onEnd = {
                onEnd?.invoke()
            })
            it.start()
        }
}

fun expandView(view: View, height: Float, onEnd: (() -> Unit)? = null) {
    ObjectAnimator.ofFloat(view, "translationY", height, 0f)
        .also {
            it.interpolator = LinearInterpolator()
            it.duration = 100
            it.addListener(onEnd = {
                onEnd?.invoke()
            })
            it.start()
        }
}