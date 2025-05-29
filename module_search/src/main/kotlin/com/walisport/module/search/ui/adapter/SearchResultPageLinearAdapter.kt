package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.walisport.module.search.R
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.databinding.ItemSearchResultLinearBinding
import com.walisport.module.search.ui.compare.SearchResultLinearCompare
import com.walisport.module.search.ui.fragment.SearchResultPageFragment

class SearchResultPageLinearAdapter(private val type: String): BaseAdapter<SearchResultBaseBean, BaseViewHolder, ViewBinding>(
    SearchResultLinearCompare()
) {
    var onItemClick: ((SearchResultBaseBean) -> Unit)? = null

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        with(binding as ItemSearchResultLinearBinding) {
            with(getItem(position)) {
                tvTitle.text = this.name
                Glide.with(holder.itemView.context).load(this.icon)
                    .placeholder(
                        SkinnableResourceManager.getTargetResourceId(
                            holder.itemView.context,
                            if (type == SearchResultPageFragment.TYPE_PLAYER) R.drawable.ic_search_result_player_placeholder
                            else R.drawable.ic_search_result_placeholder
                        )
                    )
                    .into(ivIcon)

                root.setOnClickListener {
                    onItemClick?.invoke(this)
                }
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemSearchResultLinearBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}