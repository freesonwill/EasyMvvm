package com.xcjh.app.view.balldetail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.android.cling.ClingDLNAManager
import com.android.cling.entity.ClingDevice
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.net.ApiComService
import com.xcjh.app.ui.details.common.GSYBaseActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.view.PopupBrightness
import com.xcjh.app.view.PopupSelectProjection
import com.xcjh.base_lib.utils.activityManager
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.shareText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * 比赛详情中的播放界面
 * @author Administrator
 */
class MatchVideoPlayer : StandardGSYVideoPlayer {
    private val mContext: Context? = null

    //数据源
    private val mSourcePosition = 0
    private var mCoverImage: ImageView? = null

    private var llIsShow: LinearLayout? = null
    private var ivMatchVideoNew: ImageView? = null
    private var tvToShareNew: ImageView? = null
    private var  showDialog: PopupBrightness?=null
    private var popwindow: BasePopupView?=null
    private var videoRoon: ConstraintLayout?=null




    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var smallVideoHelper: GSYVideoHelper? = null

    override fun init(context: Context) {
        super.init(context)
        mContext = context

        initData()

    }




    private fun initData() {
        Debuger.disable()
        showDialog= PopupBrightness(mContext)
        popwindow=XPopup.Builder(mContext)
            .hasShadowBg(false)
            .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
            .isViewMode(true)
            .isClickThrough(true)
            .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
            .asCustom(showDialog)


        smallVideoHelper=GSYVideoHelper(mContext,this)
        setViewShowState(mTitleTextView, GONE)
        setViewShowState(mBackButton, GONE)
        setViewShowState(mLockScreen, GONE)
        //setThumbImageView(findViewById(R.id.ThumbImageView));
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
        llIsShow=findViewById<View>(R.id.llIsShow) as LinearLayout
        ivMatchVideoNew=findViewById<View>(R.id.ivMatchVideoNew) as ImageView
        tvToShareNew=findViewById<View>(R.id.tvToShareNew) as ImageView
        videoRoon=findViewById<View>(R.id.videoRoon) as ConstraintLayout

        //封面父布局
        setViewShowState(mThumbImageViewLayout, VISIBLE)
        //加载中的动画
        setViewShowState(mLoadingProgressBar, VISIBLE)
        //全屏按钮
        setViewShowState(mFullscreenButton, GONE)
        //显示锁定
        setViewShowState(mLockScreen, VISIBLE)
        isNeedShowWifiTip = true
        dismissControlTime = 4000
        mLockScreen.setImageResource( R.drawable.detaic_tv_unlock)
        val myView: View =mLockScreen
        val layoutParams: ViewGroup.LayoutParams = myView.layoutParams

// 设置新的宽度和高度
        layoutParams.width = mContext.dp2px(34)
        layoutParams.height = mContext.dp2px(34)

        myView.layoutParams = layoutParams

        mChangePosition = false
        mChangeVolume = true
        mBrightness = true
        mContext

        //分享按钮
        tvToShareNew!!.setOnClickListener {

            mFullscreenButton.performClick()
            appViewModel.landscapeShareEvent.postValue(1)
        }
        //投屏
        ivMatchVideoNew!!.setOnClickListener {

            mFullscreenButton.performClick()
            appViewModel.landscapeShareEvent.postValue(2)
        }



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
        var orientationUtils= OrientationUtils(mContext as Activity,this,orientationOption)
        mFullscreenButton.setOnClickListener {
            if (orientationUtils.getIsLand() != 1) {
                //直接横屏
                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
//                isNeedOrientationUtils = false
//                orientationUtils.resolveByClick()
            }
            startWindowFullscreen(mContext, false, false);
        }

    }


    /**
     * 监听是否锁定
     */
    override fun lockTouchLogic() {
        super.lockTouchLogic()
        //mLockCurScreen

         if (mLockCurScreen) {
             llIsShow!!.visibility=View.GONE
            mLockScreen.setImageResource( R.drawable.detaic_tv_lock)
        }else{
             llIsShow!!.visibility=View.VISIBLE
            mLockScreen.setImageResource( R.drawable.detaic_tv_unlock)
        }
    }

    /**
     * 自定义亮度
     */
    @SuppressLint("MissingInflatedId")
    override fun showBrightnessDialog(percent: Float) {
        //是否打开
        if(!popwindow!!.isShow){
            popwindow!!.show()
        }
        showDialog!!.setBrightness((percent * 100).toInt())

    }

    override fun dismissBrightnessDialog() {
        super.dismissBrightnessDialog()
        if(popwindow!!.isShow){
            popwindow!!.dismiss()
        }
    }


