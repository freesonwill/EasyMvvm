package arch.cayenne.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencySettingBinding
import arch.cayenne.lib.common.ui.viewholder.CurrencySettingViewHolder

class CurrencySettingAdapter: BaseAdapter<BaseCurrencyData.CurrencyContentData, CurrencySettingViewHolder, ItemCurrencySettingBinding>(CurrencySettingCompare()) {
    override fun convertPlus(
        holder: CurrencySettingViewHolder,
        binding: ItemCurrencySettingBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCurrencySettingBinding {
        return ItemCurrencySettingBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemCurrencySettingBinding,
        viewType: Int
    ): CurrencySettingViewHolder {
        return CurrencySettingViewHolder(binding)
    }
}

class CurrencySettingCompare: DiffUtil.ItemCallback<BaseCurrencyData.CurrencyContentData>() {
    override fun areItemsTheSame(
        oldItem: BaseCurrencyData.CurrencyContentData,
        newItem: BaseCurrencyData.CurrencyContentData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: BaseCurrencyData.CurrencyContentData,
        newItem: BaseCurrencyData.CurrencyContentData
    ): Boolean = oldItem == newItem

}