package arch.cayenne.module.home.ui.viewholder

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.databinding.ItemOddsCellBinding

class OddsCellViewHolder(
    private val mBinding: ItemOddsCellBinding,
    private val onOddsClick: (SelectionBeanLite, Boolean) -> Unit
) : BaseViewHolder(mBinding) {
    private var currentSelection: SelectionBeanLite? = null
    fun bind(item: SelectionBeanLite, selectedId: Long?) {
        currentSelection = item
        with(mBinding) {
            tvShortName.text = item.shortName
            tvOdds.text = item.odds.getOdds()
            llOddsCell.isSelected = item.selectionId == selectedId //<<<< 是否選中
            updateState(item.active)

            llOddsCell.setOnClickListener {
                if (item.active) {
                    val isSelected = !(llOddsCell.isSelected)
                    llOddsCell.isSelected = isSelected
                    onOddsClick(item, isSelected)
                }
            }
        }
    }


    fun bindPayload(item: SelectionBeanLite, payloads: List<Any>, selectedId: Long?) {
        val diff = payloads.firstOrNull() as? Set<*> ?: return
        currentSelection = item

        with(mBinding) {
            if ("odds" in diff) {
                if (tvOdds.text.toString() != item.odds.getOdds()) {
                    tvOdds.text = item.odds.getOdds()
//                    animateOddsChange(tvOdds)
                }
            }

            if ("shortName" in diff) {
                if (tvShortName.text.toString() != item.shortName) {
                    tvShortName.text = item.shortName
                }
            }

            if ("active" in diff) {
                if (item.active) activate() else deActivate()
            }

            if ("parlay" in diff) {
                // 目前沒特別UI變化
            }
            llOddsCell.isSelected = item.selectionId == selectedId
        }
    }
    //    private fun animateOddsChange(view: View) {
//        val anim = ObjectAnimator.ofArgb(
//            view,
//            "backgroundColor",
//            Color.YELLOW, Color.TRANSPARENT
//        )
//        anim.duration = 300
//        anim.start()
//    }
    private fun updateState(active: Boolean) {
        with(mBinding) {
            tvShortName.visibility = if (active) View.VISIBLE else View.GONE
            tvOdds.visibility = if (active) View.VISIBLE else View.GONE
            ivLock.visibility = if (active) View.GONE else View.VISIBLE
            llOddsCell.isEnabled = active
        }
    }

    private fun activate() {
        with(mBinding) {
            tvShortName.visibility = View.VISIBLE
            tvOdds.visibility = View.VISIBLE
            ivLock.visibility = View.GONE
            llOddsCell.isEnabled = true
        }
    }

    fun deActivate() {
        with(mBinding) {
            tvShortName.visibility = View.GONE
            tvOdds.visibility = View.GONE
            ivLock.visibility = View.VISIBLE
            llOddsCell.isEnabled = false
        }
    }
    fun hideView() {
        mBinding.root.visibility = View.GONE
    }
}
