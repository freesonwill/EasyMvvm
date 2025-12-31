package arch.cayenne.module.chat.utils

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.widget.EditText
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.order.data.model.BetShareBean
import com.google.gson.Gson

/**
 * @author: wenxi
 * @date: 28/12/25 22:00
 * @description:
 */
object ChatMsgUtils {
    private val TAG = this.javaClass.simpleName

    /**
     * 由于部分字段在显示的时候会被拆分开，这里在字段中间添加不可见字符，防止被拆分
     * */
    fun addNoDivideCharInBetShar(value: String): String {
        if (value.contains("游戏注单")) {
            return value.replace(
                "#游戏注单:",
                "\u2060游\u2060戏\u2060注\u2060单\u2060:\u2060"
            )
        } else if (value.contains("体育注单")) {
            return value.replace(
                "#体育注单:",
                "\u2060体\u2060育\u2060注\u2060单\u2060:\u2060"
            )
        }
        return value
    }

    fun addNoDivideCharInBetShar1(value: String): String {
        "addNoDivideCharInBetShar ${value.contains("游戏注单")}".logd("aaa")
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
            val replaceTv = it.tv.replace("\u2060", "")
            newStr = newStr.replace(replaceTv, "[***]")
        }
        return newStr
    }

    /**
     * 恢复内容中的占位符
     *  @用户：[**]
     *  分享：[***]
     * */
    fun recoveryContent(
        bean: ChatMsgPageBean
    ): SpannableStringBuilder {
        var spannable = SpannableStringBuilder(bean.content)
        //添加at消息
        bean.refUid?.forEach { uid ->
            val user = bean.refInfos?.get(uid)
            val newChar = "@${user?.userName} "
            val index = spannable.indexOf("[**]")
            val endIndex = index + newChar.length
            spannable = spannable.replace(index, index + 4, newChar)
            val span = MentionSpan(ChatMsgType.AT, bean.userName, bean.refInfos?.get(uid), click = {})
            spannable.setSpan(
                span,
                index,
                endIndex,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
//            "recoveryAtChar $newChar  ${Gson().toJson(bean)} ".logd(TAG)
        }
//        "recoveryAtChar ${Gson().toJson(bean)} ".logd(TAG)

        if (bean.msgType == ChatMsgType.BET_GAME || bean.msgType == ChatMsgType.BET_SPORT) {
            bean.extraData?.let {
             val betShareBean = recoveryExtraDataBetShareBean(bean.extraData)
             val index = spannable.indexOf("[***]")
             val newChar = addNoDivideCharInBetShar(betShareBean?.content ?: "")
             val endIndex = index + newChar.length
             spannable = spannable.replace(index, index + 5, newChar)
             val span = MentionSpan(bean.msgType, newChar, null, click = {})
                spannable.setSpan(
                    span,
                    index,
                    endIndex,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )
//             "recoveryBetShar $newChar ".logd(TAG)
            }
        }
        return spannable

    }


    fun createExtraData(
        bean: BetShareBean? = null,
        spans: Array<MentionSpan>
    ): Map<String, String>? {
        val sharSpan =
            spans.find { it.msgType == ChatMsgType.BET_SPORT || it.msgType == ChatMsgType.BET_GAME }
        var map: HashMap<String, String>? = null
        sharSpan?.let {
            map = HashMap()
            map!!["userId"] = bean?.userId?.toString() ?: ""
            map!!["settleId"] = bean?.settleId ?: ""
            map!!["gameType"] = bean?.gameType?.toString() ?: ""
            map!!["validBetScore"] = bean?.validBetScore?.toString() ?: ""
            map!!["winScore"] = bean?.winScore?.toString() ?: ""
            map!!["multi"] = bean?.multi ?: ""
            map!!["roomName"] = bean?.roomName ?: ""
            map!!["ccy"] = bean?.ccy ?: ""
            map!!["settleTime"] = bean?.settleTime ?: ""
            map!!.put("content", bean?.content ?: "")
        }
        return map
    }

    fun createUserInfo(spans: Array<MentionSpan>): Map<String,ChatRefUser>? {
        val users =
            spans.filter { it.msgType == ChatMsgType.AT && it.user != null }.map {it.user!!.uid to it.user }
                .toMap()
        return users.ifEmpty { null }
    }

    fun recoveryExtraDataBetShareBean(
        extraData: Map<String, String>,
    ): BetShareBean? {
        var betShareBean: BetShareBean? = null
        if (extraData.isNotEmpty()) {
            betShareBean = BetShareBean(
                userId = extraData["userId"]?.toLongOrNull() ?: 0L,
                settleId = extraData["settleId"] ?: "",
                gameType = extraData["gameType"]?.toIntOrNull() ?: 0,
                validBetScore = extraData["validBetScore"]?.toInt() ?: 0,
                winScore = extraData["winScore"]?.toInt() ?: 0,
                multi = extraData["multi"] ?: "",
                roomName = extraData["roomName"] ?: "",
                ccy = extraData["ccy"] ?: "",
                settleTime = extraData["settleTime"] ?: "",
                content = extraData["content"] ?: ""
            )
        }

        return betShareBean
    }

    /*
    *插入分享注单时检查Editext是否有分享注单，如果有的话进行替换
    * */
    fun checkAndReplaceBetShareInEditable(edittext: EditText) {
        val spannable = SpannableStringBuilder(edittext.text)
        val spans = spannable.getSpans(0, edittext.text.length, MentionSpan::class.java)
        spans.find { it.msgType == ChatMsgType.BET_SPORT || it.msgType == ChatMsgType.BET_GAME }
            ?.let {
                edittext.setText(spannable.removeRange(
                    spannable.getSpanStart(it),
                    spannable.getSpanEnd(it)
                ))
            }
    }


}