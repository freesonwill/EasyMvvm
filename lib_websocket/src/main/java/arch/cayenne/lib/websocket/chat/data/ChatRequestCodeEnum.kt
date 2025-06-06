package arch.cayenne.lib.websocket.chat.data

/**
 * @author: wenxi
 * @date: 5/6/25 14:48
 * @description:
 */
enum class ChatRequestCodeEnum(val code: Int) {
    SUCCESS(0),
    FAIL(1);

    companion object {
        fun getChatResult(code: Int): ChatRequestCodeEnum? {
            return entries.find { it.code == code }
        }
    }
}