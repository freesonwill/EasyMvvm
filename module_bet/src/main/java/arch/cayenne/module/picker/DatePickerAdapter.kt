package arch.cayenne.module.picker

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.bet.databinding.ItemDateBinding
import arch.cayenne.module.picker.StringCompare

class DatePickerAdapter: BaseAdapter<String, BaseViewHolder, ItemDateBinding>(
    StringCompare()
) {
    override fun convertPlus(holder: BaseViewHolder, binding: ItemDateBinding, position: Int) {
       val title = getItem(position)
        binding.tvTitle.text = title
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemDateBinding {
        return ItemDateBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ItemDateBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}