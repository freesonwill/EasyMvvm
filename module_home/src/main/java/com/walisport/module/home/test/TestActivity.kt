package com.walisport.module.home.test

import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.common.ui.BaseNavActivity
import com.walisport.module.home.R
import kotlin.reflect.KClass

class TestActivity : BaseNavActivity<EmptyViewModel>() {

    override fun navigationID(): Int {
        return R.navigation.nav_graph_test
    }

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
}