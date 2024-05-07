package com.cn.game.sdk.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.bean.BettingRecordBean

/**
 * 计算默认的
 */
object ComputeDefault {
    /**
     * 左上角
     */
    var  leftTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),screenXYTemporary= intArrayOf(0, 0),
                                       viewXYLast= intArrayOf(0, 0),screenXYLast= intArrayOf(0, 0))
    /**
     * 右上角
     */
    var  rightTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),screenXYTemporary= intArrayOf(0, 0),
        viewXYLast= intArrayOf(0, 0),screenXYLast= intArrayOf(0, 0))





    /**
     * 关闭左上角临时的钱
     */
    fun offRightTopTemporarily(){
        leftTop.moneyTemporary=0


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


}