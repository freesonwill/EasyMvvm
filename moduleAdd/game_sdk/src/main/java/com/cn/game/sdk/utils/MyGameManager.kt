package com.cn.game.sdk.utils

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.cn.game.sdk.MyGameApplication
import com.cn.game.sdk.R
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.view.FastLogoView
import com.cn.game.sdk.view.OpenResultView
import com.xcjh.app.event.AppGameViewModel

@SuppressLint("StaticFieldLeak")
object MyGameManager {
    /**
     * 快三的浮动View
     */
    private var  fastThreeView:FastLogoView?=null

    /**
     * 结果的View
     */
    private var  openResultView: OpenResultView?=null
    private var mContext:Context?=null

    //获取快三的浮动view
    fun   getFastThreeView(context:Context):FastLogoView{
        if(fastThreeView==null){
            mContext=context
            fastThreeView= FastLogoView(context)

        }

        return fastThreeView as FastLogoView
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
        val options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_up, 0)
        var inagte= Intent(context, GameHomeActivity::class.java)
        ContextCompat.startActivity(context, inagte, options.toBundle())
    }

    fun setLogoTime(){
        if(fastThreeView!=null){
            fastThreeView!!.setText("10:00")
        }
    }

}