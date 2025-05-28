package arch.cayenne.lib.base.ui._interface

import android.app.Activity
import android.view.View
import arch.cayenne.lib.base.data.model.StatusBarConfig
import com.gyf.immersionbar.ImmersionBar

/**
 * 状态栏StatusBar
 * @author: zhangsan
 * @date: 2025/3/31 14:24
 * @description:
 */
interface IStatusBar {
    /**
     * 配置StatusBar
     */
    fun configStatusBar(): StatusBarConfig = StatusBarConfig

    /**
     * 设置状态栏
     */
    fun setStatusBar(config: StatusBarConfig, view: View)
}



