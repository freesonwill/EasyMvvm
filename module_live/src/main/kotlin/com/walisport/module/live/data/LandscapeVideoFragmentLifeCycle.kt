package com.walisport.module.live.data

import arch.cayenne.lib.base.data.model.UnPeekLiveData

/**
 *
 * @date: 2025/6/10 17:12
 * @description: 横屏视频fragment的生命期监听
 */
class LandscapeVideoFragmentLifeCycle {
    private val _destroyedEvent: UnPeekLiveData<Boolean> = UnPeekLiveData()

    /**
     * 销毁事件
     */
    val destroyedEvent: UnPeekLiveData<Boolean> = _destroyedEvent

}