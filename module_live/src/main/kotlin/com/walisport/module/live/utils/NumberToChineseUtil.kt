package com.walisport.module.live.utils

object NumberToChineseUtil {
    // 基本数字（0-9）
    private val digits = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")

    // 单位（十、百、千、万等）
    private val units = arrayOf("", "十", "百", "千")
    private val bigUnits = arrayOf("", "万", "亿", "万亿")

    /**
     * 将阿拉伯数字转换为汉字表示
     * @param number 输入的阿拉伯数字（正整数）
     * @return 汉字表示的数字字符串
     */
    fun toChinese(number: Long): String {
        if (number < 0) return "不支持负数"
        if (number == 0L) return digits[0]

        // 将数字转为字符串并反转，方便从低位到高位处理
        val numStr = number.toString().reversed()
        val result = StringBuilder()
        var zeroCount = 0 // 记录连续的零
        var needZero = false // 是否需要补零

        // 按每4位一组（万、亿等）处理
        for (groupIndex in 0 until (numStr.length + 3) / 4) {
            val groupStart = groupIndex * 4
            val groupEnd = minOf(groupStart + 4, numStr.length)
            var groupHasNonZero = false // 当前组是否有非零数字
            var groupResult = StringBuilder()

            // 处理当前组的4位数字
            for (i in groupStart until groupEnd) {
                val digit = numStr[i].digitToInt()
                val unitIndex = i % 4

                if (digit == 0) {
                    zeroCount++
                } else {
                    // 如果有非零数字，重置零计数并标记需要补零
                    if (zeroCount > 0 && groupHasNonZero) {
                        needZero = true
                    }
                    zeroCount = 0
                    groupHasNonZero = true

                    // 拼接非零数字和单位
                    if (needZero) {
                        groupResult.insert(0, digits[0])
                        needZero = false
                    }
                    groupResult.insert(0, digits[digit] + units[unitIndex])
                }
            }

            // 如果当前组有非零数字，添加大单位（万、亿等）
            if (groupHasNonZero) {
                result.insert(0, groupResult.toString() + bigUnits[groupIndex])
            }
        }

        // 处理特殊情况：10-19时，去掉“一”前缀
        val finalResult = result.toString()
        return if (finalResult.startsWith("一十")) {
            finalResult.substring(1)
        } else {
            finalResult
        }
    }
}