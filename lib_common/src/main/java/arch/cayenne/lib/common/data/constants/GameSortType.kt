package arch.cayenne.lib.common.data.constants

/**
 * 遊戲排序類型：熱門 / 最新 / 火熱返獎 / 冰冷返獎。
 * 共用於 hall 和 search 模組，避免重複定義。
 *
 * @date: 2025/12/10 23:01
 */
enum class GameSortType(val type: Int, val desc: String) {
    HOT(0, "hot"), // 热门
    NEW(1, "new"), // 最新上线
    HOT_REWARD(2, "hot_reward"), // 火热返奖
    COLD_REWARD(3, "cold_reward") // 冰冷返奖
}

