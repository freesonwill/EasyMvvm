package arch.cayenne.module.chat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.chat.databinding.ItemAtLayoutBinding

/**
 * @author: wenxi
 * @date: 19/11/25 15:57
 * @description:
 */
class AtAdapter : BaseAdapter<String, AtAdapter.AtViewHolder, ItemAtLayoutBinding>(object :
    ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}) {

    inner class AtViewHolder(binding: ItemAtLayoutBinding) : BaseViewHolder(binding)

    override fun convertPlus(holder: AtViewHolder, binding: ItemAtLayoutBinding, position: Int) {
        binding.tv.text = getItem(position)
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemAtLayoutBinding {

        return ItemAtLayoutBinding.inflate(inflater,parent,false)
    }

    override fun createViewHolder(binding: ItemAtLayoutBinding, viewType: Int): AtViewHolder {

        return AtViewHolder(binding)
    }
}