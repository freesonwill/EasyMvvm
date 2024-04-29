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
import com.cn.game.sdk.bean.SelectAnnotationBean
import com.cn.game.sdk.ui.fast.GameHomeActivity
import com.cn.game.sdk.view.FastLogoView
import com.cn.game.sdk.view.OpenResultView
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

    //获取快三的浮动view
    fun   getFastThreeView(context:Context):FastLogoView{
        if(fastThreeView==null){
            mContext=context
            fastThreeView= FastLogoView(context)

        }

        return fastThreeView as FastLogoView
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