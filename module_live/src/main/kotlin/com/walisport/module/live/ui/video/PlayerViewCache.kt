package com.walisport.module.live.ui.video

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.qyplayer.ui.widget.LivePlayerView

/**
 * @description: LivePlayView的cache,  作用是在视频横竖屏切换时，共享LivePlayView实例， 实现横竖屏切换时的无缓冲播放效果
 */
object PlayerViewCache {

    private val map: HashMap<LivePlayerView, Int> = HashMap()

    /**
     * 获取LivePlayerView实例
     * @param createInstanceAction 创建实例操作
     */
    fun acquirePlayerView(createInstanceAction: () -> LivePlayerView): LivePlayerView {
        val playerView = if (map.isNotEmpty()) {
            map.keys.first()
        } else {
            createInstanceAction.invoke()
        }

        var refCount = map.getOrDefault(playerView, 0)
        refCount++

        map[playerView] = refCount

        return playerView
    }

    /**
     * 释放LivePlayerView引用。如果引用降到0，对该实例执行destroy操作
     * @param playerView
     * @param noRefAction: 引用降到0时，对LivePlayerView的操作
     */
    fun releasePlayerView(playerView: LivePlayerView, noRefAction: (LivePlayerView) -> Unit) {
        var refCount = map.getOrDefault(playerView, 0)
        refCount--

        if (refCount > 0) {
            //引用计数大于0
            map[playerView] = refCount
        } else {
            //引用计数小于等于0时，从map中移除实例， 同时对实例执行清理操作
            map.remove(playerView)
            "noRefAction.invoke".logd("PlayerViewCache")
            noRefAction.invoke(playerView)
        }
    }
}