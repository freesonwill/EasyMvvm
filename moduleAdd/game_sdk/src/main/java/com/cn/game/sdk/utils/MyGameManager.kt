package com.cn.game.sdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.cn.game.sdk.view.FastLogoView
@SuppressLint("StaticFieldLeak")
object MyGameManager {
    //快三的浮动View

    private var  fastThreeView:FastLogoView?=null
    private var mContext:Context?=null

    //获取快三的浮动view

    fun   getFastThreeView(context:Context):FastLogoView{
        if(fastThreeView==null){
            mContext=context
            fastThreeView= FastLogoView(context)
        }

        return fastThreeView as FastLogoView
    }


    fun  setToast(context: Context){
        Toast.makeText(context,"sssssssssssssssss",Toast.LENGTH_LONG).show()
    }

}