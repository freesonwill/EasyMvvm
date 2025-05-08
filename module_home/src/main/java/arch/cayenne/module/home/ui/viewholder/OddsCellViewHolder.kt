package arch.cayenne.module.home.ui.viewholder

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.addListener
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemOddsCellBinding
import arch.cayenne.module.home.enums.OddsCellState

class OddsCellViewHolder(
    private val mBinding: ItemOddsCellBinding,
    private val onOddsClick: (SelectionBeanLite, Boolean) -> Unit
) : BaseViewHolder(mBinding) {
    private var currentState: OddsCellState = OddsCellState.HIDDEN
    fun bind(item: SelectionBeanLite) {
        currentState = OddsCellState.VISIBLE
        with(mBinding) {
            tvShortName.text = item.shortName
            tvOdds.text = item.odds.getOdds()
            llOddsCell.isSelected = item.isSelected //<<<< 是否選中
            val isActive = item.active
            updateState(isActive)

            llOddsCell.setOnClickListener {
                if (item.active) {
                    val isSelected = !(llOddsCell.isSelected)
                    onOddsClick(item, isSelected)
                }
            }
        }
    }


    fun bindPayload(item: SelectionBeanLite, payloads: List<Any>) {
        currentState = OddsCellState.VISIBLE
        val diff = payloads.firstOrNull() as? Set<*> ?: return
        val isActive = item.active
        with(mBinding) {

            if ("odds" in diff) {
                if (tvOdds.text.toString() != item.odds.getOdds()) {
                    tvOdds.text = item.odds.getOdds()
                }
            }

            if ("shortName" in diff) {
                if (tvShortName.text.toString() != item.shortName) {
                    tvShortName.text = item.shortName
                }
            }

            if ("active" in diff) {
                updateState(isActive)
            }

            if ("parlay" in diff) {
                // 目前沒特別UI變化
            }

            if ("isSelected" in diff) {
                llOddsCell.isSelected = item.isSelected
            }

            if ("trend" in diff) {
                showOddsTrend(item.trend)
            }
        }
    }

    private fun showOddsTrend(trendDelta: Int?) {
        with(mBinding) {
            // 先隱藏所有效果
            ivTrendUp.visibility = View.GONE
            ivTrendDown.visibility = View.GONE
            vTrendHighlight.clearAnimation()
            vTrendHighlight.visibility = View.GONE

            val trendView = when {
                trendDelta == null || trendDelta == 0 -> null
                trendDelta < 0 -> ivTrendUp   // 賠率下降 → 變好
                trendDelta > 0 -> ivTrendDown // 賠率上升 → 變差
                else -> null
            }
            // 同步閃爍 trendView 和 overlay
            trendView?.let { trendImage ->
                trendImage.alpha = 1f
                trendImage.visibility = View.VISIBLE
                vTrendHighlight.alpha = 1f
                vTrendHighlight.visibility = View.VISIBLE

                val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                    duration = 800
                    addUpdateListener { animation ->
                        val alpha = animation.animatedValue as Float
                        vTrendHighlight.alpha = alpha
                        trendImage.alpha = alpha
                    }
                    addListener(onEnd = {
                        vTrendHighlight.visibility = View.GONE
                        trendImage.visibility = View.GONE
                    })
                }
                animator.start()
            }
        }
    }


    private fun updateState(active: Boolean) {

        with(mBinding) {
            tvShortName.visibility = if (active) View.VISIBLE else View.GONE
            tvOdds.visibility = if (active) View.VISIBLE else View.GONE
            ivLock.visibility = if (active) View.GONE else View.VISIBLE
            llOddsCell.isEnabled = active
        }
    }

    fun deActivate() {
        currentState = OddsCellState.DEACTIVATED
        with(mBinding) {
            tvShortName.visibility = View.GONE
            tvOdds.visibility = View.GONE
            ivLock.visibility = View.VISIBLE
            llOddsCell.isEnabled = false
        }
    }

    fun hideView() {
        currentState = OddsCellState.HIDDEN
        mBinding.root.visibility = View.GONE
    }

    fun isDeactivated(): Boolean {
        return currentState == OddsCellState.DEACTIVATED
    }
}
