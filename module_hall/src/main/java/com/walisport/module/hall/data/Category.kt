package com.walisport.module.hall.data

import com.walisport.module.hall.data.Category.ALL
import com.walisport.module.hall.data.Category.entries

/**
 *
 * @date: 2025/12/12 16:12
 * @description:
 */
enum class Category(val type: Int , val desc: String,val color: Int) {
    ALL(100 , "全部",arch.cayenne.lib.common.R.color.game_tab_all) ,
    RECENT(101 , "最近",arch.cayenne.lib.common.R.color.game_tab_recent) ,
    HOT(102 , "热门",arch.cayenne.lib.common.R.color.game_tab_hot) ,
    ORIGIN(103 , "原创",arch.cayenne.lib.common.R.color.game_tab_original) ,
    FISH(1 , "捕鱼",arch.cayenne.lib.common.R.color.game_tab_fishing) ,
    VIDEO(2 , "视讯",arch.cayenne.lib.common.R.color.game_tab_real_people) ,
    POKER(3 , "棋牌",arch.cayenne.lib.common.R.color.game_tab_chess) ,
    TIGER(4 , "老虎机",arch.cayenne.lib.common.R.color.game_tab_tiger) ,
    SPORT(5 , "体育",arch.cayenne.lib.common.R.color.game_tab_all) ,
    LOTTERY(6 , "彩票",arch.cayenne.lib.common.R.color.game_tab_lottery) ,
    ELECTRONIC(7 , "电竞",arch.cayenne.lib.common.R.color.game_tab_esports);
}

fun Int.getCategoryByType(): Category {
    return entries.find { it.type == this } ?: ALL
}