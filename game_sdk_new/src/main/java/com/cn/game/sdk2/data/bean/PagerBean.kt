package com.cn.game.sdk2.data.bean

import androidx.fragment.app.Fragment

/***
 * TabLayout & ViewPager 所用
 * @param page 使用回調方式創建Fragment, 避免頁面留存造成洩漏
 */
data class PagerBean(
    val title: String,
    val page: (() -> Fragment)
)
