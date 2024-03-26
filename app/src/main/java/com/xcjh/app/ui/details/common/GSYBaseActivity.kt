package com.xcjh.app.ui.details.common

import android.content.res.Configuration
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.base_lib.base.BaseViewModel
import kotlinx.android.synthetic.main.match_video_player.view.fullscreen
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager

/**
 * 详情模式播放页面基础类
 * @author Administrator
 */
abstract class GSYBaseActivity<VM : BaseViewModel, DB : ViewDataBinding,T : GSYBaseVideoPlayer?> :
    BaseActivity<VM, DB>() , VideoAllCallBack {
    protected var isPlay = false
    protected var isPause = false
    protected var orientationUtils: OrientationUtils? = null
    //是否锁定
    protected var isLock=false
    //是否全屏
    private  var isFullScreen=false
    //是否横屏
    private  var isLandscape=true



    /**
     * 选择普通模式
     */
    fun initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, gSYVideoPlayer, orientationOption)
        //初始化不打开外部的旋转
        orientationUtils!!.isEnable = false
        if (gSYVideoPlayer!!.fullscreenButton != null) {
            gSYVideoPlayer!!.fullscreenButton.setOnClickListener {
                showFull()
                clickForFullScreen()
            }
        }
        gSYVideoPlayer!!.isNeedShowWifiTip = true
        gSYVideoPlayer!!.dismissControlTime = 3000
        //点击是否锁定返回
        gSYVideoPlayer!!.setLockClickListener { view, lock ->
            isLock=lock

            if(isLandscape){
                if(isLock){
                    orientationUtils!!.isEnable = false
                }else{
                    orientationUtils!!.isEnable = true
                }
            }


        }
//        gSYVideoPlayer!!.isNeedOrientationUtils=false
//        gSYVideoPlayer!!.isRotateViewAuto=false
//        gSYVideoPlayer!!.isRotateWithSystem=false
//
//
//        orientationUtils!!.isRotateWithSystem=false
    }

    /**
     * 是否横屏
     */
   public fun  setIsLandscape(screen:Boolean){
        if(isLandscape){
            orientationUtils!!.isEnable = screen
        }

    }


    fun  setisLandscape(static:Boolean){
        isLandscape=static
        if(!isLandscape){
            orientationUtils!!.isEnable = false
        }
    }

    /**
     * 选择builder模式
     */
    fun initVideoBuilderMode() {
        initVideo()
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        gSYVideoOptionBuilder.setVideoAllCallBack(this)
            .build(gSYVideoPlayer)
    }
    fun showFull() {
        if (orientationUtils!!.isLand != 1) {
            //直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils!!.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        gSYVideoPlayer!!.startWindowFullscreen(this@GSYBaseActivity,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull())
    }

    fun exitFullScreen(){
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
    }

    override fun onBackPressed() {
        //判断是否锁定，如果锁定了就不用执行下面
        if(isLock){
            return
        }

        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }


        //判读是否全屏
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        gSYVideoPlayer?.onVideoPause()
        if (orientationUtils != null) {
            orientationUtils!!.setIsPause(true)
        }
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        gSYVideoPlayer?.onVideoResume(false)
        if (orientationUtils != null) {
            orientationUtils!!.setIsPause(false)
        }
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            gSYVideoPlayer?.release()
        }
        if (orientationUtils != null) {
            orientationUtils!!.releaseListener()
        }
        //  gSYVideoPlayer?.gsyVideoManager?.stop()
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏  旋转触发
//        if (isPlay && !isPause) {
//            gSYVideoPlayer!!.onConfigurationChanged(this,
//                newConfig,
//                orientationUtils,
//                hideActionBarWhenFull(),
//                hideStatusBarWhenFull())
//        }
        gSYVideoPlayer!!.onConfigurationChanged(this,
            newConfig,
            orientationUtils,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull())
    }

    override fun onStartPrepared(url: String, vararg objects: Any) {
//        orientationUtils!!.isEnable = true

    }
    override fun onPrepared(url: String, vararg objects: Any) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        //加载成功就可以设置自适应
        if(isFullScreen){
            GSYVideoType.setScreenScaleRatio(gSYVideoPlayer!!.gsyVideoManager.currentVideoWidth /gSYVideoPlayer!!.gsyVideoManager.currentVideoHeight.toFloat())
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_CUSTOM)
        }

        //开始播放了才能旋转和全屏
//        orientationUtils!!.isEnable = detailOrientationRotateAuto && !isAutoFullWithSize
        isPlay = true
        if(isLandscape){
            orientationUtils!!.isEnable = true
        }

    }



    override fun onClickStartIcon(url: String, vararg objects: Any) {}
    override fun onClickStartError(url: String, vararg objects: Any) {}
    override fun onClickStop(url: String, vararg objects: Any) {}
    override fun onClickStopFullscreen(url: String, vararg objects: Any) {}
    override fun onClickResume(url: String, vararg objects: Any) {}
    override fun onClickResumeFullscreen(url: String, vararg objects: Any) {}
    override fun onClickSeekbar(url: String, vararg objects: Any) {}
    override fun onClickSeekbarFullscreen(url: String, vararg objects: Any) {}
    override fun onAutoComplete(url: String, vararg objects: Any) {}
    override fun onEnterFullscreen(url: String, vararg objects: Any) {
                 isFullScreen=true
        screenStatus(true)
    }
    override fun onQuitFullscreen(url: String, vararg objects: Any) {
        screenStatus(false)

        isFullScreen=false
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
        }
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
 //        mDatabind.videoPlayer.fullscreen
    }

    override fun onQuitSmallWidget(url: String, vararg objects: Any) {}
    override fun onEnterSmallWidget(url: String, vararg objects: Any) {}
    override fun onTouchScreenSeekVolume(url: String, vararg objects: Any) {}
    override fun onTouchScreenSeekPosition(url: String, vararg objects: Any) {}
    override fun onTouchScreenSeekLight(url: String, vararg objects: Any) {}
    override fun onPlayError(url: String, vararg objects: Any) {
        if(isLandscape){
            orientationUtils!!.isEnable=false
        }

    }
    override fun onClickStartThumb(url: String, vararg objects: Any) {}
    override fun onClickBlank(url: String, vararg objects: Any) {}
    override fun onClickBlankFullscreen(url: String, vararg objects: Any) {}
    override fun onComplete(url: String, vararg objects: Any) {}
    fun hideActionBarWhenFull(): Boolean {
        return true
    }

    fun hideStatusBarWhenFull(): Boolean {
        return true
    }

    /**
     * 可配置旋转 OrientationUtils
     */
    val orientationOption: OrientationOption?
        get() = null

    /**
     * 播放控件
     */
    abstract val gSYVideoPlayer: T

    /**
     * 配置播放器
     */
    abstract val gSYVideoOptionBuilder: GSYVideoOptionBuilder

    /**
     * 点击了全屏
     */
    abstract fun clickForFullScreen()

    /**
     * 是否启动旋转横屏，true表示启动
     */
    abstract val detailOrientationRotateAuto: Boolean

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    val isAutoFullWithSize: Boolean
        get() = false

    /**
     * 屏幕状态   true就是全屏
     */
    abstract fun  screenStatus(status:Boolean)

}