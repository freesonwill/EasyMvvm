package arch.cayenne.lib.base.ui.delegate

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.utils.LogUtils


/**
 * @author: zhangsan
 * @date: 2025/3/31 14:26
 * @description:
 */
class StatusBarDelegate : IStatusBar {
    private var viewPaddingTop: Int = -1
    private var immersionBar: ImmersionBar

    constructor(activity: Activity) {
        immersionBar = ImmersionBar.with(activity)
    }

    constructor(fragment: Fragment) {
        immersionBar = ImmersionBar.with(fragment)
    }

    constructor(fragment: DialogFragment) {
        fragment.dialog?.window?.let { window ->
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        immersionBar = ImmersionBar.with(fragment)
    }

    override fun setStatusBar(config: StatusBarConfig, view: View) {
        LogUtils.d("DialogFragment", "-----DialogFragment--setStatusBar${config.statusBarDarkFont}")
        immersionBar.statusBarDarkFont(config.statusBarDarkFont, 0.2f)
            .navigationBarDarkIcon(config.statusBarDarkFont) // true 表示使用深色图标，false 表示浅色图标
        val statusBarHeight = ImmersionBar.getStatusBarHeight(view.context)
        //如果动态改变rootViewPaddingTop的高度,需动态调用StatusBarConfig.rootViewPaddingTop设置高度
        if (viewPaddingTop == -1) {
            viewPaddingTop = view.paddingTop
        }
        //默认
        when (config.statusBarType) {
            StatusBarMode.DEFAULT -> {
                view.fitsSystemWindows = false
                immersionBar.statusBarColor(config.statusBarColor)//设置状态栏颜色
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.init()
                setViewPadding(
                    view,
                    viewPaddingTop + statusBarHeight,
                    view.paddingBottom
                )
            }
            //全屏
            StatusBarMode.FULLSCREEN -> {
                immersionBar.fullScreen(true) //启用全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
                immersionBar.init()
            }
            //顶部沉浸式
            //ImmersionBar实现状态栏和底部虚拟home键透明
            StatusBarMode.DRAW_BEHIND -> {
                view.fitsSystemWindows = false
                val navigationBarHeight = ImmersionBar.getNavigationBarHeight(view.context)
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式

                    .transparentStatusBar() // 设置状态栏透明
                    .transparentNavigationBar() // 设置导航栏透明
                immersionBar.init()
                setViewPadding(
                    view,
                    viewPaddingTop + statusBarHeight,
                    navigationBarHeight
                )
            }
        }
    }

    private fun setViewPadding(v: View, statusBarHeight: Int, navigationBarHeight: Int) {
        v.post {
            v.setPadding(
                v.paddingLeft,
                statusBarHeight, // paddingTop 设置为状态栏高度
                v.paddingRight,
                navigationBarHeight
            )
        }
    }
}
