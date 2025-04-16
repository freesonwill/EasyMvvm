package arch.cayenne.lib.base.ui

import android.app.Activity
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.StatusBarConfig
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import arch.cayenne.lib.base.ui.interface_.IStatusBar


/**
 * @author: zhangsan
 * @date: 2025/3/31 14:26
 * @description:
 */
class StatusBarDelegate(private val activity: Activity) : IStatusBar {

    override fun setStatusBar( config: StatusBarConfig) {
        val immersionBar = ImmersionBar.with(activity)
        //如果全屏播放不用设置状态栏颜色
        if (config.hideStatusBar) {
            immersionBar.fullScreen(true) //启用全屏模式
            immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
        } else {
            immersionBar.statusBarColor(config.statusBarColor)//设置状态栏颜色
            immersionBar.hideBar(BarHide.FLAG_SHOW_BAR) //状态栏隐藏
                .fullScreen(false) //启用全屏模式
               // .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)   // 隐藏虚拟导航栏
                .navigationBarColor(config.statusBarColor) // 设置虚拟导航栏颜色
                .autoStatusBarDarkModeEnable(true,1.0f)
        }
        immersionBar.init()
    }
}