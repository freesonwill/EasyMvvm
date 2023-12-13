package com.xcjh.app.view.balldetail

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.LoginOrOutListener
import com.xcjh.base_lib.utils.loge

/**
 * 比赛详情中的播放界面
 * @author Administrator
 */
class MatchVideoPlayer : StandardGSYVideoPlayer {
    private val mContext: Context? = null

    //数据源
    private val mSourcePosition = 0
    private var mCoverImage: ImageView? = null

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context) {
        super.init(context)
        mContext = context
        initData()
    }

    private fun initData() {
        Debuger.disable()
        setViewShowState(mTitleTextView, GONE)
        setViewShowState(mBackButton, GONE)
        setViewShowState(mLockScreen, GONE)
        //setThumbImageView(findViewById(R.id.ThumbImageView));
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        setViewShowState(mLoadingProgressBar, VISIBLE)
        setViewShowState(mFullscreenButton, GONE)
        isNeedShowWifiTip = true
        dismissControlTime = 4000
        /*     OrientationUtils orientationUtils = new OrientationUtils((Activity) mContext, this, getOrientationOption());
        getFullscreenButton().setOnClickListener(view -> {
                  //  GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
                    if (orientationUtils.getIsLand() != 1) {
                        //直接横屏
                        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                        orientationUtils.resolveByClick();
                    }
                    startWindowFullscreen(mContext, false, false);
                }
        );*/
    }

    /**
     * 设置播放URL
     *
     */
    fun setUp(url: String?): Boolean {
        //全屏裁减显示
        // GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        // GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_16_9);
        Glide.with(this).load(url).into(mCoverImage!!)
        return setUp(url, false, "")
    }

    override fun getLayoutId(): Int {
        return R.layout.match_video_player
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.ic_v_pause)
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.ic_v_play)
            } else {
                imageView.setImageResource(R.drawable.ic_v_play)
            }
        }
    }

    // 标示遮罩出现或隐藏。（因方法会调用很多遍，所以用一个标志位控制）
    private var isBottomContainerShow = false

    // 遮罩出现
    override fun setTextAndProgress(secProgress: Int) {
        super.setTextAndProgress(secProgress)
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            "video setTextAndProgress遮罩出现=====================".loge("====")
        }
    }

    // 遮罩出现，自动播放完毕会调用这个
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            "video changeUiToCompleteShow遮罩出现=====================".loge("====")
        }
    }

    // 遮罩隐藏，到时间自动隐藏
    override fun hideAllWidget() {
        super.hideAllWidget()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            "video hideAllWidget遮罩隐藏=====================".loge("====")
        }
    }

    // 遮罩隐藏，点击隐藏会调用这个，不调用上面的。
    override fun changeUiToClear() {
        super.changeUiToClear()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            "video hideAllWidget遮罩隐藏=====================".loge("====")
        }
    }

    private var mLoginOrOutListener: ControlShowListener? = null

    fun setControlListener(listener: ControlShowListener) {
        mLoginOrOutListener = listener
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        mChangePosition = false
        mChangeVolume = false
        mBrightness = false
    }
}

/**
 * 控制器显示隐藏
 */
interface ControlShowListener {
    //
    fun onShow()

    fun onHide()
}