    /**
     * 自定义音量
     */
    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        if (mVolumeDialog == null) {
            val localView = LayoutInflater.from(activityContext).inflate(R.layout.dialog_video_volume, null)
            if (localView.findViewById<View>(volumeProgressId) is ProgressBar) {
                mDialogVolumeProgressBar =
                    localView.findViewById<View>(volumeProgressId) as ProgressBar
                if (mVolumeProgressDrawable != null && mDialogVolumeProgressBar != null) {
                    mDialogVolumeProgressBar.progressDrawable = mVolumeProgressDrawable
                }
            }
            mVolumeDialog = Dialog(activityContext, com.shuyu.gsyvideoplayer.R.style.video_style_dialog_progress)
            mVolumeDialog.setContentView(localView)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mVolumeDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mVolumeDialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val localLayoutParams = mVolumeDialog.window!!.attributes
            localLayoutParams.gravity = Gravity.CENTER
            localLayoutParams.width = width
            localLayoutParams.height = height
            val location = IntArray(2)
            getLocationOnScreen(location)
            localLayoutParams.x = location[0]
            localLayoutParams.y = location[1]
            mVolumeDialog.window!!.attributes = localLayoutParams
        }
        if (!mVolumeDialog.isShowing) {
            mVolumeDialog.show()
        }
        if (mDialogVolumeProgressBar != null) {
            mDialogVolumeProgressBar.progress = volumePercent
        }
    }

    /**
     * 获取当前是否锁定屏幕
     */
    public fun getLockState() :Boolean{
        return mLockCurScreen
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





    fun getUrl():String{
        return  mOriginUrl
    }

    override fun getLayoutId(): Int {
        return R.layout.match_video_player
    }

    override fun updateStartImage() {



        val params = mStartButton.layoutParams as ConstraintLayout.LayoutParams
        if(mIfCurrentIsFullscreen){
            params.marginStart = context.dp2px(50) // 对于LTR布局中的开始边距

        }else{
            params.marginStart = context.dp2px(10) // 对于LTR布局中的开始边距

        }
        // 将更新后的布局参数应用到ImageView
        mStartButton.layoutParams = params

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
    /**
     * 屏幕改变状态 type  1是竖屏  2是全屏
     */
    public fun  getScreenState(type:Int){
        // 假设imageView是你的ImageView实例







    }

    fun setFullscreenButton(){
        if (mIfCurrentIsFullscreen) {

            if(mLockCurScreen){
                llIsShow!!.visibility=View.GONE
            }else{
                llIsShow!!.visibility=View.VISIBLE
            }


            if (mFullscreenButton != null){
                val myView: View =mFullscreenButton
                val layoutParams: ViewGroup.LayoutParams = myView.layoutParams
                // 设置新的宽度和高度
                layoutParams.width = context.dp2px(35)
                layoutParams.height = context.dp2px(35)

                myView.layoutParams = layoutParams

            }
            if (mFullscreenButton != null) mFullscreenButton.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.detaic_tv_icon_cioss))
        } else {
            llIsShow!!.visibility=View.GONE
            if (mFullscreenButton != null){
                val myView: View =mFullscreenButton
                val layoutParams: ViewGroup.LayoutParams = myView.layoutParams
                // 设置新的宽度和高度
                layoutParams.width = context.dp2px(30)
                layoutParams.height = context.dp2px(30)

                myView.layoutParams = layoutParams

            }
            if (mFullscreenButton != null) mFullscreenButton.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.detaic_tv_icon_screen))
        }
    }
    // 遮罩出现
    override fun setTextAndProgress(secProgress: Int) {
        super.setTextAndProgress(secProgress)
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            setViewShowState(mFullscreenButton, VISIBLE)

            setFullscreenButton()
            "video setTextAndProgress遮罩出现=====================".loge("====")
        }
    }



    // 遮罩出现，自动播放完毕会调用这个
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            setViewShowState(mFullscreenButton, VISIBLE)

            setFullscreenButton()
            "video changeUiToCompleteShow遮罩出现=====================".loge("====")
        }
    }

    // 遮罩隐藏，到时间自动隐藏
    override fun hideAllWidget() {
        super.hideAllWidget()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            setViewShowState(mFullscreenButton, GONE)
            "video hideAllWidget遮罩隐藏=====================".loge("====")
        }
    }

    // 遮罩隐藏，点击隐藏会调用这个，不调用上面的。
    override fun changeUiToClear() {
        super.changeUiToClear()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            setViewShowState(mFullscreenButton, GONE)
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
//        mChangeVolume = true
//        mBrightness = true
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
