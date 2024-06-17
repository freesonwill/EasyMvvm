package com.cn.game.sdk2.ui.view

import android.content.Context
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.cn.game.sdk2.R
import com.lxj.xpopup.core.BubbleAttachPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 首页的弹出框
 */
class CustomBubbleAttachPopup(content: Context) : BubbleAttachPopupView(content){
    override fun getImplLayoutId(): Int {
        return R.layout.popup_custom_bubble_attach
    }

    override fun onCreate() {
        super.onCreate()
        setBubbleBgColor(ContextCompat.getColor(context,R.color.c_ffffff))
        setBubbleShadowSize(2)
        setArrowWidth(XPopupUtils.dp2px(context, 8f))
        setArrowHeight(XPopupUtils.dp2px(context, 8f))
                                        .setBubbleRadius(10)
        setArrowRadius(XPopupUtils.dp2px(context, 2f))

        var rlPopClickToggle=findViewById<RelativeLayout>(R.id.rlPopClickToggle)
        rlPopClickToggle.clickNoRepeat {
            customBubbleAttachListener?.switchGame()
        }

    }
    var customBubbleAttachListener: CustomBubbleAttachListener?=null

    interface  CustomBubbleAttachListener{
    /**
     * 切换游戏
     */
    fun  switchGame()

    }

}