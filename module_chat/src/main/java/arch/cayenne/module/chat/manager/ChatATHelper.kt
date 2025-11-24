package arch.cayenne.module.chat.manager

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

/**
 * @author: wenxi
 * @date: 18/11/25 16:11
 * @description:
 */
object ChatATHelper {
    private val atPattern = "@[^\\s@]+\\s".toRegex()

    private val atClick: ((str: String) -> Unit) = {
        "atClick $it".logd("aaa")
    }


    fun removeMentionSpan(editText: EditText, start: Int, count: Int) {
        val range = getAtIndex(editText.text.toString()).find { start in it.first + 1..<it.last }
        range?.let {
            val spannable = SpannableStringBuilder(editText.text)
            val spans = spannable.getSpans(range.first, range.last + count, MentionSpan::class.java)
            spans.forEach {
//              val spansStart = spannable.getSpanStart(it)
//              val spansEnd = spannable.getSpanEnd(it)
//              if(spansStart != -1 && spansEnd != -1){
                spannable.removeSpan(it)
                editText.text = spannable
                editText.setSelection(start)
//              }
            }
        }
    }

    private fun getAtIndex(text: String): List<IntRange> {
        val list = atPattern.findAll(text).map {
            it.range
        }.toList()
        return list
    }


    fun filterEtInputWithAt(editText: EditText, start: Int, length: Int) {
        val text = editText.text.toString()
        val allLength = start + length
        if (start < 0 || text.length < allLength) {
            return
        }
        val range = IntRange(if (start == 0) 0 else start - 1, allLength)
        val mentionSpan = MentionSpan(text.substring(range.first, range.last), atClick)
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

    fun setEditTextDelCheck(editText: EditText) {

        editText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val cursorPositionStart = editText.selectionStart
                val cursorPositionEnd = editText.selectionEnd
                //被选中的字段进都需要自己删除没必要多检查
                if (cursorPositionEnd != cursorPositionStart) {
//                    val textBeforeCursor = editText.text.substring(0, cursorPositionEnd)
//                    val matchesBeforeCursor = atPattern.findAll(textBeforeCursor)
//                    if (!matchesBeforeCursor.none()) {
//                        val lastMatches = matchesBeforeCursor.last()
//                        "删除 cursorPosition $cursorPositionStart selectionEnd ${cursorPositionEnd} textBeforeCursor $textBeforeCursor lastMatch ${lastMatches.range.first} ${lastMatches.range.last}".logd("aaa")
//                        //已被全选的at优先删除
//                        if (cursorPositionStart == lastMatches.range.first && cursorPositionEnd == lastMatches.range.last + 1) {
//                        }
//                    }
                    return@setOnKeyListener false
                }

               //字段没有被选中，需要检查是否以at消息结尾
                if (cursorPositionStart > 0) {
                    val spannable = SpannableStringBuilder(editText.text)
                    val spans = spannable.getSpans(0, cursorPositionStart, MentionSpan::class.java)
                    if(spans.isEmpty()){
                        return@setOnKeyListener false
                    }
                    val lastSpan = spans.last()
                    val spanEnd = spannable.getSpanEnd(lastSpan)
                    val spanStart = spannable.getSpanStart(lastSpan)
                    val lastChar = spannable.elementAt(cursorPositionEnd-1)
//                    "spans ${spans.size} lastChar $lastChar cursorPositionStart $cursorPositionStart  spanStart ${spanStart}   spanEnd ${spanEnd}".logd("aaa")
                    if(spanEnd == cursorPositionStart){
                        if(lastChar == ' '){
                            return@setOnKeyListener false
                        }
                        editText.setSelection(spanStart,spanEnd)
                        return@setOnKeyListener true
                    }


//                    val textBeforeCursor = editText.text.substring(0, cursorPositionStart)
//                    val matchesBeforeCursor = atPattern.findAll(textBeforeCursor)
//                    val lastMatches = matchesBeforeCursor.last()
//                    "matchesBeforeCursor ${matchesBeforeCursor.toList()}".logd("aaa")
//                    if (matchesBeforeCursor.none()) {
//                        return@setOnKeyListener false
//                    }
//                    "cursorPosition $cursorPositionStart selectionEnd $cursorPositionEnd textBeforeCursor $textBeforeCursor lastMatches ${lastMatches?.range?.first} ${lastMatches?.range?.last}".logd(
//                        "aaa"
//                    )
//                    //删除到at后进行全选
//                    if (lastMatches.range.last + 1 == textBeforeCursor.length) {
//                        editText.setSelection(
//                            lastMatches.range.first,
//                            lastMatches.range.last + 1
//                        )
//                        return@setOnKeyListener true
//                    }
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

    override fun updateDrawState(ds: TextPaint) {
        ds.let {
            it.color = mentionColor
            it.bgColor = mentionBackgroundColor
        }
    }

    override fun onClick(widget: View) {
//        "onclick $text".logd("aaa")
        click.invoke(text)
    }
}