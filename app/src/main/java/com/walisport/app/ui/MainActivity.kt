package com.walisport.app.ui


import android.os.Bundle
import com.walisport.app.R
import com.walisport.app.data.AppNavViewModel
import arch.cayenne.lib.common.ui.BaseNavActivity
import kotlin.reflect.KClass

class MainActivity : BaseNavActivity<AppNavViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<AppNavViewModel> = AppNavViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initData() {
        super.initData()
    }
}