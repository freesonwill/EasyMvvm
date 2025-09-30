package arch.cayenne.module.chat.utils

import android.content.Context
import android.text.Spannable
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import java.util.regex.Matcher
import java.util.regex.Pattern

object EmojiUtils {
     const val NORMAL_EMOJI_REGEX = "/id=(\\d+)/"
     const val BID_EMOJI_REGEX = "/bid=(\\d+)/"
    fun replaceEmoji(context: Context, text: Spannable) {
        val emojiPattern: Pattern = Pattern.compile(NORMAL_EMOJI_REGEX)
        val emojiMatcher: Matcher = emojiPattern.matcher(text)
        while (emojiMatcher.find()) {
            val foundText = emojiMatcher.group()
            val id = emojiMatcher.group(1)
            if (arch.cayenne.module.chat.data.constants.EmojiEnum.getEmojiMap().containsKey(foundText)) {
                val emojiResId: Int = arch.cayenne.module.chat.data.constants.EmojiEnum.getEmojiMap()[foundText] ?: -1
                if (emojiResId == -1) {
                    continue
                }
                val width = 20.dp2px.toFloat()
                val span =
                    arch.cayenne.module.chat.ui.widget.EmojiSpan(context, emojiResId, width, width)
                text.setSpan(
                    span,
                    emojiMatcher.start(),
                    emojiMatcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        bidReplaceEmoji(context, text)
    }

    private fun bidReplaceEmoji(context: Context, text: Spannable) {
        val bidPattern: Pattern = Pattern.compile(BID_EMOJI_REGEX)
        val bitMatcher: Matcher = bidPattern.matcher(text)

        while (bitMatcher.find()) {
            val foundText = bitMatcher.group()
            val id = bitMatcher.group(1)
            if (arch.cayenne.module.chat.data.constants.BidEmojiEnum.getEmojiMap().containsKey(foundText)) {
                val emojiResId: Int = arch.cayenne.module.chat.data.constants.BidEmojiEnum.getEmojiMap()[foundText] ?: -1
                if (emojiResId == -1) {
                    continue
                }
                val width = 80.dp2px.toFloat()
                val height = 23.dp2px.toFloat()
                val span =
                    arch.cayenne.module.chat.ui.widget.EmojiSpan(context, emojiResId, height, width)
                text.setSpan(
                    span,
                    bitMatcher.start(),
                    bitMatcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

}