package com.walisport.module.hall.data.constants

/**
 *
 * @date: 2025/12/10 23:01
 * @description:
 */
enum class GameSortType(val type: Int , val desc: String) {
    HOT(0 , "hot") , //热门
    NEW(1 , "new") ,//  最新上线
    HOT_REWARD(2 , "hot_reward") ,     // 火热返奖
    COLD_REWARD(3 , "cold_reward")     // 冰冷返奖
}