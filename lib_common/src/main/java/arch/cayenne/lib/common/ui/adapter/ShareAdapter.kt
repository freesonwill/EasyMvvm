package arch.cayenne.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.ShareBean
import arch.cayenne.lib.common.databinding.ItemShareLayoutBinding
import arch.cayenne.lib.common.ui.compare.ShareBeanCompare

class ShareAdapter : BaseAdapter<ShareBean, BaseViewHolder, ItemShareLayoutBinding>(
    ShareBeanCompare()
) {
    private var itemClick:RecyclerItemListener<ShareBean>? = null

    fun setItemListener(listener: RecyclerItemListener<ShareBean>){
        this.itemClick = listener
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemShareLayoutBinding,
        position: Int
    ) {
        binding.tv.text = getItem(position).title
        binding.iv.background = ContextCompat.getDrawable(binding.iv.context,getItem(position).icon)
        binding.root.tag = position
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemShareLayoutBinding {
        return ItemShareLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemShareLayoutBinding,
        viewType: Int
    ): BaseViewHolder {
        binding.root.setOnClickListener {
            itemClick?.onItemClick(null,-1)
        }
        return BaseViewHolder(binding)
    }
}