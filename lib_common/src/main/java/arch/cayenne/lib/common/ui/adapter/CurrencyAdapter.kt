package arch.cayenne.lib.common.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.ItemCurrencyContentBinding
import arch.cayenne.lib.common.databinding.ItemCurrencyTitleBinding
import arch.cayenne.lib.common.ui.viewholder.CurrencyContentViewHolder
import arch.cayenne.lib.common.ui.viewholder.CurrencyTitleViewHolder

class CurrencyAdapter: BaseAdapter<BaseCurrencyData, BaseViewHolder, ViewBinding>(CurrencyCompare()) {

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is BaseCurrencyData.CurrencyTitleData) {
            return CurrencyType.TITLE.ordinal
        } else if (getItem(position) is BaseCurrencyData.CurrencyContentData2) {
            return CurrencyType.CONTENT.ordinal
        } else {
            return super.getItemViewType(position)
        }
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        if (holder is CurrencyContentViewHolder) {
            holder.bind(getItem(position) as? BaseCurrencyData.CurrencyContentData2)
        } else if (holder is CurrencyTitleViewHolder) {
            holder.bind(getItem(position) as? BaseCurrencyData.CurrencyTitleData)
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        if(viewType == CurrencyType.CONTENT.ordinal) {
            return ItemCurrencyContentBinding.inflate(inflater, parent, false)
        } else {
            return ItemCurrencyTitleBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): BaseViewHolder {
        if(viewType == CurrencyType.CONTENT.ordinal) {
            return CurrencyContentViewHolder(binding as ItemCurrencyContentBinding)
        } else {
            return CurrencyTitleViewHolder(binding as ItemCurrencyTitleBinding)
        }
    }
}

class CurrencyCompare: DiffUtil.ItemCallback<BaseCurrencyData>() {
    override fun areItemsTheSame(
        oldItem: BaseCurrencyData,
        newItem: BaseCurrencyData
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: BaseCurrencyData,
        newItem: BaseCurrencyData
    ): Boolean = oldItem == newItem
}

enum class CurrencyType {
    TITLE, CONTENT
}