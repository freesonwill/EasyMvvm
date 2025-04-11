package com.walisport.module.live.utils

import android.content.Context
import android.text.Spannable
import android.text.TextUtils
import android.util.Log
import com.walisport.lib.base.utils.LogUtils
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.data.EmojiEnum
import com.walisport.module.live.widget.EmojiSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

object EmojiUtils {

    fun replaceEmoji(context: Context, text: Spannable) {
        val pattern: Pattern = Pattern.compile("/id=(\\d+)/")
        val matcher: Matcher = pattern.matcher(text)

        while (matcher.find()) {
            val foundText = matcher.group()
            val id = matcher.group(1)
            if (EmojiEnum.getEmojiMap().containsKey(foundText)) {
                val emojiResId: Int = EmojiEnum.getEmojiMap()[foundText] ?: -1
//                LogUtils.dTag("aaa","fonundText $foundText  resId $emojiResId")
                if (emojiResId == -1) {
                    continue
                }
                var height = 20.dp2px.toFloat()
                var width = height
                if (!id.isNullOrEmpty() && TextUtils.isDigitsOnly(id)) {
                        val number = id.toInt()
                        if (number >= 82) {
                            height = 23.dp2px.toFloat()
                            width = 80.dp2px.toFloat()
                        }
                }

                val span = EmojiSpan(context, emojiResId, height,width)
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