package arch.cayenne.lib.base.data

/**
 * @author: aquan
 * @date: 2025/5/24 10:45
 * @description:
 */
enum class StatusBarMode {
    DEFAULT,         // 默认行为（状态栏下布局）
    FULLSCREEN,      // 全屏沉浸式，状态栏透明，内容顶到状态栏
    DRAW_BEHIND      // 内容绘制在状态栏之下，但非真正沉浸（状态栏可见）
}