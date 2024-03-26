package com.xcjh.app.view.balldetail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.view.PopupBrightness
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import kotlinx.android.synthetic.main.match_video_player.view.ivMatchBgNew
import kotlinx.android.synthetic.main.match_video_player.view.lltLiveErrorNew
import kotlinx.android.synthetic.main.match_video_player.view.lltNoLiveNew


/**
 * 比赛详情中的播放界面
 * @author Administrator
 */
class MatchVideoPlayer : StandardGSYVideoPlayer {
    private val mContext: Context? = null

    //数据源
    private val mSourcePosition = 0
    private var mCoverImage: ImageView? = null
    private var thumb: RelativeLayout? = null

    private var llIsShow: LinearLayout? = null
    private var ivMatchVideoNew: ImageView? = null
    private var tvToShareNew: ImageView? = null
    private var  showDialog: PopupBrightness?=null
    private var popwindow: BasePopupView?=null
    private var videoRoon: ConstraintLayout?=null
    private var viewLeft: View?=null
    private var viewRght :   View?=null

    private var ivMaskTop : AppCompatImageView?=null
    private var ivMaskBottom : AppCompatImageView?=null
    private var ivIconTui :   ImageView?=null
      var ivMatchBgNew :   ImageView?=null
    var customPlayer:MatchVideoPlayer?=null
    private var lltLiveErrorNew :   LinearLayout?=null
    private var tvReloadNew :   TextView?=null
    private var lltNoLiveNew :   LinearLayout?=null
    private var tvSignal4New :   TextView?=null
    private var tvSignal3New :   TextView?=null



    //播放状态
    private var mStatus:Int=0

    //比赛类型
    private var matchType:String="22222"
    private var isBroadcasting: Boolean = false //当前是否播放视频
    private var isFullscreen: Boolean = false //当前是否全屏


    /**
     * 当前是否播放视频  ,是否进入全屏
     */
    fun  broadcasting(show:Boolean,enter:Boolean){
        isBroadcasting=show
//        isFullscreen=enter
        if(isBroadcasting){
            if(lltNoLiveNew!=null){
                lltNoLiveNew?.visibility= GONE
            }
        }else{
            if(lltNoLiveNew!=null){
                lltNoLiveNew?.visibility= VISIBLE
            }
        }

     }



    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var smallVideoHelper: GSYVideoHelper? = null

    override fun init(context: Context) {
        super.init(context)
        mContext = context
        initData()
        updateStartImage()
    }

    override fun setStateAndUi(state: Int) {
        super.setStateAndUi(state)
        mStatus=state
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            if (state == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(R.drawable.ic_v_pause)
            } else if (state == CURRENT_STATE_ERROR) {
                imageView.setImageResource(R.drawable.ic_v_play)
            } else {
                imageView.setImageResource(R.drawable.ic_v_play)
            }
        }
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
        mCoverImage = findViewById<View>(R.id.thumbImage) as ImageView
//        thumb = findViewById<View>(R.id.thumb) as RelativeLayout
//        thumb!!.visibility= VISIBLE

        llIsShow=findViewById<View>(R.id.llIsShow) as LinearLayout
        ivMatchVideoNew=findViewById<View>(R.id.ivMatchVideoNew) as ImageView
        tvToShareNew=findViewById<View>(R.id.tvToShareNew) as ImageView
//        videoRoon=findViewById<View>(R.id.videoRoon) as ConstraintLayout
        ivMaskTop=findViewById<View>(R.id.ivMaskTop) as AppCompatImageView
        ivMaskBottom=findViewById<View>(R.id.ivMaskBottom) as AppCompatImageView
        ivIconTui=findViewById<View>(R.id.ivIconTui) as ImageView
//        ivMatchBgNew=findViewById<View>(R.id.ivMatchBgNew) as ImageView
//        lltLiveErrorNew=findViewById<View>(R.id.lltLiveErrorNew) as LinearLayout
        tvReloadNew=findViewById<View>(R.id.tvReloadNew) as TextView
        //是否显示开播
//        lltNoLiveNew=findViewById<View>(R.id.lltNoLiveNew) as LinearLayout
//        setScreenStatus(mIfCurrentIsFullscreen)
        //封面父布局
        setViewShowState(mThumbImageViewLayout, GONE)
        //加载中的动画
        setViewShowState(mLoadingProgressBar, VISIBLE)
        //全屏按钮
        setViewShowState(fullscreenButton, GONE)
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

