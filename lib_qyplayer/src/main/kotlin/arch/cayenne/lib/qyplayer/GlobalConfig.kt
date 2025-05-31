package arch.cayenne.lib.qyplayer

import android.content.Context
import androidx.core.content.edit
import com.xxx.qyplayer.PlayerConfig
import java.io.File

/**
 * 全局保存一份配置，以最后一次设置为准
 */
class GlobalConfig(private val context: Context) {
    private val kSPFileName = "player_config"
    private val kInited = "inited"

    val SNAP_SHOT_PATH: String = "snapShot" + File.separator

    private val kBrightRatio = "bright_ratio"
    private val kAutoPlay = "auto_play"
    private val kLoop = "loop"
    private val kReconnectTime = "reconnect_time"
    private val kHWDecode = "hw_decode"
    private val kFlip = "flip"
    private val kRotation = "rotation"
    private val kFill = "fill"
    private val kViewportRatio = "viewport_ratio"
    private val kColor = "color"
    private val kClear = "clear"
    private val kMute = "mute"
    private val kVolume = "volume"
    private val kAudioDecrypt = "audio_decrypt"
    private val kVideoDecrypt = "video_decrypt"
    private val kRender = "render"
    private val kSubtitles = "subtitles"
    private val kDanmaku = "danmaku"
    private val kReconnectCount = "reconnect_count"

    init {
        loadConfig()
    }

    var brightRatio = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kBrightRatio, v)
            }
        }

    var inited = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kInited, v)
            }
        }

    var isAutoPlay = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kAutoPlay, v)
            }
        }

    var isLoop = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kLoop, v)
            }
        }

    var reconnectTime = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kReconnectTime, v)
            }
        }

    var isHWDecode = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kHWDecode, v)
            }
        }

    // 镜像模式
    var flip = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kFlip, v)
            }
        }

    // 旋转模式
    var rotation = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kRotation, v)
            }
        }

    // 填充模式
    var fill = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kFill, v)
            }
        }

    // 窗口比例
    var viewportRatio = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kViewportRatio, v)
            }
        }

    var color = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kColor, v)
            }
        }

    var isClear = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kClear, v)
            }
        }

    var isMute = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kMute, v)
            }
        }

    var volume = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kVolume, v)
            }
        }

    var audioDecrypt = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kAudioDecrypt, v)
            }
        }

    var videoDecrypt = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kVideoDecrypt, v)
            }
        }

    var render = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kRender, v)
            }
        }

    var isSubtitles = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kSubtitles, v)
            }
        }

    var reconnectCount = 0
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putInt(kReconnectCount, v)
            }
        }

    var isDanmaku = false
        set(v) {
            if (field == v) {
                return
            }
            field = v
            context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE).edit {
                putBoolean(kDanmaku, v)
            }
        }

    private fun loadConfig() {
        val sp = context.getSharedPreferences(kSPFileName, Context.MODE_PRIVATE)

        inited = sp.getBoolean(kInited, false)
        brightRatio = sp.getInt(kBrightRatio, -1)
        isAutoPlay = sp.getBoolean(kAutoPlay, false)
        isLoop = sp.getBoolean(kLoop, false)
        reconnectTime = sp.getInt(kReconnectTime, 0)
        isHWDecode = sp.getBoolean(kHWDecode, false)
        flip = sp.getInt(kFlip, 0)
        rotation = sp.getInt(kRotation, 0)
        fill = sp.getInt(kFill, 0)
        viewportRatio = sp.getInt(kViewportRatio, 0)
        color = sp.getInt(kColor, 0)
        isClear = sp.getBoolean(kClear, false)
        isMute = sp.getBoolean(kMute, false)
        volume = sp.getInt(kVolume, 0)
        audioDecrypt = sp.getInt(kAudioDecrypt, 0)
        videoDecrypt = sp.getInt(kVideoDecrypt, 0)
        render = sp.getInt(kRender, 0)
        isSubtitles = sp.getBoolean(kSubtitles, false)
        isDanmaku = sp.getBoolean(kDanmaku, false)
        reconnectCount = sp.getInt(kReconnectCount, -1)
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