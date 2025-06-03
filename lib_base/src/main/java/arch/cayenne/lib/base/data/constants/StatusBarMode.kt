package arch.cayenne.lib.base.data.constants

/**
 * @author: aquan
 * @date: 2025/5/24 10:45
 * @description:
 */
enum class StatusBarMode {
    DEFAULT,         // 默认行为（状态栏下布局）
    FULLSCREEN,      // 全屏沉浸式，状态栏透明，内容顶到状态栏
    DRAW_BEHIND      // 布局顶到状态栏，布局内容自动padding到状态栏下面，图片沉浸式可用这个
}