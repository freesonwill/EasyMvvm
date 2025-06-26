package arch.cayenne.lib.qyplayer.ui.widget

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.qyplayer.R
import arch.cayenne.lib.qyplayer.gesture.GestureDialogManager
import arch.cayenne.lib.qyplayer.gesture.GestureListener
import arch.cayenne.lib.qyplayer.gesture.GestureView
import arch.cayenne.lib.qyplayer.util.ScreenUtils
import com.xxx.qyplayer.PlayerConfig
import com.xxx.qyplayer.PlayerMode
import com.xxx.qyplayer.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class LivePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var mRenderView: QYRenderView
    private lateinit var mGestureView: GestureView

    private var mOnPlayStateBtnClickListener: (() -> Unit)? = null
    private var mOnUpdateStatisticsListener: ((category: String, json: String) -> Unit)? = null

    /**
     * 单击事件处理
     */
    private var onSingleTapListener: (() -> Unit)? = null

    /**
     * 播放状态通知
     */
    private var playerStateListener: ((PlayerState) -> Unit)? = null

    private lateinit var mGestureDialogManager: GestureDialogManager
    private val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mCurrentPosition: Long = 0
    private var inSeek: Boolean = false
    private var mPlayerState = PlayerState.IDLE
    private var mScreenBrightness: Int = 0
    private var mPlayingPath: String? = null
    private var mConfig: PlayerConfig? = null

    private var isFastSpeed = false

    private lateinit var mPlayerMode: PlayerMode

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var bufferingTimeoutJob: Job? = null

    fun init(playerMode: PlayerMode) {
        mPlayerMode = playerMode
        initViews(R.layout.layout_live_player_view)
    }

    fun setOnSingleTapListener(listener: (() -> Unit)) {
        onSingleTapListener = listener
    }

    fun setPlayerStateListener(listener: (PlayerState) -> Unit) {
        playerStateListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //移除window的时候需要将监听置空，否则因为LivePlayerView存在PlayerCache引发泄漏
        onSingleTapListener = null
        playerStateListener = null
    }

    fun setDataSource(url: String) {
        mPlayingPath = url
        mRenderView.setDataSource(url)
    }

    fun getDataSource(): String? {
        return mPlayingPath
    }

    fun setConfig(cfg: PlayerConfig) {
        mConfig = cfg
        mRenderView.setConfig(cfg)
    }

    fun getConfig(): PlayerConfig {
        return mRenderView.getConfig()
    }

    fun setMute(isMute: Boolean) {
        mRenderView.setMute(isMute)
    }

    fun prepare() {
        updatePlayState(PlayerState.PLAYING)
        mRenderView.prepare()
    }

    fun start() {
        "start".logd(TAG)
        mRenderView.start()
    }

    fun onResume() {
        "onResume".logd(TAG)
        start()
    }

    fun onPause() {
        "onPause".logd(TAG)
        mRenderView.pause()
    }

    fun onStop() {
        "onStop".logd(TAG)
        mRenderView.stop()
    }

    /**
     * Activity 销毁，释放资源
     */
    fun onDestroy() {
        "onDestroy".logd(TAG)
        mRenderView.release()
    }

    /**
     * 切换播放状态。点播放按钮之后的操作
     */
    private fun switchPlayerState() {
        mOnPlayStateBtnClickListener?.invoke()
        if (mPlayerState == PlayerState.PLAYING) {
            updatePlayState(PlayerState.PAUSED)
            pause()
        } else {
            updatePlayState(PlayerState.PLAYING)
            start()
        }
    }

    fun pause() {
        updatePlayState(PlayerState.PAUSED)
        mRenderView.pause()
    }

    private fun initRenderView() {
        mRenderView = findViewById(R.id.renderView)

        mRenderView.apply {
            setOnStateChangedListener {
                (context as? Activity)?.runOnUiThread {
                    if (it.state == PlayerState.ERROR) {
                    }

                    updatePlayState(it.state)
                    mCurrentPosition = it.position.toLong()
                }
            }

            setOnUpdateStatisticsListener { category, json ->
                mOnUpdateStatisticsListener?.invoke(category, json)
                if (category == "network") {
                    (context as? Activity)?.runOnUiThread {
                        processNetworkSpeed(json)
                    }
                }
            }

            init(mPlayerMode)
            setSurfaceType(SurfaceType.SURFACE_VIEW)
        }

    }


    private fun initGestureView() {
        mGestureView = findViewById(R.id.gesture_view)

        mGestureView.apply {
            setOnGestureListener(object : GestureListener {
                override fun onHorizontalDistance(downX: Float, nowX: Float) {
                    if (!isGestureEnable()) {
                        return
                    }
                    when (mPlayerMode) {
                        PlayerMode.VOD, PlayerMode.FILE -> {
                            //水平滑动调节seek。
                            // seek需要在手势结束时操作。
                            val duration: Long = mRenderView.getDuration()
                            val position: Long = mCurrentPosition
                            var deltaPosition: Long = 0
                            var targetPosition = 0

                            //在播放时才能调整大小
                            deltaPosition = (nowX - downX).toLong() * duration / width
                            targetPosition = getTargetPosition(duration, position, deltaPosition)
                            /*if (mPlayerState == PlayerState.PLAYING) {
                            }*/
                            inSeek = true
//                            mControlView.setMediaDuration(duration.toInt())
//                            mControlView.setVideoPosition(targetPosition)

                            mGestureDialogManager.run {
                                showSeekDialog(this@LivePlayerView, targetPosition)
                                updateSeekDialog(duration, position, deltaPosition)
                            }

//                            mControlView.closeAutoHide()
                        }

                        else -> {
                            // noop 不需要处理seek
                        }
                    }
                }

                override fun onLeftVerticalDistance(downY: Float, nowY: Float) {
                    if (!isGestureEnable()) {
                        return
                    }
                    // 左侧上下滑动调整亮度
                    val changePercent = (((nowY - downY) * 100) / height).toInt()

                    mGestureDialogManager.showBrightnessDialog(
                        this@LivePlayerView,
                        if (mScreenBrightness <= 0) (ScreenUtils.getActivityBrightness(context as Activity) * 100).toInt() else mScreenBrightness
                    )
                    val brightness = mGestureDialogManager.updateBrightnessDialog(changePercent)
                    mScreenBrightness = brightness

                    ScreenUtils.updateBright(context as Activity, brightness)
                }

                override fun onRightVerticalDistance(downY: Float, nowY: Float) {
                    if (!isGestureEnable()) {
                        return
                    }
                    //右侧上下滑动调节音量
                    val volume: Int = ScreenUtils.getCurrentVolumeProgress(mAudioManager, 100)
                    // 右侧上下滑动调整音量
                    val changePercent = (((nowY - downY) * 100) / height).toInt()
                    mGestureDialogManager.showVolumeDialog(
                        this@LivePlayerView,
                        (volume).toFloat()
                    )
                    val targetVolume = mGestureDialogManager.getTargetVolume(changePercent)

                    val finalProgress = ScreenUtils.updateVolumeProgress(
                        mAudioManager,
                        targetVolume.toInt(), 100
                    )
                    mGestureDialogManager.updateVolumeDialog(finalProgress.toFloat())
                }

                override fun onGestureEnd() {
                    if (!isGestureEnable()) {
                        return
                    }

                    //直播不能做加速
                    if (mPlayerMode == PlayerMode.VOD || mPlayerMode == PlayerMode.FILE) {

                        if (inSeek) {
//                        val seekPosition: Int = mControlView.getVideoPosition()
//                        seekTo(seekPosition)
                            inSeek = false
                        }
                        if (isFastSpeed) {
                            mRenderView.setSpeed(100)
                            isFastSpeed = false
                        }
//                    mControlView.openAutoHide()
                        mGestureDialogManager.dismissBrightnessDialog()
                        mGestureDialogManager.dismissVolumeDialog()
                        mGestureDialogManager.dismissSeekDialog()
                    }
                }

                override fun onSingleTap() {
                    onSingleTapListener?.invoke()
                }

                override fun onDoubleTap() {
                    if (!isGestureEnable()) {
                        return
                    }
                    switchPlayerState()
                }

                override fun onLongPress() {
                    if (!isGestureEnable()) {
                        return
                    }

                    //直播不要做加速
                    if (mPlayerMode == PlayerMode.VOD || mPlayerMode == PlayerMode.FILE) {
                        isFastSpeed = true
                        mRenderView.setSpeed(200)
                    }
                }

            })
        }

        if (context is Activity) {
            mGestureDialogManager = GestureDialogManager(context as Activity)
        }
    }

    private fun isGestureEnable(): Boolean {
        return false
    }

    private fun processNetworkSpeed(json: String) {
        val jsonObject = JSONObject(json)
        if (jsonObject.has("network")) {
            val networkObject = jsonObject.getJSONObject("network")
            val audioSpeed =
                if (networkObject.has("audio_kbps")) networkObject.getInt("audio_kbps") else 0
            val videoSpeed =
                if (networkObject.has("video_kbps")) networkObject.getInt("video_kbps") else 0
//            mControlView.setNetworkSpeed(audioSpeed + videoSpeed)
        }
    }

    /**
     * 目标位置计算算法
     *
     * @param duration        视频总时长
     * @param currentPosition 当前播放位置
     * @param deltaPosition   与当前位置相差的时长
     * @return
     */
    private fun getTargetPosition(duration: Long, currentPosition: Long, deltaPosition: Long): Int {
        // seek步长
        val finalDeltaPosition: Long
        // 根据视频时长，决定seek步长
        val totalMinutes = duration / 1000 / 60
        val hours = (totalMinutes / 60).toInt()
        val minutes = (totalMinutes % 60).toInt()

        // 视频时长为1小时以上，小屏和全屏的手势滑动最长为视频时长的十分之一
        finalDeltaPosition = if (hours >= 1) {
            deltaPosition / 10
        } // 视频时长为31分钟－60分钟时，小屏和全屏的手势滑动最长为视频时长五分之一
        else if (minutes > 30) {
            deltaPosition / 5
        } // 视频时长为11分钟－30分钟时，小屏和全屏的手势滑动最长为视频时长三分之一
        else if (minutes > 10) {
            deltaPosition / 3
        } // 视频时长为4-10分钟时，小屏和全屏的手势滑动最长为视频时长二分之一
        else if (minutes > 3) {
            deltaPosition / 2
        } // 视频时长为1秒钟至3分钟时，小屏和全屏的手势滑动最长为视频结束
        else {
            deltaPosition
        }

        var targetPosition = finalDeltaPosition + currentPosition
        if (targetPosition < 0) {
            targetPosition = 0
        }
        if (targetPosition > duration) {
            targetPosition = duration
        }
        return targetPosition.toInt()
    }


    private fun updatePlayState(state: PlayerState) {
        if (mPlayerState == state) {
            return
        }
        mPlayerState = state
        playerStateListener?.invoke(mPlayerState)

        when (mPlayerState) {
            PlayerState.PLAYING -> {
                bufferingTimeoutJob?.cancel()
            }

            PlayerState.PAUSED -> {
                //没有暂停按钮，
            }

            PlayerState.CACHING, PlayerState.CONNECTING -> {
                // 启动协程，10秒超时
                // 如果10秒后还在loading状态， 展示加载失败页面
                bufferingTimeoutJob?.cancel()
                bufferingTimeoutJob = coroutineScope.launch {
                    delay(10000)
                    playerStateListener?.invoke(PlayerState.ERROR)
                }
            }

            else -> {

            }
        }
    }


    private fun initViews(@LayoutRes layoutId: Int) {
        // 使用 LayoutInflater 加载 XML 布局
        LayoutInflater.from(context)
            .inflate(layoutId, this, true)

        // 初始化子视图
        initRenderView()
        initGestureView()
    }


    companion object {
        const val TAG: String = "LivePlayerView"
    }
}