package com.walisport.module.hall.data

import com.walisport.module.hall.data.Category.ALL
import com.walisport.module.hall.data.Category.entries

/**
 *
 * @date: 2025/12/12 16:12
 * @description:
 */
enum class Category(val type: Int , val desc: String) {
    ALL(100 , "全部") ,
    RECENT(101 , "最近") ,
    HOT(102 , "热门") ,
    ORIGIN(103 , "原创") ,
    FISH(1 , "捕鱼") ,
    VIDEO(2 , "视讯") ,
    POKER(3 , "棋牌") ,
    TIGER(4 , "老虎机") ,
    SPORT(5 , "体育") ,
    LOTTERY(6 , "彩票") ,
    ELECTRONIC(7 , "电竞");
}

fun Int.getCategoryByType(): Category {
    return entries.find { it.type == this } ?: ALL
}