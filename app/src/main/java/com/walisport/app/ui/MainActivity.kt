package com.walisport.app.ui


import android.os.Bundle
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.walisport.app.R
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.app.ui.viewmodel.MainViewModel
import kotlin.reflect.KClass

class MainActivity : BaseNavActivity<MainViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<MainViewModel> = MainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initData() {
        super.initData()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.betResultListener.observe(this) {
            val toast = BetResultToastView(this@MainActivity)
            toast.setResult(it)
            showToast(toast, 3_000L)
        }
    }


    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.hideStatusBar = false
        return StatusBarConfig
    }
}