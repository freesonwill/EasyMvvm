package arch.cayenne.module.bet.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
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

    // 必須使用faker view才能解決子view設置0dp會顯示錯誤問題
    fun collapseView(view: View, fakerView: ImageView) {
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        val initialHeight = view.height

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = fakerView.layoutParams
            layoutParams.height = animatedValue
            fakerView.layoutParams = layoutParams
        }
        animator.doOnStart {
            val layoutParams = fakerView.layoutParams
            layoutParams.height = initialHeight
            fakerView.layoutParams = layoutParams
            fakerView.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        animator.doOnEnd {
            fakerView.visibility = View.GONE
        }

        animator.start()
    }

    fun expandView(view: View, fakerView: ImageView) {

        // 先確保原始 view 是隱藏狀態
        view.visibility = View.GONE

        // 把實際要展出的畫面先截圖給 fakerView
        val snapshot = view.drawToBitmap()
        fakerView.setImageBitmap(snapshot)

        // 先設為 0 高度，逐步展開
        val targetHeight = snapshot.height
        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = fakerView.layoutParams
            layoutParams.height = animatedValue
            fakerView.layoutParams = layoutParams
        }

        animator.doOnStart {
            val layoutParams = fakerView.layoutParams
            layoutParams.height = 0
            fakerView.layoutParams = layoutParams
            fakerView.visibility = View.VISIBLE
        }

        animator.doOnEnd {
            // 展開完成後切回原始 view，隱藏 fakerView
            fakerView.visibility = View.GONE
            view.visibility = View.VISIBLE
        }

        animator.start()
    }
}