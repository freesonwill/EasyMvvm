package arch.cayenne.module.chat.utils

/**
 * @author: wenxi
 * @date: 28/12/25 22:00
 * @description:
 */
object ChatMsgUtils {

    /**
     * 由于部分字段在显示的时候会被拆分开，这里在字段中间添加不可见字符，防止被拆分
     * */
    fun addNoDivideCharInBetShar(value: String): String {
        if (value.contains("游戏注单")) {
            return value.replace(
                "游戏注单:",
                "\u2060游\u2060戏\u2060注\u2060单\u2060:\u2060"
            )
        } else if (value.contains("体育注单")) {
            return value.replace(
                "体育注单:",
                "\u2060体\u2060育\u2060注\u2060单\u2060:\u2060"
            )
        }
        return value
    }

}