package arch.cayenne.module.chat.manager

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.lifecycle.LifecycleCoroutineScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.R
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.data.model.MentionSpan
import arch.cayenne.module.chat.utils.EmojiEditFilter
import arch.cayenne.module.chat.utils.SearchAtPopupWindow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * @author: wenxi
 * @date: 18/11/25 16:11
 * @description:
 */
class ChatATHelper(
    private val scope: LifecycleCoroutineScope,
    private val context: Context,
    private val chatEtInput: EditText,
) {
    private val TAG = ChatATHelper::class.java.simpleName
    private val atPattern = "@[^\\s@]+\\s".toRegex()
    private val atPopupWindow = SearchAtPopupWindow()

    //    private var closeAtPopup: Boolean = false
    //用于监听edit输入
    private var editInputListenJob: Job? = null

    //记录开始输入@时光标位置
    var startInputPosition: Int = -1

    //判断输入框是否删除
    var isEditDelete: Boolean = false

    //是否at输入
    var isAtInput: Boolean = false

    // 由于输入框@按下后需要弹出@弹框，为了@弹框位置正确，等软件盘弹出后在弹出@弹框
    var shouldOpenAtDialog: Boolean = false

    var etWatchListen: ((edit: Editable?) -> Unit)? = null


    private val atClick: ((str: String) -> Unit) = {}

    private val etInputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            isEditDelete = before > 0 && count == 0
            removeMentionSpan(chatEtInput, start, count)
        }

        override fun afterTextChanged(s: Editable?) {
//            if (!closeAtPopup) {
//                atPopupWindow.dismiss()
//            }
//            closeAtPopup = false

//            if (startInputPosition == -1 && !isAtInput && !isEditDelete) {
//                atPopupWindow.dismiss()
//            }
//            if (!isEditDelete && startInputPosition >= 0) {
//                listenEditInput()
//            }
//            isAtInput = false
            etWatchListen?.invoke(s)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun initChatEtInput(locale: Locale?, senText: () -> Unit) {
        //输入拦截
        chatEtInput.filters = arrayOf(EmojiEditFilter(atInput = {
            startInputPosition = chatEtInput.selectionStart
//            atPopupWindow.showPopupWindow(chatEtInput)
        }))
//        atPopupWindow.createPopupWindow(context, object : RecyclerItemListener<AtBean> {
//            override fun onItemClick(item: AtBean?, position: Int) {
//                if (item == null) {
//                    return
//                }
////                addAtMentionSpan(item)
//                startInputPosition = -1//选择at后继续输入，at弹框关闭
//            }
//        })
//        atPopupWindow.addDismissListener {
//            startInputPosition = -1
//        }
        chatEtInput.apply {
            //设置发送按钮
            imeOptions = EditorInfo.IME_ACTION_SEND
            setImeActionLabel(
                SkinnableResourceManager.getString(
                    context, R.string.live_chat_send, locale
                ), EditorInfo.IME_ACTION_SEND
            )
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    senText.invoke()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
            setEditTextDelCheck(this)
//            movementMethod = LinkMovementMethod.getInstance() //moentionSpan 点击
        }
    }

//    fun addAtMentionSpan(item: AtBean) {
//        isAtInput = true
//        chatEtInput.apply {
//            text?.let {
//                if (atPopupWindow.isSearchIng && startInputPosition >= 0 && selectionStart > startInputPosition) {
//                    it.replace(startInputPosition, selectionStart, "")
//                }
//                var nStart: Int = -1
//                var atStrLength = -1
//                if (item.isSelect) {
//                    var atStr = ""
//                    if (selectionStart > 0 && it[selectionStart - 1] == '@') {
//                        nStart = selectionStart - 1 //光标在@后面
//                        atStr = "${item.name} "
//                        atStrLength = atStr.length + 1 //少了个@
//                    } else {
//                        atStr = "@${item.name} "
//                        nStart = selectionStart
//                        atStrLength = atStr.length
//                    }
//                    it.insert(selectionStart, atStr)
//                    addSpecialMentionSpan(ChatMsgType.AT,this, item.name, nStart, atStrLength)//+ @ 空格
//                } else {
//                    var indexStart = it.indexOf("@${item.name} ")
//                    var indexEnd = indexStart + item.name.length + 2//从0开始，+1 加上空格字符串+1
//                    if (indexStart < 0) { //空格被删除的时候
//                        indexStart = it.indexOf("@${item.name}")
//                        indexEnd = indexStart + item.name.length + 1
//                    }
//                    if (indexStart >= 0 && indexEnd <= it.length) {
//                        it.replace(indexStart, indexEnd, "")
//                    }
//                }
//            }
//        }
//        if (atPopupWindow.isSearchIng) {
//            atPopupWindow.isSearchIng = false
//            dismissWindow()
//        }
//    }


    fun addAtMentionSpan(name: String, user: ChatRefUser) {
        chatEtInput.apply {
            text?.let {
                if (atPopupWindow.isSearchIng && startInputPosition >= 0 && selectionStart > startInputPosition) {
                    it.replace(startInputPosition, selectionStart, "")
                }
                var nStart: Int = -1
                var atStrLength = -1
                val atStr = "@${name} "

                nStart = selectionStart
                atStrLength = atStr.length
                it.insert(selectionStart, atStr)
                addSpecialMentionSpan(ChatMsgType.AT, this, name, user, nStart, atStrLength)//+ @ 空格
            }
        }
    }

    fun addShareBetSpan(betStr: String, msgType: ChatMsgType) {
        chatEtInput.apply {
            text?.let {
                val nStart: Int = selectionStart
                val betStrLength = betStr.length

                it.insert(selectionStart, betStr)
                addSpecialMentionSpan(msgType, this, betStr, null, nStart, betStrLength)//+ @ 空格
            }
        }
    }

//    fun addAtInEt() {
//        chatEtInput.apply {
//            text.insert(selectionStart, "@")
//        }
//    }

    // 移除at消息背景
    fun removeMentionSpan(editText: EditText, position: Int, count: Int) {
        var spannable = SpannableStringBuilder(editText.text)
        val spans = spannable.getSpans(position, position + 1, MentionSpan::class.java)
        spans.forEach { mention ->
            val spanStart = spannable.getSpanStart(mention)
            val spanEnd = spannable.getSpanEnd(mention)
            "removeMentionSpan position $position spanStart $spanStart spanEnd $spanEnd count $count ".logd(
                TAG
            )
            //两个@中间，在后一个@前面插入
            if (position in spanStart..<spanEnd) {
                spannable.removeSpan(mention)
                val checkLastIndex = position + count
                if (checkLastIndex >= spanEnd) {
                    return
                }

                //检查是否在空字符串前面加的字符 如果是重新设置需要添加背景色
                if (spannable[checkLastIndex] == ' ') {
                    val mentionSpan = MentionSpan(mention.msgType, "", null, atClick)
                    spannable.setSpan(
                        mentionSpan,
                        spanStart,
                        position,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable = spannable.replace(checkLastIndex,checkLastIndex+1,"")
                }

                editText.text = spannable
                editText.setSelection(position)
            }

        }
    }

    //获取at消息在text中的位置
    private fun getAtIndex(text: String): List<IntRange> {
        val list = atPattern.findAll(text).map {
            it.range
        }.toList()
        return list
    }

    //添加at消息的背景色字体颜色
    fun addSpecialMentionSpan(
        type: ChatMsgType,
        editText: EditText,
        name: String,
        user: ChatRefUser?,
        start: Int,
        length: Int
    ) {
        val text = editText.text.toString()
        val allLength = start + length
        if (start < 0 || text.length < allLength) {
            return
        }
        val mentionSpan = MentionSpan(type, name, user, atClick)
        editText.text.setSpan(
            mentionSpan, start, allLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    //删除Editext时，检查到有at消息进行三次确认删除
    fun setEditTextDelCheck(editText: EditText) {
        editText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val cursorPositionStart = editText.selectionStart
                val cursorPositionEnd = editText.selectionEnd
                //被选中的字段进都需要自己删除没必要多检查
                if (cursorPositionEnd != cursorPositionStart) {
//                    "del1  cursorPositionStart $cursorPositionStart cursorPositionEnd $cursorPositionEnd".logd("aaa")
                    val spannable = SpannableStringBuilder(editText.text)
                    val spans = spannable.getSpans(
                        cursorPositionStart, cursorPositionEnd, MentionSpan::class.java
                    )
                    if (spans.size == 1) {//在一个at消息中多选删除时，先全选，如果是全选就直接删除
                        val spanStart = spannable.getSpanStart(spans[0])
                        val spanEnd = spannable.getSpanEnd(spans[0])
                        if (spanStart != cursorPositionStart || spanEnd != cursorPositionEnd) {
                            editText.setSelection(spanStart, spanEnd)
                            return@setOnKeyListener true
                        }
                    }
                    val list: List<String> = spans.map {
                        editText.text.removeSpan(it)
                        it.tv
                    }.toList()
                    atPopupWindow.deleteAdapterSelect(list)
                    return@setOnKeyListener false
                }

                //字段没有被选中，需要检查是否以at消息结尾
                if (cursorPositionStart > 0) {
                    val spannable = SpannableStringBuilder(editText.text)
                    val spans = spannable.getSpans(
                        cursorPositionStart - 1, cursorPositionStart, MentionSpan::class.java
                    )

                    if (spans.isEmpty()) {
                        return@setOnKeyListener false
                    }
                    val lastSpan = spans.last()
                    val spanEnd = spannable.getSpanEnd(lastSpan)
                    val spanStart = spannable.getSpanStart(lastSpan)
//                 "cursorPositionStart $cursorPositionStart  spanStart $spanStart spanEnd $spanEnd".logd("aaa")
                    if (cursorPositionStart in spanStart until spanEnd) {
                        //光标在at消息中间，选中整个at消息
                        editText.setSelection(spanStart, spanEnd)
                        return@setOnKeyListener true
                    }

                    if (cursorPositionStart == spanEnd) {
                        val lastChar = spannable.elementAt(spanEnd - 1)
                        if (lastChar == ' ') {
//                            if (cursorPositionStart == spanEnd) { //如果光标正常的在@消息后面删除，正常对待，检查lastChar是否空字符
                            return@setOnKeyListener false
//                            } else { //如果光标在@消息中间，检查@消息后一位是否空字符
//                                editText.text.replace(spanEnd - 1, spanEnd, "")
//                                editText.setSelection(spanEnd - 1)
//                                return@setOnKeyListener true
//                            }
                        }
                        editText.setSelection(spanStart, spanEnd)
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
    }

    /**
     * 监听EditText输入。停止输入后截取开始输入到停止输入的字符串
     * 判断是否显示@弹框
     * */
    fun listenEditInput() {
        if (editInputListenJob?.isActive == true) {
            editInputListenJob?.cancel()
        }

        editInputListenJob = scope.launch {
            delay(500)
            val cursorPosition = chatEtInput.selectionStart
            var inputStr = ""

            if (startInputPosition >= 0 && cursorPosition <= chatEtInput.text.length) {
                inputStr = chatEtInput.text.substring(
                    startInputPosition, cursorPosition
                )
            }
            if (inputStr.length > 1 && inputStr.startsWith("@")) {
                atPopupWindow.searchAtList(inputStr)
            }
        }
    }

    fun addTextWatcher() {
        chatEtInput.addTextChangedListener(etInputWatcher)
    }

    fun removeTextWatcher() {
        chatEtInput.removeTextChangedListener(etInputWatcher)
    }


    /**
     * 发送出去的at消息，格式按照
     * Params: atPattern - at消息格式
     *
     * */
//    fun atTextAddSpace(editable: Editable):SpannableStringBuilder{
//        val spannable = SpannableStringBuilder(editable)
//        var spans = spannable.getSpans(0, editable.length, MentionSpan::class.java)
//        var canAddSpace = spans.isNotEmpty()
//        while (canAddSpace){
//            for (i in spans.indices){
//
//                val span = spans[i]
//                val end = spannable.getSpanEnd(span)
//                val lastChar = spannable[end]
//                if(lastChar != ' '){
//                    spannable.insert(end," ")
//                    spans = spannable.getSpans(0,spannable.length,MentionSpan::class.java)
//                    break
//                }
//             if(i == spans.size -1){
//                 canAddSpace = false
//             }
//            }
//        }
//        return spannable
//
//    }


    fun dismissWindow() {
        atPopupWindow.dismiss()
    }

}
