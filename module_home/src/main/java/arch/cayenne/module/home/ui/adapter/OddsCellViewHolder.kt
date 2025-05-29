package arch.cayenne.module.home.ui.adapter

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.addListener
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.data.constants.OddsCellState
import arch.cayenne.module.home.databinding.ItemOddsCellBinding

class OddsCellViewHolder(
    private val mBinding: ItemOddsCellBinding,
    private val onMatchItemClickListener: OnMatchItemClickListener?
) : BaseViewHolder(mBinding) {
    private var currentState: OddsCellState = OddsCellState.VISIBLE
    fun bind(item: SelectionBeanLite) {
        with(mBinding) {
            tvShortName.text = item.shortName
            tvOdds.text = item.odds.getOdds()
            val isActive = item.active
            updateState(isActive, item.isSelected)

            llOddsCell.setOnClickListener {
                if (isActive) {
                    onMatchItemClickListener?.onOddsCellClick(item)
                }
            }
        }
    }


    fun bindPayload(item: SelectionBeanLite, payloads: List<Any>) {
        val diff = payloads.firstOrNull() as? Set<*> ?: return
        val isActive = item.active
        currentState = if (isActive) OddsCellState.VISIBLE else OddsCellState.DEACTIVATED
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
                updateState(isActive, item.isSelected)
            }

            if ("parlay" in diff) {
                // 目前沒特別UI變化
            }

            if ("isSelected" in diff) {
                llOddsCell.isSelected = item.isSelected
            }

            if ("trend" in diff) {
                if (currentState == OddsCellState.VISIBLE) showOddsTrend(item.trend)
            }
        }
    }

    private fun showOddsTrend(trendDelta: Int?) {
        with(mBinding) {
            // 先隱藏所有效果
            ivTrendUp.visibility = View.GONE
            ivTrendDown.visibility = View.GONE
            val trendView = when {
                trendDelta == null || trendDelta == 0 -> null
                trendDelta < 0 -> ivTrendDown   // 賠率下降 → 變差
                trendDelta > 0 -> ivTrendUp // 賠率上升 → 變好
                else -> null
            }
            // 同步閃爍 trendView 和 overlay
            trendView?.let { trendImage ->
                trendImage.alpha = 1f
                trendImage.visibility = View.VISIBLE

                val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                    duration = 667     //2000毫秒閃3次，每次耗時667毫秒
                    repeatCount = 2
                    addUpdateListener { animation ->
                        val alpha = animation.animatedValue as Float
                        trendImage.alpha = alpha
                    }
                    addListener(onEnd = {
                        trendImage.visibility = View.GONE
                    })
                }
                animator.start()
            }
        }
    }


    private fun updateState(active: Boolean, isSelected: Boolean) {
        currentState = if (active) OddsCellState.VISIBLE else OddsCellState.DEACTIVATED
        with(mBinding) {
            root.visibility =  View.VISIBLE
            tvShortName.visibility = if (active) View.VISIBLE else View.GONE
            tvOdds.visibility = if (active) View.VISIBLE else View.GONE
            ivLock.visibility = if (active) View.GONE else View.VISIBLE
            llOddsCell.isEnabled = active
            if (active) {
                llOddsCell.isSelected = isSelected
            } else {
                llOddsCell.isSelected = false
            }
        }
    }

    fun deActivate() {
        currentState = OddsCellState.DEACTIVATED
        with(mBinding) {
            root.visibility = View.VISIBLE
            tvShortName.visibility = View.GONE
            tvOdds.visibility = View.GONE
            ivLock.visibility = View.VISIBLE
            llOddsCell.isEnabled = false
        }
    }

    fun hideView() {
        currentState = OddsCellState.DEACTIVATED
        mBinding.root.visibility = View.GONE
    }

    fun isDeactivated(): Boolean {
        return currentState == OddsCellState.DEACTIVATED
    }
}
