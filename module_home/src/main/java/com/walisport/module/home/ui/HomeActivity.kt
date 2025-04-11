package com.walisport.module.home.ui

import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.common.ui.BaseNavActivity
import com.walisport.module.home.R
import kotlin.reflect.KClass

class HomeActivity : BaseNavActivity<EmptyViewModel>() {

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun navigationID(): Int {
        return R.navigation.nav_graph_home
    }
}