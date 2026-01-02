package arch.cayenne.lib.common.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencySettingBinding
import arch.cayenne.lib.common.ui.viewholder.CurrencySettingViewHolder

class CurrencySettingAdapter :
    BaseAdapter<BaseCurrencyData.CurrencyContentData, CurrencySettingViewHolder, ItemCurrencySettingBinding>(
        CurrencySettingCompare()
    ) {
    private var clicklistener: OnItemClickListener? = null

    override fun convertPlus(
        holder: CurrencySettingViewHolder,
        binding: ItemCurrencySettingBinding,
        position: Int
    ) {
        holder.bind(getItem(position), clicklistener)
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

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clicklistener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(bean: BaseCurrencyData.CurrencyContentData)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelect(ccy: String) {
        currentList.forEach{ bean ->
            bean.fiatSelected = bean.ccy == ccy
        }
        notifyDataSetChanged()
    }
}

class CurrencySettingCompare : DiffUtil.ItemCallback<BaseCurrencyData.CurrencyContentData>() {
    override fun areItemsTheSame(
        oldItem: BaseCurrencyData.CurrencyContentData,
        newItem: BaseCurrencyData.CurrencyContentData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: BaseCurrencyData.CurrencyContentData,
        newItem: BaseCurrencyData.CurrencyContentData
    ): Boolean = oldItem == newItem

}