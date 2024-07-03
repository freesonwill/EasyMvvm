package com.cn.game.sdk2.websocket.bean

import java.io.Serializable

data class SendDataBean(var mid: Short, var sid: Short, var data: ByteArray)

data class RoundInfoBean(
    var roundId: String,
    var performs: List<Int>,
    var sum: Int,
    var isBig: Boolean,
    var isDouble: Boolean
)


/**
 * 同步注区信息时用
 */
data class AreaBetBean(
    var areaCode: Betting, var betScore: Int, var userCount: Int
)

/**
 * 注区最大最小值限制
 */
data class AreaBetConfigBean(
    var areaCode: Betting, var minLimit: Int, var maxLimit: Int
)

/**
 * 下注：服务器返回时通知ui
 */
data class BettingResponsesBean(val isSuccess: Boolean, var money: Int)
interface DataClass

/**
 * 每次点击下注就传入该对象
 */
data class BettingRecordBean(

    /**
     * 注区
     */
    var bettingArea: Betting,

    /**
     *  动画相对于控件的位置
     */
    var viewXYTemporary: FloatArray = FloatArray(2),

    /**
     * * 下注金额
     *  - 传入时：该字段单位是元
     *  - 收到时：该字段单位是分，所以需要缩小100倍
     */
    var money: Int = 0,


    ) : Serializable, DataClass {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BettingRecordBean

        return viewXYTemporary.contentEquals(other.viewXYTemporary)
    }

    override fun hashCode(): Int {
        var result = bettingArea.hashCode()
        result = 31 * result + viewXYTemporary.contentHashCode()
        result = 31 * result + money
        return result
    }
}

sealed class Betting {
    open var number: Int = 0
    open var multiplier: Float = 0f
    open var multipliers: List<Float> = listOf()
    open var count: Int = 0
    open var toastStr = ""
    override fun hashCode(): Int {
        return number.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Betting && other.number == number
    }
}

sealed class DEFAULT : Betting()
sealed class SUM : Betting()
sealed class SINGLE : Betting()
sealed class DOUBLE : Betting()
sealed class BOOM : Betting()
data class DEFAULT_BIG(
    override var number: Int = 1,
    override var multiplier: Float = 1.99f,
    override var toastStr: String = "大"
) : DEFAULT()

data class DEFAULT_SMALL(
    override var number: Int = 2,
    override var multiplier: Float = 1.99f,
    override var toastStr: String = "小"
) : DEFAULT()

data class DEFAULT_SINGLE(
    override var number: Int = 3,
    override var multiplier: Float = 1.99f,
    override var toastStr: String = "单"
) : DEFAULT()

data class DEFAULT_DOUBLE(
    override var number: Int = 4,
    override var multiplier: Float = 1.99f,
    override var toastStr: String = "双"
) : DEFAULT()

data class SUM_4(
    override var number: Int = 5,
    override var multiplier: Float = 63f,
    override var toastStr: String = "4点"
) : SUM()

data class SUM_5(
    override var number: Int = 6,
    override var multiplier: Float = 32f,
    override var toastStr: String = "5点"
) : SUM()

data class SUM_6(
    override var number: Int = 7,
    override var multiplier: Float = 19.5f,
    override var toastStr: String = "6点"
) : SUM()

data class SUM_7(
    override var number: Int = 8,
    override var multiplier: Float = 13f,
    override var toastStr: String = "7点"
) : SUM()

data class SUM_8(
    override var number: Int = 9,
    override var multiplier: Float = 9.5f,
    override var toastStr: String = "8点"
) : SUM()

data class SUM_9(
    override var number: Int = 10,
    override var multiplier: Float = 8f,
    override var toastStr: String = "9点"
) : SUM()

data class SUM_10(
    override var number: Int = 11,
    override var multiplier: Float = 7.5f,
    override var toastStr: String = "10点"
) : SUM()

data class SUM_11(
    override var number: Int = 12,
    override var multiplier: Float = 7.5f,
    override var toastStr: String = "11点"
) : SUM()

