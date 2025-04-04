package com.walisport.module.home.test

import com.walisport.lib.common.ui.BaseNavActivity
import com.walisport.module.home.R

class TestActivity : BaseNavActivity() {

    override fun navigationID(): Int {
        return R.navigation.nav_graph_test
    }
}