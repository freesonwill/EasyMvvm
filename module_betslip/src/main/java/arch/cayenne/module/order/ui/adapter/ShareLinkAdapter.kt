package arch.cayenne.module.order.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.betslip.databinding.ItemShareAppBinding
import arch.cayenne.module.betslip.databinding.ItemShareLinkBinding
import arch.cayenne.module.order.data.model.ShareBean
import arch.cayenne.module.order.ui.compare.ShareBeanCompare
import com.bumptech.glide.Glide

class ShareLinkAdapter : BaseAdapter<ShareBean, BaseViewHolder, ItemShareLinkBinding>(
    ShareBeanCompare()
) {
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemShareLinkBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvTitle.text = item.title
        Glide.with(holder.itemView).load(item.icon).into(binding.ivApp)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemShareLinkBinding {
        return ItemShareLinkBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemShareLinkBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}