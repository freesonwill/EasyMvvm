package arch.cayenne.lib.base.data.constants

import androidx.annotation.IdRes

/**
 * @author: aquan
 * @date: 2025/5/24 10:45
 * @description:
 */
sealed class StatusBarMode {
    // 默认行为（状态栏下布局）
    data object DEFAULT: StatusBarMode()
    // 全屏沉浸式，状态栏透明，内容顶到状态栏，view自动偏移
    data object FULLSCREEN: StatusBarMode()
    // 布局顶到状态栏，布局内容自动padding到状态栏下面，图片沉浸式可用这个
    data class DRAW_BEHIND(
        val autoPadding:Boolean = true, //是否自动padding到状态栏下
        @IdRes val noPaddingViewIds: List<Int> = emptyList() //不处理 padding 的 View ID
    ): StatusBarMode()
}
