package com.cn.game.sdk2.data.bean

import android.util.SparseArray
import androidx.core.util.forEach

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/20 14:06
 **/
class AreaMoneyMap {
    private val moneyMap = SparseArray<AreaMoney>()

    operator fun set(areaId: Int, money: AreaMoney){
        moneyMap[areaId] = money
    }

    val allTempMoney:Float get() {
        var sum = 0f
        moneyMap.forEach { _, value ->
            sum += value.moneyTemporary
        }
        return sum
    }

    val allOkMoney:Float get() {
        var sum = 0f
        moneyMap.forEach { _, value ->
            sum += value.moneyOkEmpty
        }
        return sum
    }

    class AreaMoney {
        /**
         * 临时钱
         */
        var moneyTemporary: Int = 0

        /**
         * * 用于保存确定的钱并且游戏结束要清空
         */
        var moneyOkEmpty: Int = 0

        /**
         * * 用于保存确定的钱用于记录续压
         */
        var moneyOk: Int = 0
    }
}