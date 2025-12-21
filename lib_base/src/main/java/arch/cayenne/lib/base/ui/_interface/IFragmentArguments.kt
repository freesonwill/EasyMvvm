package arch.cayenne.lib.base.ui._interface

import android.os.Bundle

/**
 * @date: 2025/12/16 15:01
 * @description: Fragment参数变化监听接口
 */
interface IFragmentArguments {
    suspend fun onArgumentsChanged(oldArgs: Bundle?, newArgs: Bundle?) {}
}