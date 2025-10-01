package arch.cayenne.module.chat.data.model

import arch.cayenne.module.chat.data.constants.EmojiTypeEnum

/**
 * @author: wenxi
 * @date: 4/6/25 09:53
 * @description:
 */
data class SoftData(val emojiType: EmojiTypeEnum, val emojis:List<EmojiData>) {
}