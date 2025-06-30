package com.walisport.app.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.common.data.constants.AppNotifyBean
import arch.cayenne.lib.common.data.constants.CurConnectFailedType
import arch.cayenne.lib.common.ui.BaseNavActivity
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
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

class MainActivity : BaseNavActivity<MainViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<MainViewModel> = MainViewModel::class
    private val fabControlViewModel: FloatingButtonControlViewModel by viewModel()
    private val connectFailedViewModel: ConnectFailedViewModel by viewModel()
    private var fabFragment: Fragment? = null
    private var notifyFragment: Fragment? = null
    private var connectFailedFragment : ConnectFailedFragment? = null
    private val mHandler by lazy { WeakReferenceHandler(this) }

    companion object {
        const val OPEN_NOTIFY = 1
        const val CLOSE_NOTIFY = 2
        const val CLICK_EVENT = 3
        const val TOUCH_EVENT = 4
    }

    class WeakReferenceHandler(obj: MainActivity) : Handler(Looper.getMainLooper()) {
        private val mRef: WeakReference<MainActivity> = WeakReference(obj)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            mRef.get()?.run {
                when (msg.what) {
                    OPEN_NOTIFY -> {
                        val obj = msg.obj as AppNotifyBean?
                        (notifyFragment as? AppNotifyFragment)?.showNotifyMsg(obj)
                    }

                    CLICK_EVENT -> { //点击事件弹窗消失并跳转直播详情
                        (notifyFragment as? AppNotifyFragment)?.showExitAnimation()
                        (notifyFragment as? AppNotifyFragment)?.gotoMatchLive()
                    }

                    TOUCH_EVENT -> { //触摸事件只随手指移动并消失，不跳转直播详情
                        (notifyFragment as? AppNotifyFragment)?.showExitAnimation()
                    }

                    else -> (notifyFragment as? AppNotifyFragment)?.showExitAnimation()
                }
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        fabFragment = FloatingButtonFragment.newInstance().apply {
            show(this@MainActivity)
        }
        notifyFragment = AppNotifyFragment.newInstance().apply {
            show(this@MainActivity)
            //点击事件弹窗消失并跳转到直播详情页，触摸事件弹窗随手指移动后消失，不跳转直播详情页
            setOnItemClickListener(object : AppNotifyFragment.OnClickListener {
                override fun onDown() {
                    mHandler.removeCallbacksAndMessages(null)
                }

                override fun onTouch() {
                    mHandler.removeCallbacksAndMessages(null)
                    mHandler.sendEmptyMessage(TOUCH_EVENT)
                }

                override fun onClick() {
                    mHandler.removeCallbacksAndMessages(null)
                    mHandler.sendEmptyMessage(CLICK_EVENT)
                }
            })
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
            val msg = Message.obtain()
            msg.what = OPEN_NOTIFY
            msg.obj = it
            mHandler.sendMessage(msg)
            mHandler.sendEmptyMessageDelayed(CLOSE_NOTIFY, 3000L)
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
    }

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        return StatusBarConfig
    }

    override fun onDestroy() {
        super.onDestroy()
        //退出时移除所有未处理消息防止内存泄露
        mHandler.removeCallbacksAndMessages(null)
    }
}