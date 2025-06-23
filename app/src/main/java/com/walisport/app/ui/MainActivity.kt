package com.walisport.app.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import com.walisport.app.R
import com.walisport.app.ui.viewmodel.MainViewModel
import com.walisport.module.message.ui.fragment.AppNotifyFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class MainActivity : BaseNavActivity<MainViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<MainViewModel> = MainViewModel::class
    private val fabControlViewModel: FloatingButtonControlViewModel by viewModel()
    private var fabFragment: Fragment? = null
    private var notifyFragment: Fragment? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        fabFragment = FloatingButtonFragment.newInstance().apply {
            show(this@MainActivity)
        }
        notifyFragment = AppNotifyFragment.newInstance().apply {
            show(this@MainActivity)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.betResultListener.observe(this) {
            val toast = BetResultToastView(this@MainActivity)
            toast.setResult(it)
            showToast(toast, 3_000L)
        }
        mViewModel.appNotifyListener.observe(this) {
            notifyFragment?.view?.visibility = View.VISIBLE
            (notifyFragment as AppNotifyFragment).setNotifyMsg(it)
            (notifyFragment as AppNotifyFragment).showEnterAnimation()
        }
        fabControlViewModel.isShowButtonListener.observe(this) {
            if (it) {
                fabFragment?.view?.visibility = View.VISIBLE
            } else {
                fabFragment?.view?.visibility = View.GONE
            }
        }
        fabControlViewModel.onClickAnimationListener.observe(this) { (x, y) ->
            (fabFragment as? FloatingButtonFragment)?.showDotAnimation(x, y)
        }
    }

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        return StatusBarConfig
    }
}