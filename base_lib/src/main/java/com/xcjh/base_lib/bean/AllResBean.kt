package com.xcjh.base_lib.bean

import androidx.annotation.Keep

/**
 * 所有去壳 返回的 实体类 =======================================================================
 */
@Keep
data class MyPages<T>(
    val current: String = "",
    val `data`: ArrayList<T> = arrayListOf(),
    val empty: Boolean = false,
    val pages: String = "",
    val size: String = "",
    val total: String = ""
)

@Keep
data class MyRecordPages<T>(
    val current: String = "",
    val records: ArrayList<T> = arrayListOf(),
    val empty: Boolean = false,
    val pages: String = "",
    val size: String = "",
    val total: String = ""
)

data class MsgTitle(
    val id: Int = 0,
    var msgNum: Int = 0,
    val name: String = "",
)

