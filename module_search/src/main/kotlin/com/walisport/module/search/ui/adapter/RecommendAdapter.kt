package com.walisport.module.search.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.search.R
import com.walisport.module.search.databinding.ItemRecommendBinding
import com.walisport.module.search.ui.compare.HotWordCompare

class RecommendAdapter(var onClick: ((String) -> Unit?)? = null) :
    BaseAdapter<String, BaseViewHolder, ViewBinding>(
        HotWordCompare()
    ) {

    private var keyword: String? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateMatchKeyword(keyword: String?) {
        this.keyword = keyword
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClick: (String) -> Unit) {
        this.onClick = onClick
    }

    private fun getHighlightedText(context: Context, word: String, keyword: String?): CharSequence {
        if (keyword.isNullOrEmpty()) return word
        val start = word.indexOf(keyword, ignoreCase = true)
        if (start < 0) return word

        return SpannableString(word).apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.hot_word)),
                start,
                start + keyword.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val word = getItem(position)
        if (binding is ItemRecommendBinding) {
            binding.clRoot.updatePadding(
                top = if (position == 0) 0 else binding.clRoot.paddingTop
            )
            binding.tvTitle.text = getHighlightedText(holder.itemView.context, word, keyword)
            holder.itemView.clickNoRepeat {
                onClick?.invoke(word)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return ItemRecommendBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}