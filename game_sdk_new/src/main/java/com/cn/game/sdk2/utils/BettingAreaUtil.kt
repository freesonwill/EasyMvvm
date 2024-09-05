package com.cn.game.sdk2.utils

import android.util.SparseArray
import com.cn.game.sdk2.R
import com.cn.game.sdk2.utils.ext.CommonExt.getString
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
        DefaultBet(BET_AREA_DEFAULT_BIG, R.string.g_home_txt_big.getString()),
        DefaultBet(BET_AREA_DEFAULT_SMALL, R.string.g_home_txt_small.getString()),
        DefaultBet(BET_AREA_DEFAULT_SINGLE, R.string.g_home_txt_single.getString()),
        DefaultBet(BET_AREA_DEFAULT_DOUBLE, R.string.g_home_txt_double.getString()),
        SumBet(BET_AREA_SUM_4, "4${R.string.g_home_txt_sum.getString()}", 63f),
        SumBet(BET_AREA_SUM_5, "5${R.string.g_home_txt_sum.getString()}", 32f),
        SumBet(BET_AREA_SUM_6, "6${R.string.g_home_txt_sum.getString()}", 19.5f),
        SumBet(BET_AREA_SUM_7, "7${R.string.g_home_txt_sum.getString()}", 13f),
        SumBet(BET_AREA_SUM_8, "8${R.string.g_home_txt_sum.getString()}", 9.5f),
        SumBet(BET_AREA_SUM_9, "9${R.string.g_home_txt_sum.getString()}", 8f),
        SumBet(BET_AREA_SUM_10, "10${R.string.g_home_txt_sum.getString()}", 7.5f),
        SumBet(BET_AREA_SUM_11, "11${R.string.g_home_txt_sum.getString()}", 7.5f),
        SumBet(BET_AREA_SUM_12, "12${R.string.g_home_txt_sum.getString()}", 8f),
        SumBet(BET_AREA_SUM_13, "13${R.string.g_home_txt_sum.getString()}", 9.5f),
        SumBet(BET_AREA_SUM_14, "14${R.string.g_home_txt_sum.getString()}", 13f),
        SumBet(BET_AREA_SUM_15, "15${R.string.g_home_txt_sum.getString()}", 19.5f),
        SumBet(BET_AREA_SUM_16, "16${R.string.g_home_txt_sum.getString()}", 32f),
        SumBet(BET_AREA_SUM_17, "17${R.string.g_home_txt_sum.getString()}", 63f),
        SingleBet(BET_AREA_SINGLE_1, "${R.string.g_home_tab_single.getString()}1"),
        SingleBet(BET_AREA_SINGLE_2, "${R.string.g_home_tab_single.getString()}2"),
        SingleBet(BET_AREA_SINGLE_3, "${R.string.g_home_tab_single.getString()}3"),
        SingleBet(BET_AREA_SINGLE_4, "${R.string.g_home_tab_single.getString()}4"),
        SingleBet(BET_AREA_SINGLE_5, "${R.string.g_home_tab_single.getString()}5"),
        SingleBet(BET_AREA_SINGLE_6, "${R.string.g_home_tab_single.getString()}6"),
        DoubleBet(BET_AREA_DOUBLE_1, "${R.string.g_home_tab_double.getString()}1"),
        DoubleBet(BET_AREA_DOUBLE_2, "${R.string.g_home_tab_double.getString()}2"),
        DoubleBet(BET_AREA_DOUBLE_3, "${R.string.g_home_tab_double.getString()}3"),
        DoubleBet(BET_AREA_DOUBLE_4, "${R.string.g_home_tab_double.getString()}4"),
        DoubleBet(BET_AREA_DOUBLE_5, "${R.string.g_home_tab_double.getString()}5"),
        DoubleBet(BET_AREA_DOUBLE_6, "${R.string.g_home_tab_double.getString()}6"),
        BoomBet(BET_AREA_BOOM_1, "${R.string.g_home_tab_leopard.getString()}1"),
        BoomBet(BET_AREA_BOOM_2, "${R.string.g_home_tab_leopard.getString()}2"),
        BoomBet(BET_AREA_BOOM_3, "${R.string.g_home_tab_leopard.getString()}3"),
        BoomBet(BET_AREA_BOOM_4, "${R.string.g_home_tab_leopard.getString()}4"),
        BoomBet(BET_AREA_BOOM_5, "${R.string.g_home_tab_leopard.getString()}5"),
        BoomBet(BET_AREA_BOOM_6, "${R.string.g_home_tab_leopard.getString()}6"),
        BoomBet(BET_AREA_BOOM_ALL, R.string.g_home_txt_leopard_all.getString(), 32f)
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