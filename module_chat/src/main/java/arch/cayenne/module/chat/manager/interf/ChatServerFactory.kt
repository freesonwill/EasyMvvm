package arch.cayenne.module.chat.manager.interf

/**
 * @author: wenxi
 * @date: 29/9/25 10:23
 * @description:
 */
interface ChatServerFactory {
   fun connectChatServer()

   fun disconnectChatServer()
}