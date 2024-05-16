package com.cn.game.sdk.bean

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
    var  leftTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),
                                       viewXYLast= intArrayOf(0, 0) )
    /**
     * 右上角
     */
    var  rightTop= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),
        viewXYLast= intArrayOf(0, 0) )
    /**
     * 左下角
     */
    var  leftBelow= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),
        viewXYLast= intArrayOf(0, 0) )


    /**
     * 右下角
     */
    var  rightBelow= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),
        viewXYLast= intArrayOf(0, 0) )

    /**
     * 中间注区
     */
    var  centreDate= BettingRecordBean(viewXYTemporary= intArrayOf(0, 0),
        viewXYLast= intArrayOf(0, 0))






}