package com.walisport.module.live.data.constants

/**
 * @author: wenxi
 * @date: 8/6/25 21:11
 * @description: 检查投注有效额度返回结果
 */
enum class CheckBetResultEnum(val value:Int) {
    SUCCESS(0),
    BET_AMOUNT(9), //投注额度不足
    BALANCE(11);// 余额不足

    companion object{

        fun getCheckBetResult(value: Int):CheckBetResultEnum? = entries.find { it.value == value }
    }
}