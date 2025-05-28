package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultPlayerBeanBean
import com.walisport.module.search.databinding.ItemSearchResultGridHeaderBinding
import com.walisport.module.search.databinding.ItemSearchResultGridItemBinding
import com.walisport.module.search.ui.compare.SearchResultGridCompare

class SearchResultPageGridAdapter: BaseAdapter<SearchResultListItemType, BaseViewHolder, ViewBinding>(
    SearchResultGridCompare()
) {
    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    var onItemClick: ((SearchResultBaseBean) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchResultListItemType.Header -> VIEW_TYPE_HEADER
            is SearchResultListItemType.Item -> VIEW_TYPE_ITEM
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {
                val headerBinding = binding as ItemSearchResultGridHeaderBinding
                val item = getItem(position) as SearchResultListItemType.Header
                headerBinding.tvTitle.text = item.title
            }
            VIEW_TYPE_ITEM -> {
                val itemBinding = binding as ItemSearchResultGridItemBinding
                val itemData = (getItem(position) as SearchResultListItemType.Item).data
                val isPlayer = itemData is SearchResultPlayerBeanBean

                with(itemBinding) {
                    with(itemData) {
                        tvTitle.text = name
                        ivIcon.apply {
                            val size = if (isPlayer) 87.dp2px else 68.dp2px
                            val placeholder =
                                if (isPlayer) R.drawable.ic_search_result_player_placeholder
                                else R.drawable.ic_search_result_placeholder

                            updateLayoutParams {
                                width = size
                                height = size
                            }

                            ConstraintSet().apply {
                                clone(clRoot)
                                clear(ivIcon.id, ConstraintSet.TOP)
                                if (!isPlayer) {
                                    connect(ivIcon.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                                }
                                applyTo(clRoot)
                            }

                            Glide.with(holder.itemView.context).load(itemData.icon)
                                .placeholder(placeholder)
                                .into(itemBinding.ivIcon)
                        }

                        root.setOnClickListener {
                            onItemClick?.invoke(this)
                        }
                    }
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return if(viewType == VIEW_TYPE_HEADER) {
            ItemSearchResultGridHeaderBinding.inflate(inflater, parent, false)
        } else {
            ItemSearchResultGridItemBinding.inflate(inflater, parent, false)
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}