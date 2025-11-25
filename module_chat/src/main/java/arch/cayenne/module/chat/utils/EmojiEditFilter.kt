package arch.cayenne.module.chat.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.manager.ChatATHelper

/**
 * @author: wenxi
 * @date: 13/6/25 18:22
 * @description: 单独计算表情特殊字符，并限制最大输入长度
 */
class EmojiEditFilter(private val atInput:()->Unit) : InputFilter {


    private val normalEmojiPattern = Regex(EmojiUtils.NORMAL_EMOJI_REGEX)
    private val bidEmojiPattern = Regex(EmojiUtils.BID_EMOJI_REGEX)

    private val maxLength: Int = 50

    @SuppressLint("SetTextI18n")
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        if (source == null) {
            return ""
        }
        "source $source".logd("aaa")
        if(source == "@"){
            atInput.invoke()
        }
        // 计算当前有效长度（普通字符+表情各算1）
        val originalText = dest.toString()
        val originalEffectiveLength = calculateEffectiveLength(originalText)

        // 计算新输入的有效长度
        val newText = StringBuilder(originalText)
            .replace(dstart, dend, source.subSequence(start, end).toString())
            .toString()
        val newEffectiveLength = calculateEffectiveLength(newText)
        // 如果新长度未超过限制，允许输入
        if (newEffectiveLength <= maxLength) {
//            tvSize.text = "$newEffectiveLength/$maxLength"
            return source // 返回null表示接受原始输入
        }

        // 如果完全超出限制，拒绝整个输入
        if (originalEffectiveLength >= maxLength) {
            return ""
        }

        // 部分超出限制，尝试截取可接受的部分
        val remaining = maxLength - originalEffectiveLength
        return when {
            // 如果输入的是普通文本
            !source.toString().contains(normalEmojiPattern) && !source.toString()
                .contains(bidEmojiPattern) -> {
//                tvSize.text = "${remaining+originalEffectiveLength}/$maxLength"
                source.subSequence(start, start + remaining)
            }

            // 如果输入的是表情字符串
            else -> {
                if (remaining >= 1) {
                    // 允许插入一个完整表情
                    val normalEmoji = normalEmojiPattern.find(source)?.value
                    if (normalEmoji != null) {
                        return normalEmoji
                    }
                    val bidEmoji = bidEmojiPattern.find(source)?.value
                    if (bidEmoji != null) {
                        return bidEmoji
                    }
                    ""
                } else {
                    ""
                }
            }
        }
    }

    private fun calculateEffectiveLength(text: String): Int {
        // 将每个表情替换为单个字符后计算长度
        return bidEmojiPattern.replace(normalEmojiPattern.replace(text, " "), " ").length
    }
}