            fullscreenButton.performClick()
            appViewModel.landscapeShareEvent.postValue(1)
        }
        //重新加载
        tvReloadNew!!.setOnClickListener {
            mStartButton.performClick()

        }
        //投屏
        ivMatchVideoNew!!.setOnClickListener {

            fullscreenButton.performClick()
            appViewModel.landscapeShareEvent.postValue(2)
        }
        //退出全屏
        ivIconTui!!.setOnClickListener {
            fullscreenButton.performClick()
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
//        mOrientationUtils=OrientationUtils(mContext as Activity,this,orientationOption)

//        var orientationUtils= OrientationUtils(mContext as Activity,this,orientationOption)
//
//
//         mFullscreenButton.setOnClickListener {
//            if (orientationUtils.getIsLand() != 1) {
//                //直接横屏
//                // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
//                // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
////                isNeedOrientationUtils = false
////                orientationUtils.resolveByClick()
//            }
//
//
//            startWindowFullscreen(mContext, false, false);
//        }

    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
    }



    /**
     * 退出全屏
     */
    fun exitFullScreen(){
        fullscreenButton.performClick()
    }

    /**
     * 监听是否锁定
     */
    override fun lockTouchLogic() {
        super.lockTouchLogic()
        //mLockCurScreen

         if (mLockCurScreen) {
             llIsShow!!.visibility=View.GONE
             ivMaskTop!!.visibility=View.GONE
             ivMaskBottom!!.visibility=View.GONE
             ivIconTui!!.visibility=View.GONE
            mLockScreen.setImageResource( R.drawable.detaic_tv_lock)
        }else{
             llIsShow!!.visibility=View.VISIBLE
             ivMaskBottom!!.visibility=View.VISIBLE
             ivIconTui!!.visibility=View.VISIBLE
            mLockScreen.setImageResource( R.drawable.detaic_tv_unlock)
        }
    }

    /**
     * 获取到播放状态
     */
   public fun playbackStatus(status:Int){
//        mStatus= status


    }


    /**
     * 克隆参数代码
     */
    override fun cloneParams(from: GSYBaseVideoPlayer?, to: GSYBaseVideoPlayer?) {
        super.cloneParams(from, to)
        var customFrom: MatchVideoPlayer = from as MatchVideoPlayer
        var customTo: MatchVideoPlayer = to as MatchVideoPlayer
        customTo.ivMatchBgNew = customFrom.ivMatchBgNew
        customTo.lltLiveErrorNew = customFrom.lltLiveErrorNew
        customTo.tvSignal4New = customFrom.tvSignal4New
        customTo.lltNoLiveNew = customFrom.lltNoLiveNew
        customTo.tvSignal3New = customFrom.tvSignal3New
        customTo.viewLeft = customFrom.viewLeft
        customTo.viewRght = customFrom.viewRght

    }
    /**
     * 横屏
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
    val sampleVideo  = super.startWindowFullscreen(context, actionBar, statusBar) as MatchVideoPlayer
    sampleVideo.mListItemRect = mListItemRect
    sampleVideo.mListItemSize = mListItemSize
    sampleVideo.matchType = matchType
    sampleVideo.isBroadcasting = isBroadcasting
    sampleVideo.isFullscreen = true
//        ivMatchBgNew!!.visibility= VISIBLE
        customPlayer = sampleVideo as MatchVideoPlayer
        customPlayer?.ivMatchBgNew = customPlayer?.findViewById(R.id.ivMatchBgNew)
        customPlayer?.lltLiveErrorNew = customPlayer?.findViewById(R.id.lltLiveErrorNew)
        customPlayer?.tvSignal4New = customPlayer?.findViewById(R.id.tvSignal4New)
        customPlayer?.lltNoLiveNew = customPlayer?.findViewById(R.id.lltNoLiveNew)
        customPlayer?.tvSignal3New = customPlayer?.findViewById(R.id.tvSignal3New)
        customPlayer?.viewLeft = customPlayer?.findViewById(R.id.viewLeft)
        customPlayer?.viewRght = customPlayer?.findViewById(R.id.viewRght)
        GSYVideoType.setScreenScaleRatio( customPlayer!!.gsyVideoManager.currentVideoWidth /customPlayer!!.gsyVideoManager.currentVideoHeight.toFloat())
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_CUSTOM)

        val params =  customPlayer?.viewLeft?.layoutParams as LinearLayout.LayoutParams
        params.setMargins(mContext.dp2px(69), 0, 0, 0)
        customPlayer?.viewLeft?.layoutParams=params

        val paramsNew =  customPlayer?.viewRght?.layoutParams as LinearLayout.LayoutParams
        paramsNew.setMargins(0, 0,  mContext.dp2px(69), 0)
        customPlayer?.viewRght?.layoutParams=paramsNew

        //加载失败选择信号源
        customPlayer?.tvSignal4New?.setOnClickListener {


            appViewModel.landscapeShareEvent.postValue(3)


//            customPlayer?.lltLiveErrorNew?.visibility=View.GONE
//            customPlayer?.ivMatchBgNew?.visibility=View.GONE
//            customPlayer?.lltNoLiveNew.visibility=View.GONE
        }

        //关闭选择信号源
        customPlayer?.tvSignal3New?.setOnClickListener {

            appViewModel.landscapeShareEvent.postValue(3)
        }


//        if(isBroadcasting){
//            customPlayer?.lltNoLiveNew!!.visibility= GONE
//            if(mStatus==0||mStatus==1){
//                customPlayer?.ivMatchBgNew!!.visibility= VISIBLE
//            }else if(mStatus==2){//播放
//                customPlayer?.ivMatchBgNew!!.visibility= GONE
//            }else if(mStatus==7){//错误
//                customPlayer?.ivMatchBgNew!!.visibility= VISIBLE
//                customPlayer?.lltLiveErrorNew!!.visibility= VISIBLE
//            }else{
//                customPlayer?.ivMatchBgNew!!.visibility= VISIBLE
//            }
//        }else{
//            customPlayer?.ivMatchBgNew!!.visibility= VISIBLE
//            customPlayer?.lltLiveErrorNew!!.visibility= GONE
//            customPlayer?.lltNoLiveNew!!.visibility= VISIBLE
//
//        }



    return sampleVideo

//        return super.startWindowFullscreen(context, actionBar, statusBar)
    }



    /**
     * 竖屏
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        var sampleVideo =  gsyVideoPlayer as MatchVideoPlayer
        matchType = sampleVideo.matchType
        isBroadcasting = sampleVideo.isBroadcasting
        isFullscreen =false


        if (gsyVideoPlayer != null) {
             customPlayer = gsyVideoPlayer as MatchVideoPlayer?
            ivMatchBgNew = findViewById <View>(R.id.ivMatchBgNew) as ImageView
            lltLiveErrorNew = findViewById <View>(R.id.lltLiveErrorNew) as LinearLayout
            tvSignal4New = findViewById <View>(R.id.tvSignal4New) as TextView
            lltNoLiveNew = findViewById <View>(R.id.lltNoLiveNew) as LinearLayout
            tvSignal3New = findViewById <View>(R.id.tvSignal3New) as TextView
            viewLeft = findViewById <View>(R.id.viewLeft) as View
            viewRght = findViewById <View>(R.id.viewRght) as View
            customPlayer?.ivMatchBgNew!!.visibility= GONE
            customPlayer?.lltLiveErrorNew!!.visibility= GONE
            customPlayer?.tvSignal4New!!.visibility= GONE
            customPlayer?.lltNoLiveNew!!.visibility= GONE

            llIsShow!!.visibility=View.GONE
            ivIconTui!!.visibility=View.GONE

            viewLeft!!.visibility=View.INVISIBLE

            val params = viewLeft!!.layoutParams as LinearLayout.LayoutParams
            params.setMargins(0, 0, 0, 0)
            viewLeft?.layoutParams=params

            val paramsNEw = viewLeft!!.layoutParams as LinearLayout.LayoutParams
            paramsNEw.setMargins(0, 0, 0, 0)
            viewRght?.layoutParams=paramsNEw
        }


        Log.i("SSSSSSSSSSSSSss","3333333333333==="+matchType)

    }




    /**
     * 设置全屏默认封面
     */
    fun  setFullScreenCover(type:String){
        //bg_top_basketball  bg_top_football
        matchType=type
//        if(ivMatchBgNew!=null){
//            if(matchType=="1"){
//                Glide.with(mContext).load(R.drawable.bg_top_football).into(ivMatchBgNew!!)
//            }else{
//                Glide.with(mContext).load(R.drawable.bg_top_basketball).into(ivMatchBgNew!!)
//            }
//        }

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
     * 全屏和竖屏回调  true是全屏
     */
    fun setScreenStatus(st:Boolean){
//        ivMatchBgNew!!.visibility= View.VISIBLE
        //全屏
        if(st){
        if(isBroadcasting){
//            lltNoLiveNew!!.visibility= GONE
//            lltLiveErrorNew!!.visibility= GONE
            //准备中
            if(mStatus==0||mStatus==1){
//                ivMatchBgNew!!.visibility= View.VISIBLE
            }else if(mStatus==2){//播放
//                ivMatchBgNew!!.visibility= View.GONE
            }else if(mStatus==7){//错误
                ivMatchBgNew!!.visibility= View.VISIBLE
//                ivMatchBgNew!!.visibility= View.VISIBLE
//                lltLiveErrorNew!!.visibility= View.VISIBLE
            }else{
//                ivMatchBgNew!!.visibility= View.VISIBLE
            }
        }else{
//            ivMatchBgNew!!.visibility= View.VISIBLE
//            lltNoLiveNew!!.visibility=View. VISIBLE
        }



        }else{
//            ivMatchBgNew!!.visibility= GONE
//            lltLiveErrorNew!!.visibility= GONE
//            lltNoLiveNew!!.visibility= GONE

        }
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

    fun setFullscreenButton() {
        Log.i("NNNNNNNNNNN","22222222222222222222")
        if (mIfCurrentIsFullscreen) {

            if (mLockCurScreen) {
                llIsShow!!.visibility = View.GONE
                ivIconTui!!.visibility = View.GONE
                ivMaskTop!!.visibility = View.GONE
                ivMaskBottom!!.visibility = View.GONE
            } else {
                llIsShow!!.visibility = View.VISIBLE
                ivIconTui!!.visibility = View.VISIBLE
                ivMaskTop!!.visibility = View.VISIBLE
                ivMaskBottom!!.visibility = View.VISIBLE
            }
            if (fullscreenButton != null) {
//                val myView: View = fullscreenButton
//                val layoutParams: ViewGroup.LayoutParams = myView.layoutParams
//                // 设置新的宽度和高度
//                layoutParams.width = context.dp2px(35)
//                layoutParams.height = context.dp2px(35)
//                myView.layoutParams = layoutParams


            } else {
                llIsShow!!.visibility = View.GONE
                ivMaskTop!!.visibility = View.GONE
                ivMaskBottom!!.visibility = View.GONE
                ivIconTui!!.visibility = View.GONE
                if (fullscreenButton != null) {
//                    val myView: View = fullscreenButton
//                    val layoutParams: ViewGroup.LayoutParams = myView.layoutParams
//                    // 设置新的宽度和高度
//                    layoutParams.width = context.dp2px(30)
//                    layoutParams.height = context.dp2px(30)
//
//                    myView.layoutParams = layoutParams

                }

            }
        }
    }
    // 遮罩出现
    override fun setTextAndProgress(secProgress: Int) {
        super.setTextAndProgress(secProgress)
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            setViewShowState(fullscreenButton, VISIBLE)
//            setViewShowState(startButton, VISIBLE)

            setFullscreenButton()
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
            "video setTextAndProgress遮罩出现=====================".loge("====")
        }
    }



    // 遮罩出现，自动播放完毕会调用这个
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        if (mBottomContainer.visibility == VISIBLE && !isBottomContainerShow) {
            isBottomContainerShow = true
            mLoginOrOutListener?.onShow()
            setViewShowState(fullscreenButton, VISIBLE)
//            setViewShowState(startButton, VISIBLE)
            setFullscreenButton()
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
            "video changeUiToCompleteShow遮罩出现=====================".loge("====")
        }
    }

    // 遮罩隐藏，到时间自动隐藏
    override fun hideAllWidget() {
        super.hideAllWidget()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            setViewShowState(fullscreenButton, GONE)
//            setViewShowState(startButton, GONE)
            llIsShow!!.visibility=View.GONE
            ivMaskTop!!.visibility=View.GONE
            ivMaskBottom!!.visibility=View.GONE
            ivIconTui!!.visibility=View.GONE

            "video hideAllWidget遮罩隐藏=====================".loge("====")
        }
    }

    // 遮罩隐藏，点击隐藏会调用这个，不调用上面的。
    override fun changeUiToClear() {
        super.changeUiToClear()
        if (mBottomContainer.visibility != VISIBLE && isBottomContainerShow) {
            isBottomContainerShow = false
            mLoginOrOutListener?.onHide()
            setViewShowState(fullscreenButton, GONE)
//            setViewShowState(startButton, GONE)
            llIsShow!!.visibility=View.GONE
            ivMaskTop!!.visibility=View.GONE
            ivMaskBottom!!.visibility=View.GONE
            ivIconTui!!.visibility=View.GONE

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


