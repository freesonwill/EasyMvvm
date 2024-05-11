package com.xcjh.app.utils

import com.xcjh.app.bean.MsgBeanData

/**
 * 本地缓存
 */
class CacheDataList {

    companion object {
        val globalCache: MutableMap<String, ArrayList<MsgBeanData>> = mutableMapOf()
    }

}