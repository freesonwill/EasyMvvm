package arch.cayenne.module.chat.utils

import android.text.SpannableStringBuilder
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.order.data.model.BetShareBean
import com.google.gson.Gson

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

    /***
     * 占位符类型
     *     @用户：[**]
     *     分享：[***]
     *     表情：/id=xx/
     *     大图：/bid=xx/
     * */
    fun createContent(spannable: SpannableStringBuilder, spans: Array<MentionSpan>): String {
        var newStr = spannable.replace(Regex("@\\w+\\s?"), "[**]").replace("\u2060", "")
        val sharSpan =
            spans.find { it.msgType == ChatMsgType.BET_SPORT || it.msgType == ChatMsgType.BET_GAME }
        sharSpan?.let {
            newStr = newStr.replace(it.tv, "[***]")
        }
        return newStr
    }

    fun createExtraData(bean: BetShareBean? = null, spans: Array<MentionSpan>): Any? {
        val sharSpan =
            spans.find { it.msgType == ChatMsgType.BET_SPORT || it.msgType == ChatMsgType.BET_GAME }
        val map = HashMap<String, String>()
        spans?.let {
            map.put("userId", bean?.userId?.toString() ?: "")
            map.put("settleId", bean?.settleId?.toString() ?: "")
            map.put("gameType", bean?.gameType?.toString() ?: "")
            map.put("validBetScore", bean?.validBetScore?.toString() ?: "")
            map.put("winScore", bean?.winScore?.toString() ?: "")
            map.put("multi", bean?.multi?.toString() ?: "")
            map.put("roomName", bean?.roomName?.toString() ?: "")
            map.put("ccy", bean?.ccy?.toString() ?: "")
            map.put("settleTime", bean?.settleTime?.toString() ?: "")
            map.put("content", bean?.content?.toString() ?: "")
        }
        return sharSpan?.let { map } ?: null
    }

    fun createUserInfo(spans: Array<MentionSpan>): List<ChatRefUser>? {
        val users =
            spans.filter { it.msgType == ChatMsgType.AT && it.user != null }.map { it.user!! }
                .toList()
        return users.ifEmpty { null }
    }
}