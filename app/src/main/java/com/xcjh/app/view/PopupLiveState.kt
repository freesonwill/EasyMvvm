package com.xcjh.app.view

import android.content.Context
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.app.R

class PopupLiveState (context: Context,content:String="") : BasePopupView(context) {
    override fun getInnerLayoutId(): Int {
        return R.layout.layout_dialogx_delmsg_new
    }


    override fun onCreate() {
        super.onCreate()
        val tvcancle = findViewById<TextView>(R.id.tvcancle)
        val textName = findViewById<TextView>(R.id.textName)
        val tvsure = findViewById<TextView>(R.id.tvsure)
        val viewGen = findViewById<View>(R.id.viewGen)

        textName.text = resources.getString(R.string.place_txt_login)
        tvcancle.visibility = View.GONE
        viewGen.visibility = View.GONE

        tvsure.setOnClickListener {
//            appViewModel.quitLoginEvent.postValue(true)
//            startNewActivity<LoginActivity> {}
//            GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
//                delay(1000L) // 延迟1秒（1000毫秒）
//                appViewModel.mainViewPagerEvent.postValue(-1)
//
//            }
//            dismiss()
            popupKickOutListener?.clickClose()

        }
    }

    var popupKickOutListener: PopupKickOutListener? = null

    //点击事件
    interface PopupKickOutListener {
        //关闭
        fun clickClose()
    }
}
