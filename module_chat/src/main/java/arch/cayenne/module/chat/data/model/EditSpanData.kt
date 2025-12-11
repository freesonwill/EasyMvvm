package arch.cayenne.module.chat.data.model

import android.graphics.Color
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.UpdateAppearance
import android.view.View
import androidx.annotation.ColorInt
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.chat.data.constants.MsgType

/**
 * @author: wenxi
 * @date: 27/11/25 16:10
 * @description:
 */

class MentionSpan(private val text: String, private val click: ((str: String) -> Unit)) :
    CharacterStyle(), UpdateAppearance {
    //    ClickableSpan() {
    private val tvColor = Color.parseColor("#8FBEE9")
    private val backColor = Color.parseColor("#4DFE3666")
    val tv = text

    override fun updateDrawState(ds: TextPaint) {
        ds.let {
            it.color = tvColor
//            it.bgColor = backColor
        }
    }

//    override fun onClick(widget: View) {
//        click.invoke(text)
//    }
}

class ColorSpan(@ColorInt val tvColor:Int):CharacterStyle(),UpdateAppearance{
    override fun updateDrawState(tp: TextPaint?) {
        tp.let {
//            SkinnableResourceManager.getColor(
//                , arch.cayenne.lib.common.R.color.color_00A7C0
//            )
            it?.color = tvColor
        }
    }
}

class ClickSpan(private val type:MsgType,private val text: String, ):ClickableSpan(){
    private val tvColor = Color.parseColor("#8FBEE9")
    val msgType = type
    val tv = text
    override fun updateDrawState(ds: TextPaint) {
        ds.let {
            it.color = tvColor
        }
    }
    override fun onClick(widget: View) {
    }
}