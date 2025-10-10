package arch.cayenne.module.chat.data.constants

/**
 * @author: wenxi
 * @date: 23/8/25 16:38
 * @description: 键盘切换动作
 */
enum class KeyboardActionType {
     CHAT_TO_EMOJI,
     CHAT_TO_SOFT,
     SOFT_TO_EMOJI,
     EMOJI_TO_SOFT,
     EMOJI_TO_CHAT,
     SOFT_TO_CHAT,
     SOFT_TO_SOFT, //软件盘弹出时，点击back键时使用
     CHAT_TO_CHAT,//初始化的时候调用
     NONE,
}