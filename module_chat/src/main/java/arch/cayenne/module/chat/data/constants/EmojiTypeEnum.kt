package arch.cayenne.module.chat.data.constants

/**
 * @author: wenxi
 * @date: 3/6/25 16:33
 * @description:
 */
enum class EmojiTypeEnum(val value: Int) {
    NORMAL(0),
    BID(1);

    companion object {
        fun getEnum(value: Int): EmojiTypeEnum {
            return when (value) {
                0 -> NORMAL
                1 -> BID
                else -> NORMAL
            }
        }
    }
}