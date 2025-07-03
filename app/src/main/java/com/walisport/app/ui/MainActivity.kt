package com.walisport.app.ui

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.common.data.constants.CurConnectFailedType
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.fragment.ConnectFailedFragment
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.ui.viewmodel.ConnectFailedViewModel
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import com.walisport.app.R
import com.walisport.app.ui.viewmodel.MainViewModel
import com.walisport.module.message.ui.fragment.AppNotifyFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass
import kotlin.system.exitProcess

class MainActivity : BaseNavActivity<MainViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<MainViewModel> = MainViewModel::class
    private val fabControlViewModel: FloatingButtonControlViewModel by viewModel()
    private val connectFailedViewModel: ConnectFailedViewModel by viewModel()
    private var connectFailedFragment : ConnectFailedFragment? = null
    private val fabFragment: FloatingButtonFragment by lazy {
        FloatingButtonFragment.newInstance()
    }
    private val notifyFragment: AppNotifyFragment by lazy {
        AppNotifyFragment.newInstance()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        fabFragment.show(this)
        notifyFragment.show(this)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.betResultListener.observe(this) {
            val toast = BetResultToastView(this@MainActivity)
            toast.setResult(it)
            showToast(toast, 3_000L)
        }
        mViewModel.appNotifyListener.observe(this) {
            notifyFragment.sendNotifyMsg(it)
        }
        fabControlViewModel.isShowButtonListener.observe(this) {
            if (it) {
                fabFragment.show()
            } else {
                fabFragment.hide()
            }
        }
        fabControlViewModel.onClickAnimationListener.observe(this) { (x, y) ->
            fabFragment.showDotAnimation(x, y)
        }
        mViewModel.connectStateChange.observe(this) {
            if (connectFailedFragment == null) {
                connectFailedFragment = ConnectFailedFragment.newInstance().apply {
                    show(this@MainActivity)
                    setRefreshListener {
                        mViewModel.reconnectNow()
                    }
                }
            }
            connectFailedViewModel.changeCurrencyFailedView(
                when(it) {
                    is ConnectState.ConnectSuccess -> CurConnectFailedType.HIDE
                    is ConnectState.ReconnectFailure -> CurConnectFailedType.SHOW_FAILED
                    else -> CurConnectFailedType.SHOW_MASK
                }
            )
        }
        mViewModel.aberrantNotify.observe(this) { code ->
            if (code == 1) {
                CommonDialog.newInstance(
                    title = getString(R.string.multiple_logins_title),
                    message = getString(R.string.multiple_logins_message),
                    okText = getString(R.string.enter)
                ).apply {
                    setOnOkClickListener {
                        exitProcess(0)
                    }
                }.show(supportFragmentManager)
            }else if (code == 2) {
                //TODO go to login page
            }
        }
    }

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        return StatusBarConfig
    }
}