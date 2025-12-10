package arch.cayenne.module.bet.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems
import arch.cayenne.module.bet.databinding.ItemComboDetailListBinding
import arch.cayenne.module.bet.ui.compare.ComboDetailCompare

class ComboDetailAdapter :
    BaseAdapter<ParameterItems, ComboDetailViewHolder, ItemComboDetailListBinding>(
        ComboDetailCompare()
    ) {
    override fun convertPlus(
        holder: ComboDetailViewHolder,
        binding: ItemComboDetailListBinding,
        position: Int
    ) {
        holder.init(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemComboDetailListBinding {
        return ItemComboDetailListBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemComboDetailListBinding,
        viewType: Int
    ): ComboDetailViewHolder {
        return ComboDetailViewHolder(binding)
    }
}

class ComboDetailViewHolder(private val mBinding: ItemComboDetailListBinding) :
    BaseViewHolder(mBinding) {

    fun init(bean: ParameterItems) {
        with(mBinding) {
            tvTitle.text = bean.title
            val money = bean.items.getOrNull(0)?.money
            tvTabBet.isVisible = !TextUtils.isEmpty(money)
            tvTabWin.isVisible = !TextUtils.isEmpty(money)
            rvContent.layoutManager = LinearLayoutManager(mBinding.root.context)
            rvContent.adapter = CombItemAdapter().apply {
                submitList(bean.items)
            }
        }
    }
}


