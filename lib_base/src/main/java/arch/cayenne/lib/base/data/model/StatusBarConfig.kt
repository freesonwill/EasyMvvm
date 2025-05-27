package arch.cayenne.lib.base.data.model

import arch.cayenne.lib.base.data.StatusBarEnum


/**
 * 状态栏配置
 * @property statusBarColor 状态栏颜色
 * @property hideStatusBar 是否隐藏状态栏
 */
object  StatusBarConfig  {

    //记录root 顶部内边距
    var rootViewPaddingTop: Int = -1

    var statusBarDarkFont : Boolean = false
     var keySkin  : String  = ""
    //状态栏颜色
    var statusBarColor: Int = android.R.color.black
    //状态栏模式
    var statusBarType: StatusBarEnum = StatusBarEnum.DEFAULT
}
