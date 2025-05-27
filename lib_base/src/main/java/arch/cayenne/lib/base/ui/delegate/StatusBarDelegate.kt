package arch.cayenne.lib.base.ui.delegate

import android.app.Activity
import android.view.View
import android.view.WindowManager
import arch.cayenne.lib.base.data.StatusBarMode
import arch.cayenne.lib.base.data.model.SkinType
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
class StatusBarDelegate(private val activity: Activity) : IStatusBar {
    private var viewPaddingTop :Int = -1



    override fun setStatusBar(config: StatusBarConfig, view: View) {
        val immersionBar = ImmersionBar.with(activity)
        immersionBar.statusBarDarkFont(config.statusBarDarkFont)
        val statusBarHeight = ImmersionBar.getStatusBarHeight(activity)
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
                LogUtils.e("setStatusBar-----DEFAULT--------view-${viewPaddingTop}")
                setViewPadding(
                    view,
                    viewPaddingTop+statusBarHeight,
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
                val navigationBarHeight = ImmersionBar.getNavigationBarHeight(activity)
                LogUtils.e("setStatusBar-------------view-${view},statusBarHeightv${statusBarHeight}---,vnavigationBarHeight${navigationBarHeight}")
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式
                    .transparentStatusBar() // 设置状态栏透明
                    .transparentNavigationBar() // 设置导航栏透明
                immersionBar.init()
                setViewPadding(
                    view,
                    viewPaddingTop+statusBarHeight,
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

