package arch.cayenne.lib.base.data.model

import arch.cayenne.lib.base.data.StatusBarMode


/**
 * 状态栏配置
 * @property statusBarColor 状态栏颜色
 * @property hideStatusBar 是否隐藏状态栏
 */
object  StatusBarConfig  {
    //状态栏文字颜色 false  statusBarDarkFont(false) 亮色 statusBarDarkFont(true) 暗色
    var statusBarDarkFont : Boolean = false
    //状态栏颜色
    var statusBarColor: Int = android.R.color.black
    //状态栏模式
    var statusBarType: StatusBarMode = StatusBarMode.DRAW_BEHIND
}
