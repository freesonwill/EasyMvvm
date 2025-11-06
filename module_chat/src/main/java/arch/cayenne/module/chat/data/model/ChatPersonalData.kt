package arch.cayenne.module.chat.data.model

/**
 * @author: wenxi
 * @date: 6/11/25 10:48
 * @description:  type = 1 显示标题栏 0 显示正常文字
 */
data class ChatPersonalData(val text:String,val type:Int = 0) {
}