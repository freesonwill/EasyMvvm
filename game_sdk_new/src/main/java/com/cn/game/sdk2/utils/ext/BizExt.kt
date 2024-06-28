package com.cn.game.sdk2.utils.ext

import com.cn.game.sdk2.websocket.bean.RoundInfoBean

/**
 * Description: 业务扩展
 * author       : zhangsan
 * createTime   : 2024/6/27 17:41
 **/
object BizExt {

    /** 是否是豹子 **/
    inline val RoundInfoBean.isLeopard:Boolean get() = run {
        if(performs.isEmpty()) return@run false
        performs.forEachIndexed { index,item->
            if(index in 1..2) {
                if(item != performs[index-1]) {
                    return@run false
                }
            }
        }
        return@run true
    }
}