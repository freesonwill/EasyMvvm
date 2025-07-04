package arch.cayenne.lib.qyplayer.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Surface
import android.widget.FrameLayout
import arch.cayenne.lib.qyplayer.render.IRenderCallback
import arch.cayenne.lib.qyplayer.render.IRenderView
import arch.cayenne.lib.qyplayer.render.SurfaceRenderView
import arch.cayenne.lib.qyplayer.render.TextureRenderView
import com.xxx.qyplayer.DecryptMode
import com.xxx.qyplayer.MediaInfo
import com.xxx.qyplayer.MirrorMode
import com.xxx.qyplayer.PlayerConfig
import com.xxx.qyplayer.PlayerMode
import com.xxx.qyplayer.QYPlayer
import com.xxx.qyplayer.RotateMode
import com.xxx.qyplayer.ScaleMode
import com.xxx.qyplayer.StateInfo
import com.xxx.qyplayer.ViewportRatioMode

enum class SurfaceType {
    /**
     * TextureView
     */
    TEXTURE_VIEW,

    /**
     * SurfacView
     */
    SURFACE_VIEW
}

class QYRenderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val mQYPlayer = QYPlayer(context)
    private var mIRenderView: IRenderView? = null
    private var mSurface: Surface? = null

    private var mOnSurfaceCreatedListener: (() -> Unit)? = null

    /**
     * 初始化播放器
     */
    fun init(mode: PlayerMode) {
        mQYPlayer.init(mode)
    }

    /**
     * 该方法需要在创建播放器完成后,prepare前调用
     * @param surfaceType  Surface的类型
     */
    fun setSurfaceType(surfaceType: SurfaceType) {
        mIRenderView = if (surfaceType == SurfaceType.TEXTURE_VIEW) {
            TextureRenderView(context)
        } else {
            SurfaceRenderView(context)
        }
        mIRenderView?.addRenderCallback(object : IRenderCallback {
            override fun onSurfaceCreate(surface: Surface) {
                mSurface = surface
                mQYPlayer.setSurface(surface)
                mOnSurfaceCreatedListener?.invoke()
            }

            override fun onSurfaceChanged(width: Int, height: Int) {
                mQYPlayer.setSurfaceChanged(width, height)
            }

            override fun onSurfaceDestroyed() {
                mQYPlayer.setSurface(null)
            }

        })
        addView(mIRenderView?.getView())
    }

    fun setOnUpdateStatisticsListener(onUpdateStatistics: (category: String, json: String) -> Unit) {
        mQYPlayer.setOnUpdateStatisticsListener(onUpdateStatistics)
    }

    fun setOnSnapshotListener(onSnapshot: (bitmap: Bitmap) -> Unit) {
        mQYPlayer.setOnSnapShotListener { bitmap ->
            onSnapshot(bitmap)
        }
    }

    fun prepare() {
        mQYPlayer.prepare()
    }

    fun setDataSource(url: String) {
        mQYPlayer.setDataSource(url)
    }

    fun pause() {
        mQYPlayer.pause()
    }

    fun start() {
        mQYPlayer.start()
    }

    fun setAutoPlay(isAutoPlay: Boolean) {
        mQYPlayer.setAutoPlay(isAutoPlay)
    }

    fun setLoop(isLoop: Boolean) {
        mQYPlayer.setLoop(isLoop)
    }

    fun setMute(isMute: Boolean) {
        mQYPlayer.setMute(isMute)
    }

    fun setHwDecode(hwDecode: Boolean) {
        mQYPlayer.setHwDecode(hwDecode)
    }

    fun setReconnectTime(intervalTime: Int) {
        mQYPlayer.setReconnectTime(intervalTime)
    }

    fun reload() {
        mQYPlayer.reload()
    }

    fun setIsClearScreen(isClear: Boolean) {
        mQYPlayer.setIsClearScreen(isClear)
    }

    fun setVideoBackgroundColor(color: Int): Int {
        return mQYPlayer.setBackgroundColor(color)
    }

    fun setOnlyAudio(isOnlyAudio: Boolean) {
        mQYPlayer.setOnlyAudio(isOnlyAudio)
    }

    fun stop() {
        mQYPlayer.stop()
    }

    fun getDuration(): Long {
        return mQYPlayer.getDuration()
    }

    fun setConfig(cfg: PlayerConfig) {
        mQYPlayer.setConfig(cfg)
    }

    fun getConfig(): PlayerConfig {
        return mQYPlayer.getConfig()
    }

    fun getMediaInfo(): MediaInfo {
        return mQYPlayer.getMediaInfo()
    }

    /**
     * 保存截图
     *
     * @param path 指定保存路径，如果需要保存到外部存储区，请确保有权限；否则传空字符即可，数据将通过截图回调获取
     */
    fun snapshot(fileType: Int, path: String): Int {
        return mQYPlayer.snapshot(fileType, path)
    }

    fun setMirrorMode(mode: MirrorMode): Int {
        return mQYPlayer.setMirrorMode(mode)
    }


    fun setRotateMode(mode: RotateMode): Int {
        return mQYPlayer.setRotateMode(mode)
    }


    fun setScaleMode(mode: ScaleMode): Int {
        return mQYPlayer.setScaleMode(mode)
    }


    fun setViewportRatioMode(mode: ViewportRatioMode): Int {
        return mQYPlayer.setViewportRatioMode(mode)
    }

    fun seekTo(position: Long) {
        mQYPlayer.seekTo(position)
    }

    fun setOnStateChangedListener(onStateChanged: (state: StateInfo) -> Unit) {
        mQYPlayer.setOnStateChangedListener { state ->
            onStateChanged(state)
        }
    }

    fun setOnPreparedListener(onPrepared: () -> Unit) {
        mQYPlayer.setOnPreparedListener(onPrepared)
    }

    fun setOnSurfaceCreatedListener(onSurfaceCreated: () -> Unit) {
        mOnSurfaceCreatedListener = onSurfaceCreated
    }

    fun setOnFirstFrameReceivedListener(onFirstFrameReceived: (data: ByteArray, size: Int, pts: Long, type: Int, width: Int, height: Int) -> Unit) {
        mQYPlayer.setOnFirstFrameReceivedListener(onFirstFrameReceived)
    }

    fun setOnFirstDataReceivedListener(onFirstDataReceived: (data: ByteArray, size: Int, pts: Long, type: Int) -> Unit) {
        mQYPlayer.setOnFirstDataReceivedListener(onFirstDataReceived)
    }

    fun setOnLogListener(listener: (level: Int, tag: String, msg: String) -> Unit) {
        mQYPlayer.setOnLogListener(listener)
    }

    fun setAudioDecrypt(decrypt: DecryptMode): Int {
        return mQYPlayer.setAudioDecrypt(decrypt)
    }

    fun setVideoDecrypt(decrypt: DecryptMode): Int {
        return mQYPlayer.setVideoDecrypt(decrypt)
    }

    fun setSpeed(speed: Int) {
        return mQYPlayer.setSpeed(speed)
    }

    fun setDecryptKey(key: String): Int {
        return mQYPlayer.setDecryptKey(key)
    }

    fun setCacheMin(cacheMin: Int): Int {
        return mQYPlayer.setCacheMin(cacheMin)
    }

    fun setCacheReady(cacheReady: Int): Int {
        return mQYPlayer.setCacheReady(cacheReady)
    }

    fun setCacheDropStart(cacheDropStart: Int): Int {
        return mQYPlayer.setCacheDropStart(cacheDropStart)
    }

    fun setCacheDropEnd(cacheDropEnd: Int): Int {
        return mQYPlayer.setCacheDropEnd(cacheDropEnd)
    }

    fun getCacheMin(): Int {
        return mQYPlayer.getCacheMin()
    }

    fun getCacheReady(): Int {
        return mQYPlayer.getCacheReady()
    }

    fun getCacheDropStart(): Int {
        return mQYPlayer.getCacheDropStart()
    }

    fun getCacheDropEnd(): Int {
        return mQYPlayer.getCacheDropEnd()
    }

    fun setPlayMode(mode: PlayerMode): Int {
        return mQYPlayer.setPlayMode(mode)
    }

    fun setReconnectCount(count: Int): Int {
        return mQYPlayer.setReconnectCount(count)
    }

    fun setLogLevel(level: Int) {
        mQYPlayer.setLogLevel(level)
    }

    fun setEnableLog(enable: Boolean) {
        mQYPlayer.setEnableLog(enable)
    }

    fun getVideoWidth(): Int {
        return mQYPlayer.getVideoWidth()
    }

    fun getVideoHeight(): Int {
        return mQYPlayer.getVideoHeight()
    }

    fun switchUrl(url: String): Int {
        return mQYPlayer.switchUrl(url)
    }

    fun release() {
        stop()
        mQYPlayer.setSurface(null)
        mQYPlayer.release()
        mSurface = null
    }

}