package arch.cayenne.lib.base.ui._interface

import android.os.Bundle

/**
 * @date: 2025/12/16 15:01
 * @description: Fragment参数变化监听接口
 */
interface IFragment {
    /**
     * 处理Fragment返回事件，返回true表示已处理，false表示未处理
     */
    fun onBackPressed():Boolean = false

    /**
     * 参数变化回调
     * @param oldArgs 旧参数
     * @param newArgs 新参数
     */
    suspend fun onArgumentsChanged(oldArgs: Bundle?, newArgs: Bundle?) {}
}