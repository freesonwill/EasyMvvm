package com.cn.game.sdk2.utils

import android.util.SparseArray
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BoomBet
import com.cn.game.sdk2.websocket.bean.DefaultBet
import com.cn.game.sdk2.websocket.bean.DoubleBet
import com.cn.game.sdk2.websocket.bean.SingleBet
import com.cn.game.sdk2.websocket.bean.SumBet

object BettingAreaUtil {
    const val BET_AREA_DEFAULT_BIG = 1
    const val BET_AREA_DEFAULT_SMALL = 2
    const val BET_AREA_DEFAULT_SINGLE = 3
    const val BET_AREA_DEFAULT_DOUBLE = 4
    const val BET_AREA_SUM_4 = 5
    const val BET_AREA_SUM_5 = 6
    const val BET_AREA_SUM_6 = 7
    const val BET_AREA_SUM_7 = 8
    const val BET_AREA_SUM_8 = 9
    const val BET_AREA_SUM_9 = 10
    const val BET_AREA_SUM_10 = 11
    const val BET_AREA_SUM_11 = 12
    const val BET_AREA_SUM_12 = 13
    const val BET_AREA_SUM_13 = 14
    const val BET_AREA_SUM_14 = 15
    const val BET_AREA_SUM_15 = 16
    const val BET_AREA_SUM_16 = 17
    const val BET_AREA_SUM_17 = 18
    const val BET_AREA_SINGLE_1 = 19
    const val BET_AREA_SINGLE_2 = 20
    const val BET_AREA_SINGLE_3 = 21
    const val BET_AREA_SINGLE_4 = 22
    const val BET_AREA_SINGLE_5 = 23
    const val BET_AREA_SINGLE_6 = 24
    const val BET_AREA_DOUBLE_1 = 25
    const val BET_AREA_DOUBLE_2 = 26
    const val BET_AREA_DOUBLE_3 = 27
    const val BET_AREA_DOUBLE_4 = 28
    const val BET_AREA_DOUBLE_5 = 29
    const val BET_AREA_DOUBLE_6 = 30
    const val BET_AREA_BOOM_1 = 31
    const val BET_AREA_BOOM_2 = 32
    const val BET_AREA_BOOM_3 = 33
    const val BET_AREA_BOOM_4 = 34
    const val BET_AREA_BOOM_5 = 35
    const val BET_AREA_BOOM_6 = 36
    const val BET_AREA_BOOM_ALL = 37

    private val betAreaList = listOf(
        DefaultBet(BET_AREA_DEFAULT_BIG, "大"),
        DefaultBet(BET_AREA_DEFAULT_SMALL, "小"),
        DefaultBet(BET_AREA_DEFAULT_SINGLE, "单"),
        DefaultBet(BET_AREA_DEFAULT_DOUBLE, "双"),
        SumBet(BET_AREA_SUM_4, "4点", 63f),
        SumBet(BET_AREA_SUM_5, "5点", 32f),
        SumBet(BET_AREA_SUM_6, "6点", 19.5f),
        SumBet(BET_AREA_SUM_7, "7点", 13f),
        SumBet(BET_AREA_SUM_8, "8点", 9.5f),
        SumBet(BET_AREA_SUM_9, "9点", 8f),
        SumBet(BET_AREA_SUM_10, "10点", 7.5f),
        SumBet(BET_AREA_SUM_11, "11点", 7.5f),
        SumBet(BET_AREA_SUM_12, "12点", 8f),
        SumBet(BET_AREA_SUM_13, "13点", 9.5f),
        SumBet(BET_AREA_SUM_14, "14点", 13f),
        SumBet(BET_AREA_SUM_15, "15点", 19.5f),
        SumBet(BET_AREA_SUM_16, "16点", 32f),
        SumBet(BET_AREA_SUM_17, "17点", 63f),
        SingleBet(BET_AREA_SINGLE_1, "单骰1"),
        SingleBet(BET_AREA_SINGLE_2, "单骰2"),
        SingleBet(BET_AREA_SINGLE_3, "单骰3"),
        SingleBet(BET_AREA_SINGLE_4, "单骰4"),
        SingleBet(BET_AREA_SINGLE_5, "单骰5"),
        SingleBet(BET_AREA_SINGLE_6, "单骰6"),
        DoubleBet(BET_AREA_DOUBLE_1, "对子1"),
        DoubleBet(BET_AREA_DOUBLE_2, "对子2"),
        DoubleBet(BET_AREA_DOUBLE_3, "对子3"),
        DoubleBet(BET_AREA_DOUBLE_4, "对子4"),
        DoubleBet(BET_AREA_DOUBLE_5, "对子5"),
        DoubleBet(BET_AREA_DOUBLE_6, "对子6"),
        BoomBet(BET_AREA_BOOM_1, "豹子1"),
        BoomBet(BET_AREA_BOOM_2, "豹子2"),
        BoomBet(BET_AREA_BOOM_3, "豹子3"),
        BoomBet(BET_AREA_BOOM_4, "豹子4"),
        BoomBet(BET_AREA_BOOM_5, "豹子5"),
        BoomBet(BET_AREA_BOOM_6, "豹子6"),
        BoomBet(BET_AREA_BOOM_ALL, "全豹", 32f)
    )

    /**
     *  取得所有注區
     */
    fun getAllBets(): List<Betting> {
        return betAreaList
    }

    /**
     *  取得默認注區：大、小、單、雙、全豹
     */
    fun getDefaultBets(): List<Betting> {
        return mutableListOf<Betting>().apply {
            addAll(betAreaList.filter { it.number <= BET_AREA_DEFAULT_DOUBLE })
            add(betAreaList.first { it.number == BET_AREA_BOOM_ALL })
        }.toList()
    }

    /**
     * 取得單骰注區：1-6
     */
    fun getSingleBets(): List<Betting> {
        return betAreaList.filter {
            it.number in BET_AREA_SINGLE_1 .. BET_AREA_SINGLE_6
        }
    }

    /**
     *  取得總和注區：4-17點
     */
    fun getSumBets(): List<Betting> {
        return betAreaList.filter {
            it.number in BET_AREA_SUM_4..BET_AREA_SUM_17
        }
    }

    /**
     * 取得對子注區：1-6
     */
    fun getDoubleBets(): List<Betting> {
        return betAreaList.filter {
            it.number in BET_AREA_DOUBLE_1 .. BET_AREA_DOUBLE_6
        }
    }

    /**
     *  取得豹子注區：1-6（不含全豹）
     */
    fun getBoomBets(): List<Betting> {
        return betAreaList.filter {
            it.number in BET_AREA_BOOM_1 .. BET_AREA_BOOM_6
        }
    }

    fun List<Betting>.toSpareArray(startIndex: Int = 1): SparseArray<Betting> {
        return SparseArray<Betting>().also { array ->
            this.forEachIndexed { index, betting ->
                array[index + startIndex] = betting
            }
        }
    }
}