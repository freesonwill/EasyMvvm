package com.xcjh.app.utils

import com.xcjh.app.bean.MsgBeanData

class CacheDataList {

    companion object {
        val globalCache: MutableMap<String, ArrayList<MsgBeanData>> = mutableMapOf()
    }

}