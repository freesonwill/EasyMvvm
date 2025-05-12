package com.walisport.module.live.ui.widget

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.qyplayer.ScreenMode
import arch.cayenne.lib.qyplayer.gesture.GestureDialogManager
import arch.cayenne.lib.qyplayer.gesture.GestureListener
import arch.cayenne.lib.qyplayer.gesture.GestureView
import arch.cayenne.lib.qyplayer.util.OrientationWatchDog
import arch.cayenne.lib.qyplayer.util.ScreenUtils
import arch.cayenne.lib.qyplayer.view.QYRenderView
import arch.cayenne.lib.qyplayer.view.SurfaceType
import com.xxx.qyplayer.PlayerConfig
import com.xxx.qyplayer.PlayerMode
import com.xxx.qyplayer.PlayerState
import org.json.JSONObject

class LivePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var mRenderView = QYRenderView(context)
    private lateinit var mGestureView: GestureView

    private lateinit var ctLoading: ConstraintLayout
    private lateinit var ctError: ConstraintLayout
    private lateinit var ivLoading: ImageView

    private var mOnOrientationChangeListener: ((from: Boolean, currentMode: ScreenMode) -> Unit)? =
        null
    private var mOnShowMoreClickListener: (() -> Unit)? = null
    private var mOnShowQualityClickListener: (() -> Unit)? = null
    private var mOnPlayStateBtnClickListener: (() -> Unit)? = null
    private var mOnUpdateStatisticsListener: ((category: String, json: String) -> Unit)? = null

    private lateinit var mGestureDialogManager: GestureDialogManager
    private val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mCurrentScreenMode = ScreenMode.SMALL
    private val mOrientationWatchDog = OrientationWatchDog(context)
    private var mIsFullScreenLocked = false
    private var mCurrentPosition: Long = 0
    private var inSeek: Boolean = false
    private var mPlayerState = PlayerState.IDLE
    private var mScreenBrightness: Int = 0
    private var isActivityStopped = false
    private var mPlayingPath: String? = null
    private var mConfig: PlayerConfig? = null

    private var isFastSpeed = false

    private lateinit var mPlayerMode: PlayerMode

    private var loadingAnim: ObjectAnimator? = null


    fun init(playerMode: PlayerMode) {
        mPlayerMode = playerMode
        initViews()
        initListeners()
    }

    private fun initViews() {

        // 使用 LayoutInflater 加载 XML 布局
        LayoutInflater.from(context)
            .inflate(com.walisport.module.live.R.layout.layout_live_player_view, this, true)

        ctLoading = findViewById(com.walisport.module.live.R.id.ct_loading)
        ctError = findViewById(com.walisport.module.live.R.id.ct_error)
        ivLoading = findViewById(com.walisport.module.live.R.id.iv_video_loading)

        // 初始化子视图
        initRenderView()
        initGestureView()
        initStateView()
    }

    private fun initListeners() {
        mOrientationWatchDog.setOnOrientationListener(object :
            OrientationWatchDog.OnOrientationListener {
            override fun changedToLandForwardScape(fromPort: Boolean) {
                this@LivePlayerView.changedToLandForwardScape(fromPort)
            }

            override fun changedToLandReverseScape(fromPort: Boolean) {
                this@LivePlayerView.changedToLandReverseScape(fromPort)
            }

            override fun changedToPortrait(fromLand: Boolean) {
                this@LivePlayerView.changeToPortrait(fromLand)
            }

        })
    }

    /**
     * 切换播放状态。点播放按钮之后的操作
     */
    private fun switchPlayerState() {
        mOnPlayStateBtnClickListener?.invoke()
        if (mPlayerState == PlayerState.PLAYING) {
            updatePauseIconView(PlayerState.PAUSED)
            pause()
        } else {
            updatePauseIconView(PlayerState.PLAYING)
            start()
        }
    }

    fun start() {
        mRenderView.start()
    }

    private fun pause() {
        mRenderView.pause()
    }

    fun onResume() {
        mOrientationWatchDog.startWatch()
        if (mIsFullScreenLocked) {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                changeScreenMode(ScreenMode.SMALL, false)
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeScreenMode(ScreenMode.FULL, false)
            }
        }

        start()
    }

    fun onPause(){
        mRenderView.pause()
    }

    /**
     * 屏幕方向变为横屏
     *
     * @param fromPort 是否从竖屏变过来
     */
    private fun changedToLandForwardScape(fromPort: Boolean) {
        // 如果不是从竖屏变过来，也就是一直横屏的时候，就不用做后续操作了。比如通过传感器监测时会需要状态
        if (!fromPort) {
            return
        }
        changeScreenMode(ScreenMode.FULL, false)
        mOnOrientationChangeListener?.invoke(fromPort, mCurrentScreenMode)
    }

    /**
     * 屏幕方向变为横屏。
     *
     * @param fromPort 是否从竖屏变过来
     */
    private fun changedToLandReverseScape(fromPort: Boolean) {
        //如果不是从竖屏变过来，也就是一直是横屏的时候，就不用操作了
        if (!fromPort) {
            return
        }
        changeScreenMode(ScreenMode.FULL, true)
        mOnOrientationChangeListener?.invoke(fromPort, mCurrentScreenMode)
    }

    /**
     * 改变屏幕模式
     */
    private fun changeScreenMode(targetMode: ScreenMode, isReverse: Boolean) {
        var finalScreenMode = targetMode
        if (mIsFullScreenLocked) {
            finalScreenMode = ScreenMode.FULL
        }

        if (targetMode != mCurrentScreenMode) {
            mCurrentScreenMode = finalScreenMode
        }

        if (context is Activity) {
            when (finalScreenMode) {
                ScreenMode.FULL -> {
                    if (isReverse) {
                        (context as Activity).requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    } else {
                        (context as Activity).requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }

                ScreenMode.SMALL -> {
                    (context as Activity).requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }
    }

    /**
     * 屏幕方向变为竖屏
     *
     * @param fromLand 是否从横屏转过来
     */
    private fun changeToPortrait(fromLand: Boolean) {
        //屏幕转为竖屏
        if (mIsFullScreenLocked) {
            return
        }

        if (mCurrentScreenMode === ScreenMode.FULL) {
            //全屏情况转到了竖屏
            if (fromLand) {
                changeScreenMode(ScreenMode.SMALL, false)
            } else {
                //如果没有转到过横屏，就不让他转了。防止竖屏的时候点横屏之后，又立即转回来的现象
            }

        } else if (mCurrentScreenMode === ScreenMode.SMALL) {
            //竖屏的情况转到了竖屏
        }
        mOnOrientationChangeListener?.invoke(fromLand, mCurrentScreenMode)
    }

    private fun initRenderView() {
        mRenderView = findViewById(com.walisport.module.live.R.id.renderView)

        mRenderView.apply {
            setOnStateChangedListener {
                (context as? Activity)?.runOnUiThread {
                    if (it.state == PlayerState.ERROR) {
                    }

                    updatePauseIconView(it.state)
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
        mGestureView = findViewById(com.walisport.module.live.R.id.gesture_view)

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

                override fun onSingleTap() {
//                    if (mControlView.visibility != VISIBLE) {
//                        mControlView.show()
//                    } else {
//                        mControlView.hide()
//                    }
                }

                override fun onDoubleTap() {
                    if (!isGestureEnable()) {
                        return
                    }
                    switchPlayerState()
                }

                override fun onLongPress() {
                    isFastSpeed = true
                    mRenderView.setSpeed(200)
                }

            })
        }

        if (context is Activity) {
            mGestureDialogManager = GestureDialogManager(context as Activity)
        }
    }

    private fun isGestureEnable(): Boolean {
        return mCurrentScreenMode == ScreenMode.FULL
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
    fun getTargetPosition(duration: Long, currentPosition: Long, deltaPosition: Long): Int {
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

    private fun initStateView() {
    }

    private fun updatePauseIconView(state: PlayerState) {
        if (mPlayerState == state) {
            return
        }
        mPlayerState = state
        when (mPlayerState) {
            PlayerState.PLAYING -> {
//                mControlView.setPlayState(PlayState.PLAYING)
                loadingAnim?.cancel()
                ctLoading.visibility = GONE
                ctError.visibility = GONE
            }

            PlayerState.PAUSED -> {
//                mControlView.setPlayState(PlayState.NOT_PLAYING)
                //没有暂停按钮，
            }

            PlayerState.CACHING, PlayerState.CONNECTING -> {
//                mControlView.setPlayState(PlayState.NOT_PLAYING)
                // 创建旋转动画
                loadingAnim = ObjectAnimator.ofFloat(
                    ivLoading,  // 目标 View
                    "rotation",  // 属性名称
                    0f, 360f // 从 0 度旋转到 360 度
                ).run {
                    // 设置动画属性
                    setDuration(1000) // 持续时间 1 秒
                    repeatCount = ObjectAnimator.INFINITE // 无限循环
                    interpolator = LinearInterpolator() // 匀速旋转

                    // 启动动画
                    start()
                    this
                }

                ctLoading.visibility = VISIBLE
                ctError.visibility = GONE
            }

            PlayerState.STOPPED -> {
//                mControlView.setPlayState(PlayState.NOT_PLAYING)
                loadingAnim?.cancel()
                ctLoading.visibility = GONE
                ctError.visibility = GONE
            }

            else -> {
//                mControlView.setPlayState(PlayState.NOT_PLAYING)
                loadingAnim?.cancel()
                ctLoading.visibility = GONE
                ctError.visibility = GONE
            }
        }
    }

    /**
     * 锁定屏幕。锁定屏幕后，只有锁会显示，其他都不会显示。手势也不可用
     *
     * @param lockScreen 是否锁住
     */
    fun lockScreen(lockScreen: Boolean) {
        mIsFullScreenLocked = lockScreen
//        mControlView.setScreenLockStatus(mIsFullScreenLocked)
        mGestureView.setScreenLockStatus(mIsFullScreenLocked)
    }

    fun prepare() {
        mRenderView.prepare()
    }

    fun setDataSource(url: String) {
        mPlayingPath = url
//        mControlView.updateTitle(url)
        mRenderView.setDataSource(url)
    }

    fun setOnUpdateStatisticsListener(onUpdateStatistics: (category: String, json: String) -> Unit) {
        mOnUpdateStatisticsListener = onUpdateStatistics
    }

    fun setOnSurfaceCreatedListener(onSurfaceCreated: () -> Unit) {
        mRenderView.setOnSurfaceCreatedListener(onSurfaceCreated)
    }

    fun setOnOrientationChangeListener(orientationChange: (from: Boolean, currentMode: ScreenMode) -> Unit) {
        mOnOrientationChangeListener = orientationChange
    }

    fun setConfig(cfg: PlayerConfig) {
        mConfig = cfg
        mRenderView.setConfig(cfg)
    }

    fun setMute(isMute: Boolean) {
        mRenderView.setMute(isMute)
    }

    fun getConfig(): PlayerConfig {
        return mRenderView.getConfig()
    }

    fun onStop() {
        mRenderView.stop()
        isActivityStopped = true
    }

    /**
     * Activity 销毁，释放资源
     */
    fun onDestroy() {
        mRenderView.release()
        mOrientationWatchDog.destroy()
    }
}