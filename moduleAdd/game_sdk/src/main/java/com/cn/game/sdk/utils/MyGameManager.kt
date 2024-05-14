package com.cn.game.sdk.utils

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.cn.game.sdk.MyGameApplication
import com.cn.game.sdk.R
import com.cn.game.sdk.bean.SelectAnnotationBean
import com.cn.game.sdk.listener.GameTimeStatic
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.view.FastLogoView
import com.cn.game.sdk.view.OpenResultView
import com.xcjh.base_lib.App.Companion.appGame

@SuppressLint("StaticFieldLeak")
object MyGameManager {
    /**
     * 投注的钱
     */
    var noteList=ArrayList<SelectAnnotationBean>()
    /**
     * 快三的浮动View
     */
    private var  fastThreeView:FastLogoView?=null

    /**
     * 结果的View
     */
    private var  openResultView: OpenResultView?=null
    private var mContext:Context?=null

    /**
     * 当前余额并且是点击了确定后扣除的真实
     */
      var currentMoney:Int=5000

    /**
     * 每次点击扣钱，但是不显示出来，确定后才把这个金额显示在真实钱上
     */
    var temporaryCurrentMoney:Int=5000

    /**
     * 是否可以点击操作
     */
    var  isClickOperation:Boolean=true

    /**
     * 是否进入直播间了，如果进入直播间就要调用 进入直播间的消息
     */
    var  isStayLive:Boolean=false

    /**
     * 倒计时的时间是毫秒1000
     */
    var countdownTime:Int=20000

    /**
     * 倒计时的状态0没有连接上  1是下注   2结算
     */
    var  static=1

    //获取快三的浮动view
    fun   getFastThreeView(context:Context):FastLogoView{
        if(fastThreeView==null){
            mContext=context
            fastThreeView= FastLogoView(context)
        }

        return fastThreeView as FastLogoView
    }

    /**
     * 直播流状态
     */
    private val mGameListener = linkedMapOf<String, GameTimeStatic>()
    fun setLiveStatusListener(tag: String, listener: GameTimeStatic) {
        mGameListener[tag] = listener
    }

    fun removeLiveStatusListener(tag: String) {
        if (mGameListener[tag] != null) {
            mGameListener.remove(tag)
        }
    }

    init {
        noteList.add(SelectAnnotationBean( money = 10,select=true))
        noteList.add(SelectAnnotationBean( money = 50))
        noteList.add(SelectAnnotationBean( money = 100))
        noteList.add(SelectAnnotationBean( money = 200))
        noteList.add(SelectAnnotationBean( money = 500))
        noteList.add(SelectAnnotationBean( money = 1000))
        noteList.add(SelectAnnotationBean( money = 2000))
        noteList.add(SelectAnnotationBean( money = 5000))
        noteList.add(SelectAnnotationBean( money = 10000))
        noteList.add(SelectAnnotationBean( money = 20000))
        noteList.add(SelectAnnotationBean( money = 50000))
        noteList.add(SelectAnnotationBean( money = 100000))
    }

    /**
     * 获取到快三结果的View
     */
    fun getOpenResultView(context:Context):OpenResultView{
        if(openResultView==null){
            mContext=context
            openResultView= OpenResultView(context)

        }

        return openResultView as OpenResultView
    }


    fun  setToast(context: Context){
        val options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_up, R.anim.slide_down)
        var inagte= Intent(context, GameHomeActivity::class.java)
        ContextCompat.startActivity(context, inagte, options.toBundle())
    }

    fun setLogoTime(){
        if(fastThreeView!=null){
            fastThreeView!!.setText("10:00")
        }
    }





    /**
     * 根据选中的item返回要飞的图片
     */
    fun getListImage(position:Int,context:Context): Drawable {
        if(position==0){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_shi)!!
        }else if(position==1){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_wushi)!!
        }else if(position==2){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_yibai)!!
        }else if(position==3){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_liangbai)!!
        }else if(position==4){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_wubai)!!
        }else if(position==5){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_qian)!!
        }else if(position==6){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_liangqian)!!
        }else if(position==7){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_wuqian)!!
        }else if(position==8){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_yiwan)!!
        }else if(position==9){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_liangwan)!!
        }else if(position==10){
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_wuwan)!!
        }else{
            return ContextCompat.getDrawable(context, R.drawable.icon_ok_shiwan)!!
        }

    }


    val countDownTimer = object : CountDownTimer(20 * 1000, 500) {
        override fun onTick(millisUntilFinished: Long) {
            mGameListener.forEach{

                val seconds: Long = Math.round(millisUntilFinished.toDouble() / 1000)
                if(seconds.toInt()<=1||static==2){
                    isClickOperation=false
                }

                countdownTime=seconds.toInt()
                it.toPair().second.onCountdown(millisUntilFinished)
            }
        }

        override fun onFinish() {
            countdownTime=0
            if(static==1){
                static=2
                isClickOperation=false
            }else{
                static=1

            }
            mGameListener.forEach{
                it.toPair().second.onStatic(static)
            }



        }
    }


}