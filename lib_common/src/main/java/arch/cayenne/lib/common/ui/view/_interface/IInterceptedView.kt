package arch.cayenne.lib.common.ui.view._interface

import androidx.annotation.IntDef

/**
 * @date: 2025/8/15 17:08
 * @description: 手势拦截接口
 */
interface IInterceptedView {
    /**
     *  获取当前拦截的方向
     * @return  返回拦截的方向列表
     *
     * @example
     * listOf(Direction.UP, Direction.DOWN) //表示拦截上滑和下滑
     * listOf(Direction.NONE) //表示不拦截
     */
    fun getInterceptedDirections(): List<@Direction.Flag Int>

    /**
     * 设置拦截的方向
     * @param direction
     *
     * @example
     * setInterceptedDirection(Direction.UP) //拦截上滑和下滑
     * setInterceptedDirection(Direction.UP or Direction.LEFT) //拦截上滑和下滑
     */
    fun setInterceptedDirection(@Direction.Flag direction: Int)
}

object Direction {
    const val NONE = 0x00 //不拦截
    const val UP = 1 shl 0 //上滑
    const val DOWN = 1 shl 1 //下滑
    const val LEFT = 1 shl 2 //左滑
    const val RIGHT = 1 shl 3 //右滑
    private val ALL_FLAGS = listOf(UP, DOWN, LEFT, RIGHT)

    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
    @IntDef(flag = true, value = [NONE, UP, DOWN, LEFT, RIGHT])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Flag

    internal fun @receiver:Flag Int.split(): List<@Flag Int> {
        val directions = ALL_FLAGS.filter { this.has(it) }
        return directions.ifEmpty { listOf(NONE) }
    }

    internal fun List<@Flag Int>.merge(): Int {
        return if (this.isEmpty()) {
            NONE
        } else {
            this.reduce { acc, dir -> acc or dir }
        }
    }

    internal fun @receiver:Flag Int.has(@Flag flag: Int): Boolean {
        return this and flag != 0
    }

    internal fun @receiver:Flag Int.add(@Flag flag: Int): @Flag Int {
        return this or flag
    }

    internal fun @receiver:Flag Int.remove(@Flag flag: Int): @Flag Int {
        return this and flag.inv()
    }
}