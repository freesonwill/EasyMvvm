package arch.cayenne.lib.base.ui.interface_

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.StatusBarConfig

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
    fun setStatusBar(config: StatusBarConfig)
}



