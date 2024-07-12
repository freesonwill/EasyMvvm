package com.xcjh.app.view

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.xcjh.app.R
import com.xcjh.app.databinding.LiveOpenDialogBinding
import com.xcjh.base_lib.utils.view.clickNoRepeat

class LiveOpenPopup(context: Context,name:String) : BasePopupView(context) {
    private lateinit var mDatabind: LiveOpenDialogBinding
    var  livaName:String=name

    /**
     * 是否下次继续提示默认是可以
     */
    private  var isTips:Boolean=true
    /**
     * 倒计时的时间是毫秒1000
     */
    var countdownTime:Int=10000
    var countDownTimer:CountDownTimer?=null
    override fun getInnerLayoutId(): Int {
        return R.layout.live_open_dialog
    }



    override fun onCreate() {
        super.onCreate()
        mDatabind = LiveOpenDialogBinding.bind(findViewById<View>(R.id.rlRoot))
        countDownTimer = object : CountDownTimer( countdownTime.toLong(), 1000){
            override fun onTick(millisUntilFinished: Long) {
                var secondsddd = (millisUntilFinished / 1000).toInt()
                if(secondsddd>=10){

                    mDatabind.txtLiveTime.text=resources.getString(R.string.live_txt_time,secondsddd.toString())
                }else{

                    mDatabind.txtLiveTime.text=resources.getString(R.string.live_txt_time,"0${secondsddd.toString()}")
                }
            }

            override fun onFinish() {
                dismiss()
            }

        }
        setDate()
        //点击继续观看
        mDatabind.rlOpenContinue.clickNoRepeat {
            liveOpenListener?.continueLive()
            dismiss()
        }
        //点击选择信号源
        mDatabind.rlOpenSelect.clickNoRepeat {
            liveOpenListener?.selectSignal()
            dismiss()
        }

    }
      fun setName(name:String){
        livaName=name
        setDate()
    }

    fun setDate(){
        mDatabind.txtOpenName.text=resources.getString(R.string.live_txt_name,livaName)
        countDownTimer!!.start()
    }

    override fun dismiss() {
        countDownTimer!!.cancel()
        liveOpenListener?.clickClose()
        super.dismiss()
    }
    var liveOpenListener: LiveOpenPopupListener?=null
    //点击事件
    interface  LiveOpenPopupListener{
        /**
         * 关闭
         */
        fun  clickClose()

        /**
         * 继续观看
         */
        fun continueLive()

        /**
         * 选择信号源
         */
        fun selectSignal()
    }

}