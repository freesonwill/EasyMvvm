package arch.cayenne.module.bet.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.ui.fragment.AllInfoDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.locationInWindow
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.ItemComboDetail1Binding
import arch.cayenne.module.bet.databinding.ItemComboDetail2Binding
import arch.cayenne.module.bet.ui.compare.ParameterUICompare
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterUI
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems2
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterUIItem

class ComboDetailAdapter : BaseAdapter<ParameterUIItem, BaseViewHolder, ViewBinding>(ParameterUICompare()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            ParameterUI.TYPE_GROUP -> ItemComboDetail1Binding.inflate(inflater, parent, false)
            ParameterUI.TYPE_CHILD -> ItemComboDetail2Binding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("unknown type: $viewType")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return when (viewType) {
            ParameterUI.TYPE_GROUP -> GroupVH(binding as ItemComboDetail1Binding)
            ParameterUI.TYPE_CHILD -> ChildVH(binding as ItemComboDetail2Binding)
            else -> throw IllegalArgumentException("unknown type: $viewType")
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GroupVH -> holder.bind(item.group!!,position)
            is ChildVH -> holder.bind(item.child!!,item.childIndexInGroup)
        }
    }

    class GroupVH(private val mBinding: ItemComboDetail1Binding) : BaseViewHolder(mBinding) {

        fun bind(bean: ParameterItems,position: Int) {
            with(mBinding) {
                tvTitle.text = bean.title
                val money = bean.items.getOrNull(0)?.moneyStr
                tvTabBet.isVisible = !TextUtils.isEmpty(money)
                tvTabWin.isVisible = !TextUtils.isEmpty(money)
            }
            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            if (position > 0) {
                params.topMargin = 15.5.dp2px
            } else {
                params.topMargin = 0
            }
        }
    }

    class ChildVH(private val mBinding: ItemComboDetail2Binding) : BaseViewHolder(mBinding) {

        fun bind(bean: ParameterItems2, position: Int) {
            with(mBinding) {
                tvTabCombo.text = bean.comboStr.apply {
                    tvTabCombo.clickNoRepeat {
                        showDetailDialog(it, this)
                    }
                }
                tvTabBet.text = bean.moneyStr.apply {
                    tvTabBet.clickNoRepeat {
                        showDetailDialog(it, this)
                    }
                }
                tvTabWin.text = bean.winMoneyStr.apply {
                    tvTabWin.clickNoRepeat {
                        showDetailDialog(it, this)
                    }
                }
                tvTabOdds.text = bean.oddsStr.apply {
                    tvTabOdds.clickNoRepeat {
                        showDetailDialog(it, this)
                    }
                }
                if (position % 2 != 0) {
                    bg.background = null
                } else {
                    bg.background = R.drawable.shape_combination.getDrawable()
                }
            }
        }

        private fun showDetailDialog(view: View, text: String?) {
            if(text.isNullOrBlank()) return
            val location = view.locationInWindow
            val h = ViewUtils.getStatusBarHeight(view.context)
            val positionX = location.first() + view.width / 2
            val positionY = location.last() - h - 1.dp2px
            val fragment = view.findFragment<Fragment>()
            AllInfoDialogFragment.newInstance(
                positionX ,
                positionY ,
                text
            ).show(fragment.parentFragmentManager , TAG)
        }
    }
}


