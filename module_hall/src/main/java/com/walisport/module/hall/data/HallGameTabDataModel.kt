package com.walisport.module.hall.data

import androidx.annotation.DrawableRes
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.fragment.BaseFragment

data class HallGameTabDefault(
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



