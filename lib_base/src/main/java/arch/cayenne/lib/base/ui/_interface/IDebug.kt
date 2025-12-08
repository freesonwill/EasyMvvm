package arch.cayenne.lib.base.ui._interface

/**
 * @date: 2025/12/8 14:33
 * @description:调试接口
 */
interface IDebug {
    /**
     * 是否追踪加载时间（DEBUG用）
     */
    fun enableTrackLoadTime() = false

    /**
     * 是否开启日志
     */
    fun logEnabled() = true
}