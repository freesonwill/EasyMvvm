package arch.cayenne.module.bet.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.data.ParameterItems
import arch.cayenne.module.bet.databinding.ItemCombinationListBinding
import arch.cayenne.module.bet.ui.compare.CombinationCompare

class CombinationAdapter :
    BaseAdapter<ParameterItems, CombinationViewHolder, ItemCombinationListBinding>(
        CombinationCompare()
    ) {
    override fun convertPlus(
        holder: CombinationViewHolder,
        binding: ItemCombinationListBinding,
        position: Int
    ) {
        holder.init(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCombinationListBinding {
        return ItemCombinationListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemCombinationListBinding,
        viewType: Int
    ): CombinationViewHolder {
        return CombinationViewHolder(binding)
    }
}

class CombinationViewHolder(private val mBinding: ItemCombinationListBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: ParameterItems) {
        with(mBinding) {
            tvTitle.text = bean.title
            val money = bean.items[0].money
            tvTabBet.isVisible = !TextUtils.isEmpty(money)
            tvTabWin.isVisible = !TextUtils.isEmpty(money)
            rvContent.layoutManager = LinearLayoutManager(mBinding.root.context)
            rvContent.adapter = CombItemAdapter().apply {
                submitList(bean.items)
            }
        }
    }
}


