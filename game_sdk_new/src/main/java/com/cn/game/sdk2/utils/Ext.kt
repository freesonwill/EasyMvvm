package com.cn.game.sdk2.utils


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object Ext {


    fun Int.toPinyin(): String {
        return PinyinUtils.toPinyin(this)
    }
}