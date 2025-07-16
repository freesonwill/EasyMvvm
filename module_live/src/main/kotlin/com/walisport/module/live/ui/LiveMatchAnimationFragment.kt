package com.walisport.module.live.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.BUTTONS_ANIMATION_DURATION
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.HIDE_BUTTONS_TIMER
import com.walisport.module.live.databinding.FragmentLiveMatchAnimationBinding
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchAnimationViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import kotlinx.coroutines.Job
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
    private val mediaViewModel: LiveMatchMediaViewModel by sharedViewModel<LiveMatchMediaViewModel, LiveMatchMediaFragment>()


    private var buttonsDisplaying = true

    /**
     * 隐藏操作栏的定时Job
     */
    private var scheduledHideButtonsJob: Job? = null


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel

        initWebView()
        scheduleHideButtons()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        mBinding.ivChooseSource.setOnClickListener {
            mediaViewModel.chooseSourceView()
        }

        //单击事件处理
        mBinding.ctMatchAnimation.setOnClickListener() {

        }

        mBinding.animationView.setOnTouchListener { v, event -> //单击事件
            if (buttonsDisplaying) {
                buttonsDisplaying = false

                hideButtonsAnimated()
            } else {
                buttonsDisplaying = true

                showButtonsAnimated()
            }
            true
        }


    }

    override fun createObserver() {
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }

        with(mViewModel) {
            //比赛动画url监听
            animationLiveUrl.observe(viewLifecycleOwner) { url ->
                url?.also {
                    "url:$url".logd(TAG)
                    if (url != mBinding.animationView.url) {
                        mBinding.animationView.loadUrl(it)
                    }
                }
            }

            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.source.isEmpty()) {
                        mBinding.ivChooseSource.visibility = View.INVISIBLE
                    } else {
                        mBinding.ivChooseSource.visibility = View.VISIBLE

                    }
                }
            }
        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }

    private fun initWebView() {
        val webSettings = mBinding.animationView.settings

        with(webSettings) {
            domStorageEnabled = true
            displayZoomControls = true
            databaseEnabled = true
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            blockNetworkImage = false
            setGeolocationEnabled(true)
            setGeolocationDatabasePath(
                requireActivity().applicationContext.getDir(
                    "database",
                    android.content.Context.MODE_PRIVATE
                ).path
            )
            useWideViewPort = true
            loadWithOverviewMode = true
            defaultTextEncodingName = "UTF-8"
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

        }

        with(mBinding.animationView) {
            setDefaultHandler(DefaultHandler())

            setWebViewClient(object :
                BridgeWebViewClient(this) {

                override fun onFormResubmission(
                    view: WebView?,
                    dontResend: Message?,
                    resend: Message
                ) {
                    super.onFormResubmission(view, dontResend, resend)
                    resend.sendToTarget()
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    view.settings.apply {
                        blockNetworkImage = false
                        if (!loadsImagesAutomatically) {
                            loadsImagesAutomatically = true
                        }
                    }
                    super.onPageFinished(view, url)
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
        }
    }


    /**
     * 设置定时任务，隐藏操作栏
     */
    private fun scheduleHideButtons() {
        scheduledHideButtonsJob?.cancel()
        scheduledHideButtonsJob = lifecycleScope.launch {
            delay(HIDE_BUTTONS_TIMER)

            buttonsDisplaying = false
            hideButtonsAnimated()
        }

    }

    /**
     * 隐藏底部操作栏
     */
    private fun hideButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        mBinding.root.startSafeAnimateSet(
            {
                playTogether(
                    mBinding.bottomArea.startSafeObjectAnimator(
                        "translationY",
                        *floatArrayOf(0f, operateAreaHeight)
                    ),
                    mBinding.bottomArea.startSafeObjectAnimator("alpha", 1f, 0.5f)
                )
            },
            duration = BUTTONS_ANIMATION_DURATION,
            interpolator = LinearInterpolator(),
            start = true
        )
    }

    /**
     * 展示底部操作栏
     */
    private fun showButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        mBinding.root.startSafeAnimateSet(
            {
                playTogether(
                    mBinding.bottomArea.startSafeObjectAnimator(
                        "translationY",
                        *floatArrayOf(operateAreaHeight, 0f)
                    ),
                    mBinding.bottomArea.startSafeObjectAnimator(
                        "alpha",
                        *floatArrayOf(0.5f, 1f)
                    ),

                    )
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        scheduleHideButtons()
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                })
            },
            duration = BUTTONS_ANIMATION_DURATION,
            start = true
        )
    }


    companion object {
        const val TAG = "LiveMatchAnimationFragment"
    }


}