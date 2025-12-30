package com.walisport.module.search.ui.model

/**
 * 搜索模組內部使用的 Avatar 結構，對齊 hall 模組的 Avatar。
 */
data class Avatar(
    val url: String,
    val thumbhash: String,
    val css: String,
)

/**
 * 搜索結果頁中的遊戲卡片資料模型。
 * 結構與 hall 模組的 GameContentData 對齊（欄位命名保持一致），但定義在 search 模組內，避免模組依賴。
 */
data class SearchGameContentData(
    val id: Long,                 // 遊戲 id
    val name: String,             // 遊戲名稱
    val avatar: Avatar,           // 遊戲 icon（url + thumbHash）
    val online: Int,              // 在線人數
    val reward: Double,           // 返獎率
    val hasMore: Boolean,         // 是否有返獎
    val hotOrCold: HotColdType,   // 熱／冷標記
)

/**
 * 遊戲熱度標記：火熱 / 冰冷 / 無。
 * 命名與 hall 模組的 HotColdType 對齊。
 */
enum class HotColdType {
    NONE,
    HOT,
    COLD
}


