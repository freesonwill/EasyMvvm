package com.walisport.module.live.utils

import android.content.Context
import android.text.Spannable
import com.walisport.module.live.widget.EmojiSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

object EmojiUtils {

    fun replaceEmoji(context: Context, text: Spannable, textSize:Float) {
        val pattern: Pattern = Pattern.compile("\\[[^\\]]+\\]")
        val matcher: Matcher = pattern.matcher(text)

        while (matcher.find()) {
            val foundText = matcher.group()
            if (com.walisport.module.live.data.EmojiEnum.getEmojiMap().containsKey(foundText)) {
                val emojiResId: Int = com.walisport.module.live.data.EmojiEnum.getEmojiMap()[foundText] ?: -1
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