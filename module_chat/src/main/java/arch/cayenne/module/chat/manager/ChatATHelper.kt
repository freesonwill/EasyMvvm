package arch.cayenne.module.chat.manager

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * @author: wenxi
 * @date: 18/11/25 16:11
 * @description:
 */
object ChatATHelper {

    fun filterEtInputWithAt(editable: Editable){
        val text = editable.toString()
//        editable.clear()
        val pattern =  "@[^\\s@]+\\s".toRegex()
        pattern.findAll(text).forEach {matchResult ->
            val start = matchResult.range.first
            val end = matchResult.range.last+1
            val mentionSpan = MentionSpan()
            editable.setSpan(mentionSpan,start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }


}

class MentionSpan():ClickableSpan(){
    private val mentionColor = Color.parseColor("#8FBEE9")
    private val mentionBackgroundColor = Color.parseColor("#4DFE3666")

    override fun updateDrawState(ds: TextPaint) {
        ds.let {
           it.color = mentionColor
           it.bgColor = mentionBackgroundColor
       }
    }

    override fun onClick(widget: View) {

    }
}