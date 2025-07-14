package arch.cayenne.lib.qyplayer

import com.xxx.qyplayer.PlayerConfig

/**
 * 全局保存一份配置，以最后一次设置为准
 */
class GlobalConfig {

    init {
        loadConfig()
    }

    var brightRatio = 0

    var inited = false

    var isAutoPlay = false

    var isLoop = false

    var reconnectTime = 0

    var isHWDecode = false

    // 镜像模式
    var flip = 0

    // 旋转模式
    var rotation = 0

    // 填充模式
    var fill = 0

    // 窗口比例
    var viewportRatio = 0

    var color = 0

    var isClear = false

    var isMute = false

    var volume = 0

    var audioDecrypt = 0

    var videoDecrypt = 0

    var render = 0

    var isSubtitles = false

    var reconnectCount = 0

    var isDanmaku = false

    private fun loadConfig() {
        inited = false
        brightRatio = -1
        isAutoPlay = false
        isLoop = false
        reconnectTime = 0
        isHWDecode = false
        flip = 0
        rotation = 0
        fill = 0
        viewportRatio = 0
        color = 0
        isClear = false
        isMute = false
        volume = 0
        audioDecrypt = 0
        videoDecrypt = 0
        render = 0
        isSubtitles = false
        isDanmaku = false
        reconnectCount = -1
    }
}

fun GlobalConfig.transformToPlayerConfig(): PlayerConfig = PlayerConfig().also {
    it.isAutoPlay = if (isAutoPlay) 1 else 0
    it.isLoop = if (isLoop) 1 else 0
    it.reconnectTime = reconnectTime
    it.isHWDecode = if (isHWDecode) 1 else 0
    it.flip = flip
    it.rotation = rotation
    it.fill = fill
    it.viewportRatio = viewportRatio
    it.color = color
    it.isClear = if (isClear) 1 else 0
    it.isMute = if (isMute) 1 else 0
    it.volume = volume
    it.audioDecrypt = audioDecrypt
    it.videoDecrypt = videoDecrypt
    it.render = render
    it.isSubtitles = if (isSubtitles) 1 else 0
    it.isDanmaku = if (isDanmaku) 1 else 0
    it.reconnectCount = reconnectCount
}

fun GlobalConfig.transformFromPlayerConfig(cfg: PlayerConfig) {
    isAutoPlay = cfg.isAutoPlay != 0
    isLoop = cfg.isLoop != 0
    reconnectTime = cfg.reconnectTime
    isHWDecode = cfg.isHWDecode != 0
    flip = cfg.flip
    rotation = cfg.rotation
    fill = cfg.fill
    viewportRatio = cfg.viewportRatio
    color = cfg.color
    isClear = cfg.isClear != 0
    isMute = cfg.isMute != 0
    volume = cfg.volume
    audioDecrypt = cfg.audioDecrypt
    videoDecrypt = cfg.videoDecrypt
    render = cfg.render
    isSubtitles = cfg.isSubtitles != 0
    isDanmaku = cfg.isDanmaku != 0
    reconnectCount = cfg.reconnectCount
}