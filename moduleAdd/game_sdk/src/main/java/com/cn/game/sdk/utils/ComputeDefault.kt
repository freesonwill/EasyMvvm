package com.cn.game.sdk.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.cn.game.sdk.R
import com.cn.game.sdk.bean.SelectAnnotationBean

/**
 * 计算默认的
 */
object ComputeDefault {


    /**
     * 右上角注区的位置  动画结束后显示的位置
     */
    var rightTop = IntArray(2)

    /**
     * 右上角注区动画位置  动画的位置
     */
    var rightTopAnimation= IntArray(2)


    /**
     * 右上角注区上一次位置  动画结束后显示的位置
     */
    var rightTopLast= IntArray(2)

    /**
     * 右上角注区动画上一次位置  动画的位置
     */
    var rightTopAnimationLast= IntArray(2)

    /**
     * 用于保存默认右上角的临时钱   如果当前结束了要清空
     */
    var rightTopTemporarily:Int=0

    /**
     * 用于保存右上角的确定的钱  如果当前结束了要清空
     */
    var rightTopOk:Int=0

    /**
     * 用于保存上一轮确定的钱 ，这个是和rightTopOk一样的，但是这个在游戏结束后不清空
     */
    var rightLastOk:Int=0




    /**
     * 点击右上角注区的钱
     */
    fun  clickRightTopMoney(money:Int){
        //加上钱
        rightTopTemporarily += money

    }

    /**
     * 获取右上角临时和上一次确定的总数
     */
    fun getRightTopOwn():Int{
        return  rightTopTemporarily+rightTopOk
    }

    /**
     * 关闭右上角临时的钱
     */
    fun offRightTopTemporarily(){
        rightTopTemporarily=0


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