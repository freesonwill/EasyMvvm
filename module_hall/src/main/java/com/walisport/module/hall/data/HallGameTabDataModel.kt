package com.walisport.module.hall.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.fragment.BaseFragment

data class HallGameTabDefault(
    @ColorRes val colorRes: Int =  arch.cayenne.lib.common.R.color.color_00E0E5,
    @DrawableRes val res: Int,
    private val _title: String,
    private val _page: (() -> BaseFragment<*, *>)
) : HallGamePage(_title, _page)

data class HallGameTab(
    val res: String,
    private val _title: String,
    private val _page: (() -> BaseFragment<*, *>)
) : HallGamePage(_title, _page)

open class HallGamePage(
    title: String,
    page: (() -> BaseFragment<*, *>)
) : PagerBean(title, page)



