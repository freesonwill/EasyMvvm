package com.xcjh.base_lib.network

import com.xcjh.base_lib.R
import com.xcjh.base_lib.appContext

/**
 * 作者　: zb
 * 
 * 描述　: 错误枚举类
 */
enum class Error(private val code: Int, private val err: String) {

    /**
     * 未知错误
     */
    UNKNOWN(1000, appContext.getString(R.string.UNKNOWN)),
    /**
     * 解析错误
     */
    PARSE_ERROR(1001, appContext.getString(R.string.PARSE_ERROR)),
    /**
     * 网络错误
     */
    NETWORK_ERROR(1002, appContext.getString(R.string.NETWORK_ERROR)),

    /**
     * 证书出错
     */
    SSL_ERROR(1004, appContext.getString(R.string.SSL_ERROR)),

    /**
     * 连接超时
     */
    TIMEOUT_ERROR(1006, appContext.getString(R.string.TIMEOUT_ERROR));

    fun getValue(): String {
        return err
    }

    fun getKey(): Int {
        return code
    }

}