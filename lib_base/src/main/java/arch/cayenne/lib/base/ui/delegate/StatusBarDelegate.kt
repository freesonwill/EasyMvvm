package arch.cayenne.lib.base.ui.delegate

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui._interface.IStatusBar
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar


/**
 * @author: zhangsan
 * @date: 2025/3/31 14:26
 * @description:
 */
class StatusBarDelegate : IStatusBar {
    private var viewPaddingTop: Int = -1
    private var immersionBar: ImmersionBar
    private val TAG = "StatusBarDelegate"
    private var mActivity: Activity
    private var isInit = false

    constructor(activity: Activity) {
        mActivity = activity
        immersionBar = ImmersionBar.with(activity)
    }

    constructor(fragment: Fragment) {
        mActivity = fragment.requireActivity()
        immersionBar = ImmersionBar.with(fragment)
    }

    constructor(fragment: DialogFragment) {
        mActivity = fragment.requireActivity()
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
        val window = mActivity.window
        val originUiFlag = window.decorView.systemUiVisibility
        immersionBar.statusBarDarkFont(config.statusBarDarkFont, 0.2f)
            .navigationBarDarkIcon(config.statusBarDarkFont) // true 表示使用深色图标，false 表示浅色图标
        val statusBarHeight = getStatusBarHeight(view)
        //如果动态改变rootViewPaddingTop的高度,需动态调用StatusBarConfig.rootViewPaddingTop设置高度
        if (viewPaddingTop == -1) {
            //DEFAULT -> DRAW_BEHIND时，paddingTop已经多了状态栏高度
            viewPaddingTop = if(view.paddingTop != 0 && view.fitsSystemWindows && config.statusBarType is StatusBarMode.DRAW_BEHIND){
                view.paddingTop - statusBarHeight
            }else {
                view.paddingTop
            }
        }
        //默认
        when (val statusBarMode = config.statusBarType) {
            StatusBarMode.DEFAULT -> {
                try {//防止找不到颜色报错
                    immersionBar.statusBarColor(config.statusBarColor)//设置状态栏颜色
                }catch (e:Exception){
                    e.printStackTrace()
                    immersionBar.statusBarColor(android.R.color.black)//设置状态栏颜色
                }
                immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                    .fullScreen(false) //退出全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.init()
                view.fitsSystemWindows = true
            }
            //全屏
            StatusBarMode.FULLSCREEN -> {
                immersionBar.fullScreen(true) //启用全屏模式
                    .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
                immersionBar.init()
                view.fitsSystemWindows = false
            }
            //顶部沉浸式
            //ImmersionBar实现状态栏和底部虚拟home键透明
            is StatusBarMode.DRAW_BEHIND -> {
                val navigationBarHeight = ImmersionBar.getNavigationBarHeight(view.context)
                if (!isInit || Build.VERSION.SDK_INT > 28) {
                    immersionBar
                        .hideBar(BarHide.FLAG_SHOW_BAR) //状态栏显示
                        .fullScreen(false) //退出全屏模式
                        .transparentStatusBar() // 设置状态栏透明
                    if (statusBarMode.autoPaddingNavigationBarColor){
                        immersionBar .navigationBarColor(config.navigationBarColorColor) // 设置虚拟导航栏颜色
                    }else{
                        immersionBar.transparentNavigationBar() // 设置导航栏透明
                    }
                    immersionBar.init()
                } else {
                    window.decorView.systemUiVisibility = if (config.statusBarDarkFont) {
                        originUiFlag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        originUiFlag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    }
                }

                if(statusBarMode.autoPadding) {
                    val noPaddingViewIds = statusBarMode.noPaddingViewIds
                    if(noPaddingViewIds.isEmpty()) {
                        setViewPadding(view,
                            viewPaddingTop + statusBarHeight,
                           if (statusBarMode.autoPaddingNavigationBarColor) 0 else navigationBarHeight
                        )
                    }else {
                        (view as ViewGroup).children.forEach { v ->
                            if (noPaddingViewIds.contains(v.id)) return@forEach
                            val lp = v.layoutParams as? MarginLayoutParams
                            if (lp != null) {
                                lp.topMargin += statusBarHeight
                            } else {
                                "${view}.layoutParams is not MarginLayoutParams".loge(TAG)
                            }
                        }
                    }
                }
                view.fitsSystemWindows = false
            }
        }
        isInit = true
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
