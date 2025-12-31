package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.MatchBasicInfoBean.MatchStatus
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.live.databinding.FragmentLiveMatchAnimationBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchAnimationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


/**
 * 比赛动画页
 */
class LiveMatchAnimationFragment :
    BaseFragment<LiveMatchAnimationViewModel, FragmentLiveMatchAnimationBinding>() {
    override val vbClass: KClass<FragmentLiveMatchAnimationBinding> =
        FragmentLiveMatchAnimationBinding::class
    override val vmClass: KClass<LiveMatchAnimationViewModel> = LiveMatchAnimationViewModel::class

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

//
//    private var buttonsDisplaying = true
//
//    /**
//     * 隐藏操作栏的定时Job
//     */
//    private var scheduledHideButtonsJob: Job? = null


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel

        val metrics = resources.displayMetrics

        //density和scaledDensity被篡改，尝试恢复
        if (metrics.density != DensityInfo.density && DensityInfo.density > 0) {
            metrics.density = DensityInfo.density
        }
        if (metrics.scaledDensity != DensityInfo.scaledDensity && DensityInfo.scaledDensity > 0) {
            metrics.scaledDensity = DensityInfo.scaledDensity
        }
        launch {
            initWebView()
        }
//        scheduleHideButtons()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        mBinding.backView.touchBackPressed()
//        mBinding.animationView.setOnTouchListener { v, event -> //单击事件
//            if (buttonsDisplaying) {
//                buttonsDisplaying = false
//
//                hideButtonsAnimated()
//            } else {
//                buttonsDisplaying = true
//
//                showButtonsAnimated()
//            }
//            true
//        }


    }

    override suspend fun createObserver() {
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }
        mainViewModel.status.observe(viewLifecycleOwner){
            if (it !in listOf(MatchStatus.NOT_STARTED.code, MatchStatus.PAUSED.code)) {
                mBinding.backView.visibility = View.VISIBLE
            }else{
                mBinding.backView.visibility = View.GONE
            }
        }
        with(mViewModel) {
            //比赛动画url监听
            animationLiveUrl.observe(viewLifecycleOwner) { url ->
                url?.also {
                    "url:$url".logd(TAG)
                    if (url != mBinding.animationView.url) {
                        mBinding.ivVideoLoading.visibility = View.VISIBLE
                        mBinding.animationView.loadUrl(it)
                    }
                }
            }

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private suspend fun initWebView() {
        with(mBinding.animationView) {

            setWebViewClient(object :
                WLSWebViewClient(this) {

                override fun onFormResubmission(
                    view: WebView?,
                    dontResend: Message?,
                    resend: Message
                ) {
                    super.onFormResubmission(view, dontResend, resend)
                    resend.sendToTarget()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    view.settings.apply {
                        blockNetworkImage = false
                        if (!loadsImagesAutomatically) {
                            loadsImagesAutomatically = true
                        }
                    }
                    visibility = View.VISIBLE
                    super.onPageFinished(view, url)
                    lifecycleScope.launch {
                        delay(850)
                        mBinding.ivVideoLoading.visibility = View.GONE
                    }
                }
            })

            requestFocus()
            setWebChromeClient(object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    return true
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    //                showProgress(newProgress)
                }
            })

            setBackgroundColor(arch.cayenne.lib.common.R.color.black.getColor())
        }
    }

    override fun onResume() {
        mBinding.animationView.onResume()
        mBinding.animationView.resumeTimers()
        super.onResume()
    }

    override fun onPause() {
        mBinding.animationView.onPause()
        mBinding.animationView.pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        mBinding.animationView.destroy()
        super.onDestroy()
    }


//    /**
//     * 设置定时任务，隐藏操作栏
//     */
//    private fun scheduleHideButtons() {
//        scheduledHideButtonsJob?.cancel()
//        scheduledHideButtonsJob = lifecycleScope.launch {
//            delay(HIDE_BUTTONS_TIMER)
//
//            buttonsDisplaying = false
//            hideButtonsAnimated()
//        }
//
//    }
//
//    /**
//     * 隐藏底部操作栏
//     */
//    private fun hideButtonsAnimated() {
//        val operateAreaHeight =
//            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()
//
//        mBinding.root.startSafeAnimateSet(
//            {
//                playTogether(
//                    mBinding.bottomArea.startSafeObjectAnimator(
//                        "translationY",
//                        *floatArrayOf(0f, operateAreaHeight)
//                    ),
//                    mBinding.bottomArea.startSafeObjectAnimator("alpha", 1f, 0.5f)
//                )
//            },
//            duration = BUTTONS_ANIMATION_DURATION,
//            interpolator = LinearInterpolator(),
//            start = true
//        )
//    }

//    /**
//     * 展示底部操作栏
//     */
//    private fun showButtonsAnimated() {
//        val operateAreaHeight =
//            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()
//
//        mBinding.root.startSafeAnimateSet(
//            {
//                playTogether(
//                    mBinding.bottomArea.startSafeObjectAnimator(
//                        "translationY",
//                        *floatArrayOf(operateAreaHeight, 0f)
//                    ),
//                    mBinding.bottomArea.startSafeObjectAnimator(
//                        "alpha",
//                        *floatArrayOf(0.5f, 1f)
//                    ),
//
//                    )
//                addListener(object : Animator.AnimatorListener {
//                    override fun onAnimationStart(animation: Animator) {
//                    }
//
//                    override fun onAnimationEnd(animation: Animator) {
//                        scheduleHideButtons()
//                    }
//
//                    override fun onAnimationCancel(animation: Animator) {
//                    }
//
//                    override fun onAnimationRepeat(animation: Animator) {
//                    }
//                })
//            },
//            duration = BUTTONS_ANIMATION_DURATION,
//            start = true
//        )
//    }


    companion object {
        const val TAG = "LiveMatchAnimationFragment"
    }


}