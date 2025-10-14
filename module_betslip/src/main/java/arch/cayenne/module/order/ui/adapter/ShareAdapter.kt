package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.databinding.ItemShareAppBinding
import arch.cayenne.module.order.data.model.ShareBean
import arch.cayenne.module.order.ui.compare.ShareBeanCompare

class ShareAdapter : BaseAdapter<ShareBean, BaseViewHolder, ItemShareAppBinding>(
    ShareBeanCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemShareAppBinding,
        position: Int
    ) {
        binding.tvTitle.text = getItem(position).title
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemShareAppBinding {
        return ItemShareAppBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemShareAppBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}