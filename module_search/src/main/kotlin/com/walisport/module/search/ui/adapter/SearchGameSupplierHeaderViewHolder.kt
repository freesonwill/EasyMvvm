package com.walisport.module.search.ui.adapter

import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.search.data.model.SearchGameSupplierListItem
import com.walisport.module.search.databinding.ItemSupplierHeaderBinding
import com.walisport.module.search.R

/**
 * Search 模組專用的供應商標題 ViewHolder，用於 BottomSheet。
 */
class SearchGameSupplierHeaderViewHolder(
    private val mBinding: ItemSupplierHeaderBinding
) : BaseViewHolder(mBinding) {
    fun bind(item: SearchGameSupplierListItem.Header) {
        with(mBinding) {
            if (item.letter == '*') {
                ivHeaderHot.visibility = View.VISIBLE
                tvHeaderName.text = R.string.supplier_section_title_hot.getString()
            } else {
                ivHeaderHot.visibility = View.GONE
                tvHeaderName.text = item.letter.toString()
            }
        }
    }
}

