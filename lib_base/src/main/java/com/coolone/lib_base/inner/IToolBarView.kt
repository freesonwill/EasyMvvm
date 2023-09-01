package com.coolone.lib_base.inner

import androidx.annotation.LayoutRes
import com.coolone.lib_base.R

/**
 * Description:
 * author       : baoyuedong
 * email        : baoyuedong@tsenf.io
 * createTime   : 2023/9/1 14:50
 **/
interface IToolBarView {

    @LayoutRes
    fun toolBarLayoutId(): Int {
        return R.layout.common_toolbar
    }
}