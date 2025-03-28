package com.walisport.module_home.ui

import com.walisport.lib_common.ui.BaseNavActivity
import com.walisport.module_home.R

class HomeActivity : BaseNavActivity() {

    override fun navigationID(): Int {
        return R.navigation.nav_graph_home
    }

}