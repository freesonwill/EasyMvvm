package arch.cayenne.lib.qyplayer.control

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import arch.cayenne.lib.qyplayer.R
import arch.cayenne.lib.qyplayer.ScreenMode
import arch.cayenne.lib.qyplayer.util.NetworkUtil
import com.supucloud.qyplayer.PlayerMode


enum class PlayState {
    PLAYING,
    NOT_PLAYING
}

typealias onClickListener = () -> Unit

/**
 * 控制条界面。包括了顶部的标题栏，底部 的控制栏，锁屏按钮等等。是界面的主要组成部分。
 */
class ControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var mTitleBar: LinearLayout
    private lateinit var mTitleBarBackBtn: ImageView
    private lateinit var mTitleBarTitle: TextView
    private lateinit var mTitleQualityBtn: TextView
    private lateinit var mTitleMoreBtn: ImageView
    private lateinit var mScreenLockBtn: ImageView
    private lateinit var mScreenShotBtn: ImageView
    private lateinit var mControlBar: View
    private lateinit var mPlayStateBtn: ImageView
    private lateinit var mPlayRefreshBtn: ImageView
    private lateinit var mVideoTypeView: TextView
    private lateinit var mPlayStopBtn: ImageView
    private lateinit var mTimeView: TextView
    private lateinit var mLargeInfoBar: View
    private lateinit var mLargeInfoSeekBar: SeekBar
    private lateinit var mLargeDurationView: TextView
    private lateinit var mLargePositionView: TextView
    private lateinit var mSmallInfoBar: View
    private lateinit var mSmallInfoSeekBar: SeekBar
    private lateinit var mSmallDurationView: TextView
    private lateinit var mSmallPositionView: TextView
    private lateinit var mScreenModeBtn: ImageView
    private lateinit var mNetworkView: TextView

    private var mOnBackClickListener: onClickListener? = null
    private var mOnShowQualityClickListener: onClickListener? = null
    private var mOnShowMoreClickListener: onClickListener? = null
    private var mOnLockScreenClickListener: onClickListener? = null
    private var mOnScreenShotClickListener: onClickListener? = null
    private var mOnPlayStateClickListener: onClickListener? = null
    private var mOnPlayRefreshClickListener: onClickListener? = null
    private var mOnScreenModeClickListener: onClickListener? = null
    private var onSeekListener: ((progress: Int) -> Unit)? = null

    private var mScreenMode = ScreenMode.SMALL
    private var mScreenLocked = false
    private var mControlBarCanShow = true
    private var isSeekBarTouching = false
    private var mInScreenCosting = false
    private var startTime: Long = System.currentTimeMillis()
    private var mVideoPosition: Int = 0
    private var mMediaDuration: Int = 0
    private var mPlayState = PlayState.NOT_PLAYING
    private val mHideHandler = HideHandler(this)
    private val WHAT_HIDE: Int = 0
    private val DELAY_TIME: Long = 5 * 1000L //5秒后隐藏
    private var mPlayerMode: PlayerMode = PlayerMode.FLUENCY

    class HideHandler(private val controlView: ControlView) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (!controlView.isSeekBarTouching && !controlView.mInScreenCosting) {
                controlView.hide()
            }
            super.handleMessage(msg)
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_control, this, true)
        initAllViews()
        initListener()
        updateAllViews()
        updateTimer()
    }

    private fun initAllViews() {
        mTitleBar = findViewById(R.id.title_bar)
        mTitleBarTitle = findViewById(R.id.title_title)
        mTitleBarBackBtn = findViewById(R.id.title_back)
        mTitleQualityBtn = findViewById(R.id.title_quality)
        mControlBar = findViewById(R.id.controlbar)
        mPlayStateBtn = findViewById(R.id.player_state)
        mPlayRefreshBtn = findViewById(R.id.player_refresh)
        mPlayStopBtn = findViewById(R.id.player_stop)
        mVideoTypeView = findViewById(R.id.tv_type)
        mTimeView = findViewById(R.id.tv_time)
        mScreenModeBtn = findViewById(R.id.screen_mode)
        mScreenLockBtn = findViewById(R.id.screen_lock)
        mScreenShotBtn = findViewById(R.id.screen_shot)
        mTitleMoreBtn = findViewById(R.id.title_more)
        mLargeInfoBar = findViewById(R.id.info_large_bar)
        mLargeInfoSeekBar = findViewById(R.id.seekbar)
        mLargeDurationView = findViewById(R.id.info_large_duration)
        mLargePositionView = findViewById(R.id.info_large_position)
        mSmallInfoBar = findViewById(R.id.info_small_bar)
        mSmallInfoSeekBar = findViewById(R.id.info_small_seekbar)
        mSmallDurationView = findViewById(R.id.info_small_duration)
        mSmallPositionView = findViewById(R.id.info_small_position)
        mNetworkView = findViewById(R.id.info_network)
    }

    private fun initListener() {
        mTitleBarBackBtn.setOnClickListener { mOnBackClickListener?.invoke() }
        mTitleQualityBtn.setOnClickListener { mOnShowQualityClickListener?.invoke() }
        mTitleMoreBtn.setOnClickListener { mOnShowMoreClickListener?.invoke() }
        mPlayStateBtn.setOnClickListener { mOnPlayStateClickListener?.invoke() }
        mPlayRefreshBtn.setOnClickListener { mOnPlayRefreshClickListener?.invoke() }
        mScreenModeBtn.setOnClickListener { mOnScreenModeClickListener?.invoke() }
        mScreenLockBtn.setOnClickListener { mOnLockScreenClickListener?.invoke() }
        mScreenShotBtn.setOnClickListener { mOnScreenShotClickListener?.invoke() }

        val listener = object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mVideoPosition = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mHideHandler.removeMessages(WHAT_HIDE)
                isSeekBarTouching = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeekBarTouching = false
                onSeekListener?.invoke(mVideoPosition)
                mHideHandler.removeMessages(WHAT_HIDE)
                mHideHandler.sendEmptyMessageDelayed(
                    WHAT_HIDE,
                    DELAY_TIME
                )
            }

        }
        mLargeInfoSeekBar.setOnSeekBarChangeListener(listener)
        mSmallInfoSeekBar.setOnSeekBarChangeListener(listener)
    }

    private fun updateAllViews() {
        updateTitleBar()
        updateTitleVisible()
        updatePlayStateBtn()
        updateAllControlBar()
        updateScreenModeBtn()
        updateShowMoreBtn()
        updateScreenLockBtn()
        updateScreenShotBtn()
        updateQualityBtn()
        updateRefreshBtn()
        updateVideoTypeView()
        updateTimeView()
        updatePlayStopView()
        updateLargeInfoBar()
        updateSmallInfoBar()
        updateNetworkSpeedView()
    }

    private fun updateTimeView() {
        mTimeView.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked && mPlayerMode != PlayerMode.FILE && mPlayerMode != PlayerMode.VOD) VISIBLE else GONE
    }

    private fun updateVideoTypeView() {
        mVideoTypeView.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE

        mVideoTypeView.text = when (mPlayerMode) {
            PlayerMode.LOW_DELAY -> "LIVE"
            PlayerMode.REAL_TIME -> "LIVE"
            PlayerMode.FLUENCY -> "LIVE"
            PlayerMode.VOD -> "VOD"
            PlayerMode.FILE -> "FILE"
        }
    }

    private fun updatePlayStopView() {
        mPlayStopBtn.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE
    }

    private fun updateTimer() {
        val now = System.currentTimeMillis()
        mTimeView.text = TimeFormater.formatMs(now - startTime)

        postDelayed({ updateTimer() }, 1000)
    }

    private fun updateAllControlBar() {
        val canShow = mControlBarCanShow && !mScreenLocked
        mControlBar.visibility = if (canShow) VISIBLE else INVISIBLE
    }

    private fun updatePlayStateBtn() {
        when (mPlayState) {
            PlayState.NOT_PLAYING -> {
                mPlayStateBtn.setImageResource(R.drawable.ic_start)
            }

            PlayState.PLAYING -> {
                mPlayStateBtn.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun updateScreenModeBtn() {
        mScreenModeBtn.setImageResource(R.drawable.ic_land)
        mScreenModeBtn.visibility =
            if (mScreenMode == ScreenMode.SMALL && !mScreenLocked) VISIBLE else GONE
    }

    private fun updateRefreshBtn() {
        mPlayRefreshBtn.setImageResource(R.drawable.ic_refresh)
        mPlayRefreshBtn.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE
    }

    private fun updateTitleBar() {
        mTitleBar.visibility = if (!mScreenLocked) VISIBLE else GONE
    }

    private fun updateShowMoreBtn() {
        mTitleMoreBtn.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE
    }

    private fun updateQualityBtn() {
        mTitleQualityBtn.visibility =
                /*if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else*/ GONE
    }

    fun updateTitle(title: String) {
        mTitleBarTitle.text = title.substring(title.lastIndexOf('/') + 1, title.length)
    }

    private fun updateTitleVisible() {
        mTitleBarTitle.visibility = if (mScreenMode == ScreenMode.FULL) VISIBLE else INVISIBLE
    }

    /**
     * 更新小屏下的控制条信息
     */
    @SuppressLint("SetTextI18n")
    private fun updateSmallInfoBar() {
        mSmallInfoSeekBar.progress = mVideoPosition
        mSmallInfoSeekBar.max = mMediaDuration
        mSmallDurationView.text = TimeFormater.formatMs(mMediaDuration.toLong())
        mSmallPositionView.text = TimeFormater.formatMs(mVideoPosition.toLong())

        mSmallInfoSeekBar.visibility = isShowSmallViews()
        mSmallDurationView.visibility = isShowSmallViews()
        mSmallPositionView.visibility = isShowSmallViews()
    }

    private fun isShowSmallViews(): Int {
        return GONE
    }

    private fun isShowLargeViews(): Int {
        return if (mScreenMode == ScreenMode.FULL && !mScreenLocked && (mPlayerMode == PlayerMode.FILE || mPlayerMode == PlayerMode.VOD)) VISIBLE else GONE
    }

    /**
     * 更新大屏下的控制条信息
     */
    @SuppressLint("SetTextI18n")
    private fun updateLargeInfoBar() {
        mLargeInfoSeekBar.progress = mVideoPosition
        mLargeInfoSeekBar.max = mMediaDuration
        mLargeDurationView.text = TimeFormater.formatMs(mMediaDuration.toLong())
        mLargePositionView.text = TimeFormater.formatMs(mVideoPosition.toLong())

        mLargeInfoSeekBar.visibility = isShowLargeViews()
        mLargeDurationView.visibility = isShowLargeViews()
        mLargePositionView.visibility = isShowLargeViews()
    }

    private fun updateScreenLockBtn() {
        mScreenLockBtn.setImageResource(if (mScreenLocked) R.drawable.ic_lock else R.drawable.ic_unlock)
        mScreenLockBtn.visibility = if (mScreenMode == ScreenMode.FULL) VISIBLE else GONE
    }

    private fun updateScreenShotBtn() {
        mScreenShotBtn.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE
    }

    fun hideMoreButton() {
        mTitleMoreBtn.visibility = GONE
    }

    fun showMoreButton() {
        mTitleMoreBtn.visibility = VISIBLE
    }

    fun setPlayMode(mode: PlayerMode) {
        mPlayerMode = mode
    }

    private fun setControlBarCanShow(show: Boolean) {
        mControlBarCanShow = show
        updateAllControlBar()
    }

    fun setOnScreenModeClickListener(click: onClickListener) {
        mOnScreenModeClickListener = click
    }

    fun setOnPlayStateClickListener(click: onClickListener) {
        mOnPlayStateClickListener = click
    }

    fun setOnPlayStopClickListener(click: onClickListener) {
        mPlayState = PlayState.NOT_PLAYING
        updatePlayStateBtn()
        mPlayStopBtn.setOnClickListener {
            click.invoke()
        }
    }

    fun setOnBackClickListener(click: onClickListener) {
        mOnBackClickListener = click
    }

    fun setOnShowMoreClickListener(click: onClickListener) {
        mOnShowMoreClickListener = click
    }

    fun setOnShowQualityClickListener(click: onClickListener) {
        mOnShowQualityClickListener = click
    }

    fun setOnRefreshClickListener(click: onClickListener) {
        mOnPlayRefreshClickListener = click
    }

    fun setOnLockScreenClickListener(click: onClickListener) {
        mOnLockScreenClickListener = click
    }

    fun setOnScreenShotClickListener(click: onClickListener) {
        mOnScreenShotClickListener = click
    }

    fun setScreenModeStatus(mode: ScreenMode) {
        mScreenMode = mode
        updateAllViews()
    }

    fun setScreenLockStatus(locked: Boolean) {
        mScreenLocked = locked
        updateAllViews()
    }

    /**
     * 设置媒体时长
     */
    fun setMediaDuration(duration: Int) {
        mMediaDuration = duration
    }

    fun getVideoPosition(): Int {
        return mVideoPosition
    }

    fun setOnSeekListener(onSeekListener: (progress: Int) -> Unit) {
        this.onSeekListener = onSeekListener
    }

    private fun hideDelayed() {
        mHideHandler.removeMessages(WHAT_HIDE)
        mHideHandler.sendEmptyMessageDelayed(WHAT_HIDE, DELAY_TIME)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            hideDelayed()
        }
    }

    fun hide() {
        visibility = GONE
    }

    fun show() {
        visibility = VISIBLE
    }

    /**
     * 更新视频进度
     *
     * @param position 位置，ms
     */
    fun setVideoPosition(position: Int) {
        mVideoPosition = position
        updateLargeInfoBar()
        updateSmallInfoBar()
    }

    /**
     * 开启控制栏自动隐藏
     */
    fun openAutoHide() {
        hideDelayed()
    }

    /**
     * 关闭控制栏自动隐藏，并且展示控制栏
     */
    fun closeAutoHide() {
        mHideHandler.removeMessages(WHAT_HIDE)
        show()
    }

    fun setPlayState(playState: PlayState) {
        mPlayState = playState
        updatePlayStateBtn()
    }

    @SuppressLint("SetTextI18n")
    fun setNetworkSpeed(kbps: Int) {
        mNetworkView.text = NetworkUtil.formatSpeed(kbps.toLong())
    }

    private fun updateNetworkSpeedView() {
        mNetworkView.visibility =
            if (mScreenMode == ScreenMode.FULL && !mScreenLocked) VISIBLE else GONE
    }

}