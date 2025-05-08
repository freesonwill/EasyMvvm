package arch.cayenne.lib.qyplayer.control

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import com.supucloud.qyplayer.MirrorMode
import com.supucloud.qyplayer.RotateMode
import com.supucloud.qyplayer.ScaleMode
import com.supucloud.qyplayer.ViewportRatioMode
import com.supucloud.qyplayer.demo.GlobalConfig
import com.supucloud.qyplayer.demo.R
import com.supucloud.qyplayer.demo.util.ScreenUtils
import com.supucloud.qyplayer.demo.util.toast
import com.supucloud.qyplayer.transformToMirrorMode
import com.supucloud.qyplayer.transformToRotateMode
import com.supucloud.qyplayer.transformToScaleMode
import com.supucloud.qyplayer.transformToSizeRatioMode

typealias SwitchChangedListener = (isChecked: Boolean) -> Unit
typealias ClickListener = () -> Unit

class ShowMoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mPipSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mHwDecodeSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mAutoPlaySwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mLoopPlaySwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mMuteSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mPlayInBackSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mAutoFrameSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mAutoReconnectSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mKeepStopSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mOnlyAudioSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mAudioDecryptSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mVideoDecryptSwitch: Switch

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var mNoAudioSwitch: Switch

    private lateinit var mVolumeSeekBar: SeekBar
    private lateinit var mLightSeekBar: SeekBar
    private lateinit var mBufferedTimeSeekBar: SeekBar
    private lateinit var mReconnectTimeSeekBar: SeekBar
    private lateinit var mResetConfigTextView: TextView
    private lateinit var mLightRatioTextView: TextView
    private lateinit var mVolumeRatioTextView: TextView
    private lateinit var mBufferLengthTextView: TextView
    private lateinit var mReconnectTimeTextView: TextView
    private lateinit var mMediaTextView: TextView
    private lateinit var mStatisticsTextView: TextView
    private lateinit var mNetworkTextView: TextView
    private lateinit var mDecoderTextView: TextView

    private var mOnReconnectTimeChangedListener: ((time: Int) -> Unit)? = null
    private var mOnBufferedTimeChangedListener: ((time: Int) -> Unit)? = null

    private lateinit var mMirrorRadioGroup: RadioGroup
    private lateinit var mScaleRadioGroup: RadioGroup
    private lateinit var mViewportRatioRadioGroup: RadioGroup
    private lateinit var mRotateRadioGroup: RadioGroup
    private lateinit var mColorRadioGroup: RadioGroup

    private val mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mGlobalConfig: GlobalConfig? = null

    private val COLOR_BLACK = 0x000000ff.toInt()
    private val COLOR_WHITE = 0xffffffff.toInt()
    private val COLOR_RED = 0xff0000ff.toInt()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_show_more, this, true)
        initView()
        initListener()
        updateView()
    }

    fun setConfig(cfg: GlobalConfig) {
        mGlobalConfig = cfg
        mHwDecodeSwitch.isChecked = cfg.isHWDecode
        mAutoPlaySwitch.isChecked = cfg.isAutoPlay
        mBufferLengthTextView.text = "${cfg.maxCache / 1000}s"
        mBufferedTimeSeekBar.progress = cfg.maxCache / 1000
        mReconnectTimeTextView.text = "${cfg.reconnectTime / 1000}s"
        mReconnectTimeSeekBar.progress = cfg.reconnectTime / 1000
        mKeepStopSwitch.isChecked = !cfg.isClear
        mAutoReconnectSwitch.isChecked = cfg.reconnectTime != 0
        mLoopPlaySwitch.isChecked = cfg.isLoop
        mMuteSwitch.isChecked = cfg.isMute
        mAudioDecryptSwitch.isChecked = cfg.isAudioDecrypt
        mVideoDecryptSwitch.isChecked = cfg.isVideoDecrypt
        mNoAudioSwitch.isChecked = cfg.isNoAudio


        updateMirrorRadioGroup(cfg.flip.transformToMirrorMode())
        updateScaleRadioGroup(cfg.fill.transformToScaleMode())
        updateSizeRatioRadioGroup(cfg.viewportRatio.transformToSizeRatioMode())
        updateRotateRadioGroup(cfg.rotation.transformToRotateMode())
        updateRotateRadioGroup(cfg.color)
    }

    private fun updateMirrorRadioGroup(mode: MirrorMode) {
        when (mode) {
            MirrorMode.MIRROR_MODE_NONE -> mMirrorRadioGroup.check(R.id.rb_mirror_none)
            MirrorMode.MIRROR_MODE_VERTICAL -> mMirrorRadioGroup.check(R.id.rb_mirror_v)
            MirrorMode.MIRROR_MODE_HORIZONTAL -> mMirrorRadioGroup.check(R.id.rb_mirror_h)
            else -> {
                // noop
            }
        }
    }

    private fun updateScaleRadioGroup(mode: ScaleMode) {
        when (mode) {
            ScaleMode.SCALE_CONTAIN -> mScaleRadioGroup.check(R.id.rb_scale_fit)
            ScaleMode.SCALE_COVER -> mScaleRadioGroup.check(R.id.rb_scale_crop)
            ScaleMode.SCALE_FILL -> mScaleRadioGroup.check(R.id.rb_scale_scale)
            else -> {
                // noop
            }
        }
    }

    private fun updateSizeRatioRadioGroup(mode: ViewportRatioMode) {
        when (mode) {
            ViewportRatioMode.DEFAULT -> mViewportRatioRadioGroup.check(R.id.rb_size_ratio_default)
            ViewportRatioMode.Size_16_9 -> mViewportRatioRadioGroup.check(R.id.rb_size_ratio_16_9)
            ViewportRatioMode.Size_4_3 -> mViewportRatioRadioGroup.check(R.id.rb_size_ratio_4_3)
        }
    }

    private fun updateRotateRadioGroup(mode: RotateMode) {
        when (mode) {
            RotateMode.ROTATE_0 -> mRotateRadioGroup.check(R.id.rb_rotate_none)
            RotateMode.ROTATE_90 -> mRotateRadioGroup.check(R.id.rb_rotate_90)
            RotateMode.ROTATE_180 -> mRotateRadioGroup.check(R.id.rb_rotate_180)
            RotateMode.ROTATE_270 -> mRotateRadioGroup.check(R.id.rb_rotate_270)
        }
    }

    private fun updateRotateRadioGroup(color: Int) {
        when (color) {
            COLOR_BLACK -> mColorRadioGroup.check(R.id.rb_color_black)
            COLOR_WHITE -> mColorRadioGroup.check(R.id.rb_color_white)
            COLOR_RED -> mColorRadioGroup.check(R.id.rb_color_red)
        }
    }

    private fun initView() {
        mResetConfigTextView = findViewById(R.id.tv_reset_config)
        mVolumeSeekBar = findViewById(R.id.seekbar_volume)
        mLightSeekBar = findViewById(R.id.seekbar_light)
        mBufferedTimeSeekBar = findViewById(R.id.seekbar_buffer)
        mReconnectTimeSeekBar = findViewById(R.id.seekbar_auto_reconnect)
        mLightRatioTextView = findViewById(R.id.tv_bright_ratio)
        mVolumeRatioTextView = findViewById(R.id.tv_volume_ratio)
        mBufferLengthTextView = findViewById(R.id.tv_buffer_time)
        mReconnectTimeTextView = findViewById(R.id.tv_auto_reconnect_time)
        mPipSwitch = findViewById(R.id.swt_pip)
        mAutoPlaySwitch = findViewById(R.id.swt_auto_play)
        mLoopPlaySwitch = findViewById(R.id.swt_loop_play)
        mMuteSwitch = findViewById(R.id.swt_mute)
        mHwDecodeSwitch = findViewById(R.id.swt_hardware)
        mPlayInBackSwitch = findViewById(R.id.swt_back_play)
        mAutoFrameSwitch = findViewById(R.id.swt_auto_frame)
        mAutoReconnectSwitch = findViewById(R.id.swt_auto_reconnect)
        mKeepStopSwitch = findViewById(R.id.swt_keep_stop_frame)
        mOnlyAudioSwitch = findViewById(R.id.swt_only_audio)
        mAudioDecryptSwitch = findViewById(R.id.swt_audio_decrypt)
        mVideoDecryptSwitch = findViewById(R.id.swt_video_decrypt)
        mNoAudioSwitch = findViewById(R.id.swt_no_audio)
        mMediaTextView = findViewById(R.id.tv_media_info)
        mStatisticsTextView = findViewById(R.id.tv_statistics_info)
        mNetworkTextView = findViewById(R.id.tv_network_info)
        mDecoderTextView = findViewById(R.id.tv_decoder_info)
        mMirrorRadioGroup = findViewById(R.id.mirror_group)
        mScaleRadioGroup = findViewById(R.id.scale_group)
        mViewportRatioRadioGroup = findViewById(R.id.size_ratio_group)
        mRotateRadioGroup = findViewById(R.id.rotate_group)
        mColorRadioGroup = findViewById(R.id.color_group)
    }

    private fun updateView() {
        updateCurrentVolume()
        updateActivityBrightness()
    }

    private fun initListener() {
        mVolumeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mVolumeSeekBar.progress = updateVolumeProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // noop
            }
        })

        mLightSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateBrightProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

        })

        mBufferedTimeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateBufferLengthProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

        })

        mReconnectTimeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateReconnectTimeProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // noop
            }

        })

        mResetConfigTextView.setOnClickListener {
            mGlobalConfig?.let {
                it.inited = false
                "Configuration reset. Restart the player to apply changes.".toast(context)
            }
        }
    }

    private fun updateCurrentVolume() {
        val progress = ScreenUtils.getCurrentVolumeProgress(mAudioManager, mVolumeSeekBar.max)
        mVolumeSeekBar.progress = progress
        mVolumeRatioTextView.text = "$progress%"
    }

    private fun updateVolumeProgress(progress: Int): Int {
        val finalProgress = ScreenUtils.updateVolumeProgress(mAudioManager, progress, mVolumeSeekBar.max)
        mVolumeRatioTextView.text = "$finalProgress%"
        return finalProgress
    }

    private fun updateActivityBrightness() {
        val brightness = ScreenUtils.getActivityBrightness(context as Activity)
        mLightSeekBar.progress = (brightness * 100).toInt()
        mLightRatioTextView.text = "${mLightSeekBar.progress}%"
    }

    private fun updateBrightProgress(progress: Int) {
        ScreenUtils.updateBright(context as Activity, progress)
        mLightSeekBar.progress = progress
        mLightRatioTextView.text = "${progress}%"
    }

    fun updateBufferLengthProgress(progress: Int) {
        mBufferedTimeSeekBar.progress = progress
        mBufferLengthTextView.text = "${progress}s"
        mOnBufferedTimeChangedListener?.invoke(progress * 1000)
    }

    fun updateReconnectTimeProgress(progress: Int) {
        mReconnectTimeSeekBar.progress = progress
        mReconnectTimeTextView.text = "${progress}s"
        mOnReconnectTimeChangedListener?.invoke(progress * 1000)

        mAutoReconnectSwitch.isChecked = progress != 0
    }

    fun setOnPipClickListener(onChangedListener: SwitchChangedListener) {
        mPipSwitch.setOnCheckedChangeListener { _, isChecked -> onChangedListener.invoke(isChecked) }
    }

    fun setOnAutoPlayClickListener(onChangedListener: SwitchChangedListener) {
        mAutoPlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnLoopPlayClickListener(onChangedListener: SwitchChangedListener) {
        mLoopPlaySwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnMutePlayClickListener(onChangedListener: SwitchChangedListener) {
        mMuteSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnHwDecodeClickListener(onChangedListener: SwitchChangedListener) {
        mHwDecodeSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnPlayInBackClickListener(onChangedListener: SwitchChangedListener) {
        mPlayInBackSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnAutoFrameClickListener(onChangedListener: SwitchChangedListener) {
        mAutoFrameSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnAutoReconnectClickListener(onChangedListener: SwitchChangedListener) {
        mAutoReconnectSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnKeepStopClickListener(onChangedListener: SwitchChangedListener) {
        mKeepStopSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnOnlyAudioClickListener(onChangedListener: SwitchChangedListener) {
        mOnlyAudioSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnAudioDecryptClickListener(onChangedListener: SwitchChangedListener) {
        mAudioDecryptSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnVideoDecryptClickListener(onChangedListener: SwitchChangedListener) {
        mVideoDecryptSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnNoAudioClickListener(onChangedListener: SwitchChangedListener) {
        mNoAudioSwitch.setOnCheckedChangeListener { _, isChecked ->
            onChangedListener.invoke(isChecked)
        }
    }

    fun setOnReconnectTimeChangedListener(onReconnectTimeChanged: (time: Int) -> Unit) {
        mOnReconnectTimeChangedListener = onReconnectTimeChanged
    }

    fun setOnBufferedTimeChangedListener(onReconnectTimeChanged: (bufferedTime: Int) -> Unit) {
        mOnBufferedTimeChangedListener = onReconnectTimeChanged
    }

    fun setOnMirrorRadioGroupClickedListener(onClick: (mode: MirrorMode) -> Unit) {
        mMirrorRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            onClick.invoke(
                when (checkedId) {
                    R.id.rb_mirror_none -> MirrorMode.MIRROR_MODE_NONE
                    R.id.rb_mirror_v -> MirrorMode.MIRROR_MODE_VERTICAL
                    R.id.rb_mirror_h -> MirrorMode.MIRROR_MODE_HORIZONTAL
                    else -> MirrorMode.MIRROR_MODE_NONE
                }
            )
        }
    }

    fun setOnScaleRadioGroupClickedListener(onClick: (mode: ScaleMode) -> Unit) {
        mScaleRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            onClick.invoke(
                when (checkedId) {
                    R.id.rb_scale_fit -> ScaleMode.SCALE_CONTAIN
                    R.id.rb_scale_crop -> ScaleMode.SCALE_COVER
                    R.id.rb_scale_scale -> ScaleMode.SCALE_FILL
                    else -> ScaleMode.SCALE_CONTAIN
                }
            )
        }
    }

    fun setOnViewportRatioRadioGroupClickedListener(onClick: (mode: ViewportRatioMode) -> Unit) {
        mViewportRatioRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            onClick.invoke(
                when (checkedId) {
                    R.id.rb_size_ratio_default -> ViewportRatioMode.DEFAULT
                    R.id.rb_size_ratio_16_9 -> ViewportRatioMode.Size_16_9
                    R.id.rb_size_ratio_4_3 -> ViewportRatioMode.Size_4_3
                    else -> ViewportRatioMode.DEFAULT
                }
            )
        }
    }

    fun setOnRotateRadioGroupClickedListener(onClick: (mode: RotateMode) -> Unit) {
        mRotateRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            onClick.invoke(
                when (checkedId) {
                    R.id.rb_rotate_none -> RotateMode.ROTATE_0
                    R.id.rb_rotate_90 -> RotateMode.ROTATE_90
                    R.id.rb_rotate_180 -> RotateMode.ROTATE_180
                    R.id.rb_rotate_270 -> RotateMode.ROTATE_270
                    else -> RotateMode.ROTATE_0
                }
            )
        }
    }

    fun setOnClearColorRadioGroupClickedListener(onClick: (color: Int) -> Unit) {
        mColorRadioGroup.setOnCheckedChangeListener { _, checkId ->
            onClick.invoke(
                when (checkId) {
                    R.id.rb_color_black -> COLOR_BLACK // 颜色值为RGBA顺序
                    R.id.rb_color_white -> COLOR_WHITE
                    R.id.rb_color_red -> COLOR_RED
                    else -> Color.BLACK
                }
            )
        }
    }

    fun setOnMediaInfoClickListener(onClick: ClickListener) {
        mMediaTextView.setOnClickListener { onClick.invoke() }
    }

    fun setOnStatisticsInfoClickListener(onClick: ClickListener) {
        mStatisticsTextView.setOnClickListener { onClick.invoke() }
    }
    fun setOnNetworkInfoClickListener(onClick: ClickListener) {
        mNetworkTextView.setOnClickListener { onClick.invoke() }
    }
    fun setOnDecoderInfoClickListener(onClick: ClickListener) {
        mDecoderTextView.setOnClickListener { onClick.invoke() }
    }
}