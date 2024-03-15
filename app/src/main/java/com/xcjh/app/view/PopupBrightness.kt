package com.xcjh.app.view

import android.content.Context
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.app.R

/**
 * 调节亮度
 */
class PopupBrightness(context: Context) : BasePopupView(context) {
    var volumeProgressbarNew: ProgressBar?=null

    override fun getInnerLayoutId(): Int {
        return R.layout.dialog_video_brightness
    }

    override fun onCreate() {
        super.onCreate()
          volumeProgressbarNew=findViewById<ProgressBar>(R.id.volumeProgressbarNew)
    }



    var popupBrightnessListener: PopupBrightnessListener?=null

    //点击事件
    interface  PopupBrightnessListener{
        //关闭
        fun  clickClose()


    }

    public fun setBrightness(num:Int){
        if(volumeProgressbarNew!=null){
            volumeProgressbarNew!!.progress=num
        }

    }

}