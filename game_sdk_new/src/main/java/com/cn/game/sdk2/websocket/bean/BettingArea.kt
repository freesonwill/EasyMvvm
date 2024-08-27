package com.cn.game.sdk2.websocket.bean

/**
 *  注區資訊
 */
sealed class Betting {
    /**
     *  注區編號(App自定義, 1-4 大小單雙 / 5-18 4-17點 / 19-24 單骰1-6 / 25-30 對子1-6 / 31-36 豹子1-6 / 37 全豹)
     */
    open var number: Int = 0

    /**
     *  賠率
     */
    open var multiplier: Float = 0f

    /**
     *  賠率列表
     */
    open var multipliers: List<Float> = listOf()

    /**
     *  注區中獎次數（單骰）
     */
    open var count: Int = 0

    /**
     *  注區名稱
     */
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

data class DefaultBet(
    override var number: Int,
    override var toastStr: String,
    override var multiplier: Float = 1.99f
) : DEFAULT()

data class SingleBet(
    override var number: Int,
    override var toastStr: String,
    override var multipliers: List<Float> = listOf(2f, 3f, 4f),
    override var count: Int = 0
) : SINGLE() {
    override fun equals(other: Any?): Boolean {
        return other is SINGLE && other.number == this.number
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + number
    }
}

data class SumBet(
    override var number: Int,
    override var toastStr: String,
    override var multiplier: Float
) : SUM()

data class DoubleBet(
    override var number: Int,
    override var toastStr: String,
    override var multiplier: Float = 12f
) : DOUBLE()

data class BoomBet(
    override var number: Int,
    override var toastStr: String,
    override var multiplier: Float = 180f
) : BOOM()