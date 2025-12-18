package com.walisport.module.search.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.search.R
import com.walisport.module.search.databinding.ItemRecommendBinding
import com.walisport.module.search.ui.compare.HotWordCompare
import java.util.Locale

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

    private fun getHighlightedText(context: Context, word: String, keyword: String?): CharSequence {
        if (keyword.isNullOrEmpty()) return word

        val spannable = SpannableString(word)
        val lowerWord = word.lowercase(Locale.getDefault())
        val lowerKeyword = keyword.lowercase(Locale.getDefault())

        // 規則1: 精確匹配
        if (lowerWord == lowerKeyword) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.hot_word)),
                0,
                keyword.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        }

        // 規則1.5: 前綴匹配
        if (lowerWord.startsWith(lowerKeyword)) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.hot_word)),
                0,
                keyword.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        } else if (lowerKeyword.startsWith(lowerWord)) {
            spannable.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.hot_word)),
                0,
                word.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        }

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.hot_word))

        // 規則2.1: 模糊匹配 - 連續出現（允許中間有少量間隔）
        val fuzzyContinuousRanges = findFuzzyContinuousMatch(lowerWord, lowerKeyword)
        if (fuzzyContinuousRanges.isNotEmpty()) {
            fuzzyContinuousRanges.forEach { range ->
                spannable.setSpan(
                    colorSpan,
                    range.first,
                    range.last + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spannable
        }

        // 規則2.2: 模糊匹配 - 不連續出現（依序字符匹配）
        val charMatchRanges = findCharSequenceMatch(lowerWord, lowerKeyword)
        if (charMatchRanges.isNotEmpty()) {
            charMatchRanges.forEach { range ->
                spannable.setSpan(
                    colorSpan,
                    range.first,
                    range.last + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spannable
        }

        // 規則4: 分詞匹配
        val tokens = segmentKeyword(keyword)
        if (tokens.size > 1) {
            val allTokensFound = tokens.all { token ->
                lowerWord.contains(token.lowercase(Locale.getDefault()), ignoreCase = true)
            }
            if (allTokensFound) {
                tokens.forEach { token ->
                    var searchFrom = 0
                    while (true) {
                        val tokenLower = token.lowercase(Locale.getDefault())
                        val index = lowerWord.indexOf(tokenLower, startIndex = searchFrom)
                        if (index < 0) break
                        spannable.setSpan(
                            colorSpan,
                            index,
                            index + token.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        searchFrom = index + 1
                    }
                }
                return spannable
            }
        }

        // 規則7: 單字兜底匹配
        if (keyword.length > 1) {
            val singleCharMatches = findSingleCharMatches(lowerWord, lowerKeyword)
            if (singleCharMatches.isNotEmpty()) {
                singleCharMatches.forEach { range ->
                    spannable.setSpan(
                        colorSpan,
                        range.first,
                        range.last + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                return spannable
            }
        }

        return word
    }

    /**
     * 分詞：將關鍵字拆解為多個語義單元
     * 支援的分隔符：空格、vs、VS、對陣等
     * 對於沒有明顯分隔符的中文關鍵詞，拆解為單字進行匹配
     */
    private fun segmentKeyword(keyword: String): List<String> {
        if (keyword.isBlank()) return emptyList()
        
        val separators = Regex("\\s+|\\s*[vV][sS]\\s*|\\s*[vV]\\.?[sS]\\.?\\s*|對陣|VS", RegexOption.IGNORE_CASE)
        val tokens = keyword.split(separators)
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        if (tokens.size == 1 && keyword.length > 1) {
            val hasSeparator = separators.containsMatchIn(keyword)
            if (!hasSeparator) {
                return keyword.map { it.toString() }
            }
        }
        
        return tokens.ifEmpty { listOf(keyword) }
    }

    /**
     * 模糊連續匹配：尋找關鍵字在文字中的連續出現位置（允許中間有少量字符）
     * 最多允許1個字符的間隔，用於處理字符相對連續的情況
     */
    private fun findFuzzyContinuousMatch(text: String, keyword: String): List<IntRange> {
        if (keyword.isEmpty() || keyword.length > text.length) return emptyList()
        
        val maxGap = 1
        
        for (start in 0..(text.length - keyword.length)) {
            val matchRanges = mutableListOf<IntRange>()
            var keywordIndex = 0
            var textIndex = start
            var lastMatchIndex = -1
            
            while (keywordIndex < keyword.length && textIndex < text.length) {
                if (text[textIndex] == keyword[keywordIndex]) {
                    val gap = if (lastMatchIndex >= 0) textIndex - lastMatchIndex - 1 else 0
                    if (lastMatchIndex >= 0 && gap > maxGap) {
                        break
                    }
                    matchRanges.add(textIndex..textIndex)
                    lastMatchIndex = textIndex
                    keywordIndex++
                    if (keywordIndex == keyword.length) {
                        return matchRanges
                    }
                }
                textIndex++
                
                if (textIndex - start > keyword.length * 3) {
                    break
                }
            }
        }
        return emptyList()
    }

    /**
     * 依序字符匹配：關鍵字的每個字符在文字中依序出現（不要求連續）
     */
    private fun findCharSequenceMatch(text: String, keyword: String): List<IntRange> {
        val matchRanges = mutableListOf<IntRange>()
        var searchFrom = 0
        
        for (char in keyword) {
            val index = text.indexOf(char, startIndex = searchFrom)
            if (index == -1) {
                return emptyList()
            }
            matchRanges.add(index..index)
            searchFrom = index + 1
        }
        
        return matchRanges
    }

    /**
     * 單字兜底匹配：將關鍵字拆解為單字，匹配含任意單字的內容
     */
    private fun findSingleCharMatches(text: String, keyword: String): List<IntRange> {
        val matchRanges = mutableListOf<IntRange>()
        
        for (char in keyword) {
            var searchFrom = 0
            while (true) {
                val index = text.indexOf(char, startIndex = searchFrom)
                if (index < 0) break
                
                if (matchRanges.none { it.first == index }) {
                    matchRanges.add(index..index)
                }
                searchFrom = index + 1
            }
        }
        
        return matchRanges
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        val word = getItem(position)
        if (binding is ItemRecommendBinding) {
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