data class SUM_12(
    override var number: Int = 13,
    override var multiplier: Float = 8f,
    override var toastStr: String = "12点"
) : SUM()

data class SUM_13(
    override var number: Int = 14,
    override var multiplier: Float = 9.5f,
    override var toastStr: String = "13点"
) : SUM()

data class SUM_14(
    override var number: Int = 15,
    override var multiplier: Float = 13f,
    override var toastStr: String = "14点"
) : SUM()

data class SUM_15(
    override var number: Int = 16,
    override var multiplier: Float = 19.5f,
    override var toastStr: String = "15点"
) : SUM()

data class SUM_16(
    override var number: Int = 17,
    override var multiplier: Float = 32f,
    override var toastStr: String = "16点"
) : SUM()

data class SUM_17(
    override var number: Int = 18,
    override var multiplier: Float = 63f,
    override var toastStr: String = "17点"
) : SUM()

data class SINGLE_1(
    override var number: Int = 19,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰1"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class SINGLE_2(
    override var number: Int = 20,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰2"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class SINGLE_3(
    override var number: Int = 21,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰3"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class SINGLE_4(
    override var number: Int = 22,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰4"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class SINGLE_5(
    override var number: Int = 23,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰5"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class SINGLE_6(
    override var number: Int = 24,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0,
    override var toastStr: String = "单骰6"
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + number
        return result
    }
}

data class DOUBLE_1(
    override var number: Int = 25,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子1"
) : DOUBLE()

data class DOUBLE_2(
    override var number: Int = 26,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子2"
) : DOUBLE()

data class DOUBLE_3(
    override var number: Int = 27,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子3"
) : DOUBLE()

data class DOUBLE_4(
    override var number: Int = 28,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子4"
) : DOUBLE()

data class DOUBLE_5(
    override var number: Int = 29,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子5"
) : DOUBLE()

data class DOUBLE_6(
    override var number: Int = 30,
    override var multiplier: Float = 12f,
    override var toastStr: String = "对子6"
) : DOUBLE()

data class BOOM_1(
    override var number: Int = 31,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子1"
) : BOOM()

data class BOOM_2(
    override var number: Int = 32,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子2"
) : BOOM()

data class BOOM_3(
    override var number: Int = 33,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子3"
) : BOOM()

data class BOOM_4(
    override var number: Int = 34,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子4"
) : BOOM()

data class BOOM_5(
    override var number: Int = 35,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子5"
) : BOOM()

data class BOOM_6(
    override var number: Int = 36,
    override var multiplier: Float = 180f,
    override var toastStr: String = "豹子6"
) : BOOM()

data class BOOM_ALL(
    override var number: Int = 37,
    override var multiplier: Float = 32f,
    override var toastStr: String = "豹子1-6"
) : BOOM()

val areaMap = mutableMapOf<Int, Betting>(
    1 to DEFAULT_BIG(),
    2 to DEFAULT_SMALL(),
    3 to DEFAULT_SINGLE(),
    4 to DEFAULT_DOUBLE(),
    5 to SUM_4(),
    6 to SUM_5(),
    7 to SUM_6(),
    8 to SUM_7(),
    9 to SUM_8(),
    10 to SUM_9(),
    11 to SUM_10(),
    12 to SUM_11(),
    13 to SUM_12(),
    14 to SUM_13(),
    15 to SUM_14(),
    16 to SUM_15(),
    17 to SUM_16(),
    18 to SUM_17(),
    19 to SINGLE_1(),
    20 to SINGLE_2(),
    21 to SINGLE_3(),
    22 to SINGLE_4(),
    23 to SINGLE_5(),
    24 to SINGLE_6(),
    25 to DOUBLE_1(),
    26 to DOUBLE_2(),
    27 to DOUBLE_3(),
    28 to DOUBLE_4(),
    29 to DOUBLE_5(),
    30 to DOUBLE_6(),
    31 to BOOM_1(),
    32 to BOOM_2(),
    33 to BOOM_3(),
    34 to BOOM_4(),
    35 to BOOM_5(),
    36 to BOOM_6(),
    37 to BOOM_ALL(),
)