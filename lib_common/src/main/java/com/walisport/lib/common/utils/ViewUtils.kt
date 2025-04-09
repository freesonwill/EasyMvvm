package com.walisport.lib.common.utils

import android.content.Context
import android.content.res.Resources
import android.text.Spannable
import android.util.TypedValue
import com.walisport.lib.common.data.EmojiEnum
import com.walisport.lib.common.widget.EmojiSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

object ViewUtils {
    fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )
    }

     fun replaceEmoji(context:Context,text: Spannable, textSize:Float) {
        val pattern: Pattern = Pattern.compile("\\[[^\\]]+\\]")
        val matcher: Matcher = pattern.matcher(text)

        while (matcher.find()) {
            val foundText = matcher.group()
            if (EmojiEnum.getEmojiMap().containsKey(foundText)) {
                val emojiResId: Int = EmojiEnum.getEmojiMap()[foundText] ?: -1
                if (emojiResId == -1) {
                    continue
                }
                val span = EmojiSpan(context, emojiResId, textSize)
                text.setSpan(
                    span,
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

    }
}