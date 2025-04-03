package com.walisport.module.home.test

import androidx.fragment.app.Fragment
import com.walisport.lib.base.ui.BaseFragment

/**
 * @author: zhangsan
 * @date: 2025/3/30 23:13
 * @description:
 */
/***
 * TabLayout & ViewPager 所用
 * @param page 使用回調方式創建Fragment, 避免頁面留存造成洩漏
 */
open class PagerBean(
    val title: String,
    val fragment: Fragment,
    val page: (() -> Fragment)
)