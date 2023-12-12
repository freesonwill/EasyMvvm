package com.xcjh.app.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.lxj.xpopup.core.PositionPopupView
import com.xcjh.app.R
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.base_lib.utils.view.clickNoRepeat

class PushCardPopup(content: Context,var beingLiveBean: BeingLiveBean) : PositionPopupView(content){

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_push_card
    }

    override fun onCreate() {
        super.onCreate()
        var  ivCardClose=findViewById<AppCompatImageView>(R.id.ivCardClose)
        var  txtDialogClick=findViewById<AppCompatTextView>(R.id.txtDialogClick)
        ivCardClose.clickNoRepeat {
            pushCardPopupListener!!.clicktClose()
        }
        txtDialogClick.clickNoRepeat {
            pushCardPopupListener!!.selectGoto(beingLiveBean)
        }
    }
    var pushCardPopupListener: PushCardPopupListener?=null

    //点击事件
    interface  PushCardPopupListener{
        //关闭
        fun  clicktClose()
        fun selectGoto(beingLiveBean: BeingLiveBean)
    }
}