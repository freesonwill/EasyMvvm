package com.cn.game.sdk2.data.bean

data class GameHallItem(
    val idp:Int,
    val gameType:Int,
    val weight:Int,
    val direction:Int,
    val icon:String,
    val name:String,
) {
    var online:Int = 0
    /**
     * 是否是本地游戏 0-App 1-网络 2-本地
     */
    val iconType:Int get() = when {
        icon.startsWith("http") ->  1
        icon.startsWith("/") -> 2
        else -> 0
    }

    override fun toString(): String {
        return "name:$name"
    }
    companion object {
        private val gameTypeMap by lazy { mapOf(
            0 to 0,
            3 to 1,
            2 to 2,
            1 to 3,
            5 to 4,
            4 to 5,
        )    }

        /**
         * 游戏类型转Tab的索引
         */
        fun gameType2Index(gameType: Int):Int{
            return gameTypeMap[gameType]!!
        }
    }
}
