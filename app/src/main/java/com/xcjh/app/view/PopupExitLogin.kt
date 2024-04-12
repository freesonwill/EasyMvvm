package com.xcjh.app.view

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.app.R
import com.xcjh.app.adapter.PushCardPopup
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.web.WebActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 退出登录
 */
class PopupExitLogin(context: Context) : BottomPopupView(context) {


    override fun getImplLayoutId(): Int {
        return R.layout.dialog_login_out
    }


    override fun onCreate() {
        super.onCreate()
        var txtDialogCancel=findViewById<AppCompatTextView>(R.id.txtDialogCancel)
        var txtDialogLogin= findViewById<AppCompatTextView>(R.id.txtDialogLogin)
        txtDialogCancel.clickNoRepeat {
           dismiss()
        }
        txtDialogLogin.clickNoRepeat {
//            mViewModel.unbindPush()
            popupExitLoginListener!!.clickClose()
        }
    }
    var popupExitLoginListener: PopupExitLoginListener?=null

    //点击事件
    interface  PopupExitLoginListener{
        //关闭
        fun  clickClose()
    }


}