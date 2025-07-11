package arch.cayenne.lib.websocket.chat.utils

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author: wenxi
 * @date: 10/7/25 16:26
 * @description:用于缓存聊天信息，防止聊天信息刷新过快
 */
class ChatMessagePool {
    private val _messageQueue = ConcurrentLinkedQueue<String>()
}