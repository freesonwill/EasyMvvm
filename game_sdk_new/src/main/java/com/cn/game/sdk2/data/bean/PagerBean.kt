package com.cn.game.sdk2.data.bean

import androidx.fragment.app.Fragment
import com.cn.game.sdk2.ui.page.fast3.BaseFast3Fragment

/***
 * TabLayout & ViewPager 所用
 * @param page 使用回調方式創建Fragment, 避免頁面留存造成洩漏
 */
open class PagerBean(
    val title: String,
    val page: (() -> Fragment)
)

class GamePageBean(
    title: String,
    page: (() -> BaseFast3Fragment<*, *>)
) : PagerBean(title, page)
