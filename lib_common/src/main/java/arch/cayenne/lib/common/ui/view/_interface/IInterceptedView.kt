package arch.cayenne.lib.common.ui.view._interface

/**
 * @date: 2025/8/15 17:08
 * @description: 手势拦截接口
 */
interface IInterceptedView {
    fun getInterceptedDirections(): List<Direction>
    fun setInterceptedDirection(first: Direction, vararg other: Direction)
}

enum class Direction(val v: Int) {
    NONE(0x00), //不拦截
    UP(0x01 shl 0), //上滑
    DOWN(0x01 shl 1), //下滑
    LEFT(0x01 shl 2),//左滑
    RIGHT(0x01 shl 3) //右滑
}