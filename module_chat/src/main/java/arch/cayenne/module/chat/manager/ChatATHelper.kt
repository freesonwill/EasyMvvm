package arch.cayenne.module.chat.manager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.text.getSpans
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.model.AtBean
import arch.cayenne.module.chat.ui.fragment.ChatUserInfoFragment
import arch.cayenne.module.chat.utils.EmojiEditFilter
import arch.cayenne.module.chat.utils.SearchAtPopupWindow
import java.util.Locale

/**
 * @author: wenxi
 * @date: 18/11/25 16:11
 * @description:
 */
class ChatATHelper(
    private val context: Context,
    private val chatEtInput: EditText,
    childFragmentManager: FragmentManager,
) {
    private val atPattern = "@[^\\s@]+\\s".toRegex()
    val atPopupWindow = SearchAtPopupWindow()
    var closeAtPopup: Boolean = false

    private val atClick: ((str: String) -> Unit) = {
//        ChatUserInfoFragment().show(childFragmentManager)
    }

    private val etInputWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                "beforeTextChanged s $s  start $start count $count after $after".logd("aaa")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                "onTextChanged $s start $start before $before  count $count ".logd("aaa")
            removeMentionSpan(chatEtInput, start, count)
        }

        override fun afterTextChanged(s: Editable?) {
            if (!closeAtPopup) {
                atPopupWindow.dismiss()
            }
            closeAtPopup = false
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun initChatEtInput(locale: Locale?, senText: () -> Unit) {
        //输入拦截
        chatEtInput.filters = arrayOf(EmojiEditFilter() {
            closeAtPopup = true
            atPopupWindow.showPopupWindow(chatEtInput)
        })
        atPopupWindow.createPopupWindow(context, object : RecyclerItemListener<AtBean> {
            override fun onItemClick(item: AtBean?, position: Int) {
                if (item == null) {
                    return
                }
                chatEtInput.apply {
                    closeAtPopup = true
                    text?.let {
                        val start = it.length
                        val name = item.name
                        if (item.isSelect) {
                            if (it.endsWith('@')) {
                                it.append("$name ")
                                filterEtInputWithAt(this, start, name.length + 1)//+空格
                            } else {
                                it.append("@$name ")
                                filterEtInputWithAt(
                                    this,
                                    start,
                                    name.length + 2
                                )//+@ 空格
                            }
                        } else {
                            var indexStart = it.indexOf("@$name ")
                            var indexEnd = indexStart + item.name.length + 2//从0开始，+1 加上空格字符串+1
                            if (indexStart < 0) { //空格被删除的时候
                                indexStart = it.indexOf("@$name")
                                indexEnd = indexStart + item.name.length + 1
                            }
                            if (indexStart >= 0 && indexEnd <= it.length) {
                                it.replace(indexStart, indexEnd, "")
                            }
                        }
                    }
                }

            }
        })
        chatEtInput.apply {
            //设置发送按钮
            imeOptions = EditorInfo.IME_ACTION_SEND
            setImeActionLabel(
                SkinnableResourceManager.getString(
                    context,
                    R.string.live_chat_send,
                    locale
                ), EditorInfo.IME_ACTION_SEND
            )
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    senText.invoke()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            addTextChangedListener(etInputWatcher)
            setEditTextDelCheck(this)
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    // 移除at消息背景
    fun removeMentionSpan(editText: EditText, position: Int, count: Int) {
        val spannable = SpannableStringBuilder(editText.text)
        val spans = spannable.getSpans(position, position + 1, MentionSpan::class.java)
//        "spansSize ${spans.size} position $position".logd("aaa")
        spans.forEach {
            val spanStart = spannable.getSpanStart(it)
            val spanEnd = spannable.getSpanEnd(it)
//            "spanStart $spanStart spanEnd ${spanEnd}".logd("aaa")
            //两个@中间，在后一个@前面插入

            if (position in spanStart + 1..<spanEnd) {
                spannable.removeSpan(it)
                if (spanStart + 1 == position) {
                    val tv = spannable.substring(spanStart+3,spanEnd)
//                    "spanTv 111${tv}1111 ".logd("aaa")
                    val mentionSpan = MentionSpan(tv, atClick)
                    spannable.setSpan(mentionSpan,spanStart+2,spanEnd,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                editText.text = spannable
                editText.setSelection(position)
            }

        }
//         @前面                      @后面一个
//        spansSize 1 position 5   spansSize 1 position 6
//        spanStart 4 spanEnd 10   spanStart 4 spanEnd 10

//        span?.let {
//            val spanStart = spannable.getSpanStart(it)
//            val spanEnd = spannable.getSpanEnd(it)
//            "start $spanStart  end $spanEnd position $position legnth:${spannable.length} position+1:${spannable[position + 1]}".logd(
//                "aaa"
//            )
//
//            if (position in spanStart + 1..<spanEnd) {
//                spannable.removeSpan(it)
//                editText.text = spannable
//                editText.setSelection(position)
//            } else if (position + 1 < spannable.length && spannable[position + 1] == '@') {
//                val removeSpannable = SpannableStringBuilder(editText.text)
//                val removeSpans = removeSpannable.getSpans(position, position+1, MentionSpan::class.java)
//                removeSpans.forEach {span ->
//                   removeSpannable.removeSpan(span)
//                }
//
//
//            }
//        }


//        val range = getAtIndex(editText.text.toString()).find { start in it.first + 1..<it.last }
//        range?.let {
//            val spannable = SpannableStringBuilder(editText.text)
//            val spans = spannable.getSpans(range.first, range.last + count, MentionSpan::class.java)
//            spans.forEach {
////              val spansStart = spannable.getSpanStart(it)
////              val spansEnd = spannable.getSpanEnd(it)
//                spannable.removeSpan(it)
//                editText.text = spannable
//                editText.setSelection(start)
//            }
//        }
    }

    //获取at消息在text中的位置
    private fun getAtIndex(text: String): List<IntRange> {
        val list = atPattern.findAll(text).map {
            it.range
        }.toList()
        return list
    }

    //添加at消息的背景色字体颜色
    fun filterEtInputWithAt(editText: EditText, start: Int, length: Int) {
        val text = editText.text.toString()
        val allLength = start + length
        if (start < 0 || text.length < allLength) {
            return
        }
        val range = IntRange(if (start == 0) 0 else start - 1, allLength)
        val tv = text.substring(range.first + 1, range.last - 1)
        val mentionSpan = MentionSpan(tv, atClick)
        editText.text.setSpan(
            mentionSpan,
            range.first,
            range.last,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
//        editable.clear()
//        val pattern =  "@[^\\s@]+\\s".toRegex()
//        pattern.findAll(text).forEach {matchResult ->
//            val start = matchResult.range.first
//            val end = matchResult.range.last+1
//            val mentionSpan = MentionSpan()
//            editable.setSpan(mentionSpan,start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
    }

    //删除Editext时，检查到有at消息进行三次确认删除
    fun setEditTextDelCheck(editText: EditText) {
        editText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val cursorPositionStart = editText.selectionStart
                val cursorPositionEnd = editText.selectionEnd
                //被选中的字段进都需要自己删除没必要多检查
                if (cursorPositionEnd != cursorPositionStart) {
                    return@setOnKeyListener false
                }

                //字段没有被选中，需要检查是否以at消息结尾
                if (cursorPositionStart > 0) {
                    val spannable = SpannableStringBuilder(editText.text)
                    val spans = spannable.getSpans(0, cursorPositionStart, MentionSpan::class.java)
                    if (spans.isEmpty()) {
                        return@setOnKeyListener false
                    }
                    val lastSpan = spans.last()
                    val spanEnd = spannable.getSpanEnd(lastSpan)
                    val spanStart = spannable.getSpanStart(lastSpan)
                    val lastChar = spannable.elementAt(cursorPositionEnd - 1)
                    "del editext ${spannable.substring(spanStart, spanEnd)}".logd("aaa")
                    if (spanEnd == cursorPositionStart) {
                        if (lastChar == ' ') {
                            return@setOnKeyListener false
                        }
                        editText.setSelection(spanStart, spanEnd)
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
    }

}

class MentionSpan(private val text: String, private val click: ((str: String) -> Unit)) :
    ClickableSpan() {
    private val mentionColor = Color.parseColor("#8FBEE9")
    private val mentionBackgroundColor = Color.parseColor("#4DFE3666")
    val tv = text

    override fun updateDrawState(ds: TextPaint) {
        ds.let {
            it.color = mentionColor
            it.bgColor = mentionBackgroundColor
        }
    }

    override fun onClick(widget: View) {
        click.invoke(text)
    }
}