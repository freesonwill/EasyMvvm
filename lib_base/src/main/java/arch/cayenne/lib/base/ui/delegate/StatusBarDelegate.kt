package arch.cayenne.lib.base.ui.delegate

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge


/**
 * @author: zhangsan
 * @date: 2025/3/31 14:26
 * @description:
 */
class StatusBarDelegate : IStatusBar {
    private var viewPaddingTop: Int = -1
    private var immersionBar: ImmersionBar
    private val TAG = "StatusBarDelegate"

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

    private fun getStatusBarHeight(view:View):Int{
        val windowInsetsCompat = ViewCompat.getRootWindowInsets(view)
        val topInset = windowInsetsCompat?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        //topInset比statusBarHeight准确（ROG手机）
        val ret = if(topInset == 0) ImmersionBar.getStatusBarHeight(view.context) else topInset
        //"topInset:$topInset,ret:$ret".logd()
        return ret
    }

    override fun setStatusBar(config: StatusBarConfig, view: View) {
        immersionBar.statusBarDarkFont(config.statusBarDarkFont, 0.2f)
            .navigationBarDarkIcon(config.statusBarDarkFont) // true 表示使用深色图标，false 表示浅色图标
        val statusBarHeight = getStatusBarHeight(view)
        //如果动态改变rootViewPaddingTop的高度,需动态调用StatusBarConfig.rootViewPaddingTop设置高度
        if (viewPaddingTop == -1) {
            //DEFAULT -> DRAW_BEHIND时，paddingTop已经多了状态栏高度
            viewPaddingTop = if(view.paddingTop != 0 && view.fitsSystemWindows && config.statusBarType == StatusBarMode.DRAW_BEHIND){
                view.paddingTop - statusBarHeight
            }else {
                view.paddingTop
            }
        }
        //默认
        when (config.statusBarType) {
            StatusBarMode.DEFAULT -> {
                immersionBar.statusBarColor(config.statusBarColor)//设置状态栏颜色
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.init()
                view.fitsSystemWindows = StatusBarConfig.fitsSystemWindows
            }
            //全屏
            StatusBarMode.FULLSCREEN -> {
                immersionBar.fullScreen(true) //启用全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
                immersionBar.init()
                view.fitsSystemWindows = StatusBarConfig.fitsSystemWindows
            }
            //顶部沉浸式
            //ImmersionBar实现状态栏和底部虚拟home键透明
            StatusBarMode.DRAW_BEHIND -> {
                val navigationBarHeight = ImmersionBar.getNavigationBarHeight(view.context)
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式
                    .transparentStatusBar() // 设置状态栏透明
                    .transparentNavigationBar() // 设置导航栏透明
                immersionBar.init()
                if(StatusBarConfig.noPaddingViewIds.isEmpty()) {
                    setViewPadding(view,
                        viewPaddingTop + statusBarHeight,
                        navigationBarHeight
                    )
                }else {
                    val noPaddingViewIds = StatusBarConfig.noPaddingViewIds
                    (view as ViewGroup).children.forEach { v ->
                        if (noPaddingViewIds.contains(v.id)) return@forEach
                        val lp = v.layoutParams as? MarginLayoutParams
                        if (lp != null) {
                            lp.topMargin += statusBarHeight
                        } else {
                            "view.layoutParams is not MarginLayoutParams".loge(TAG)
                        }
                    }
                }
                StatusBarConfig.noPaddingViewIds = emptyList()
                view.fitsSystemWindows = StatusBarConfig.fitsSystemWindows
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
