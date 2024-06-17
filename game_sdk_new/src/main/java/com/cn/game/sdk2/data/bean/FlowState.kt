package com.cn.game.sdk2.data.bean

/**
 * 流状态
 */
sealed class FlowState<T> {
    data class Start<T>(val data: T? = null) : FlowState<T>()
    data class Progress<T>(val progress: Int, val total: Int, val data: T? = null) : FlowState<T>()
    data class Success<T>(val data: T? = null) : FlowState<T>()
    data class Failed<T>(val code: Int, val err: String?) : FlowState<T>()
}