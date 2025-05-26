package com.walisport.module.search.data.constants

/**
 * 球员位置枚举类
 * @property code 位置代码
 */
enum class PlayerPositionEnum(val code: String) {
    // 球员位置，F-前锋、M-中场、D-后卫、G-守门员
    FORWARD("F"),
    MIDFIELDER("M"),
    DEFENDER("D"),
    GOALKEEPER("G"),
    UNKNOWN("");

    companion object {
        fun fromCode(code: String): PlayerPositionEnum {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }

    override fun toString(): String = code
}