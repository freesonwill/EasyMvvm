package arch.cayenne.module.home.ui.viewholder

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.home.databinding.ItemOddsCellBinding

class OddsCellViewHolder(
    private val mBinding: ItemOddsCellBinding,
    private val onOddsClick: (SelectionBean) -> Unit
) : BaseViewHolder(mBinding) {
    private var currentSelection: SelectionBean? = null
    fun bind(item: SelectionBean) {
        currentSelection = item
        updateOddsView(item)
    }
    private fun updateOddsView(item: SelectionBean) {
        with(mBinding) {
            tvShortName.text = item.shortName
            tvOdds.text = item.odds.getOdds()
            if (item.active) activate() else deActivate()
//            ivFlashIcon.visibility = View.GONE // 初始關閉閃爍 icon

            llOddsCell.setOnClickListener {
                if (item.active) onOddsClick(item)
            }
        }
    }

    fun bindPayload(item: SelectionBean, payloads: List<Any>) {
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

    fun activate() {
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
