package com.coolone.lib_base.base.inner

import com.coolone.lib_base.network.manager.NetState

/**
 * 网络接口
 */
interface INetView {
    /**
     * 网络状态变化
     */
    fun onNetworkStateChanged(netState: NetState)

    /**
     * 发起网络请求
     */

}