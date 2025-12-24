package com.walisport.module.feedback.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.feedback.databinding.FragmentFeedbackMainBinding
import com.walisport.module.feedback.ui.viewmodel.FeedbackMainViewModel
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass


/**
 * 反馈详情页
 */
class FeedbackMainFragment : BaseFragment<FeedbackMainViewModel, FragmentFeedbackMainBinding>() {

    override val vbClass: KClass<FragmentFeedbackMainBinding> = FragmentFeedbackMainBinding::class
    override val vmClass: KClass<FeedbackMainViewModel> = FeedbackMainViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null //图片选择后返回h5


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                fileChooserCallback?.onReceiveValue(arrayOf(it))
            } ?: run {
                fileChooserCallback?.onReceiveValue(arrayOf(Uri.EMPTY))
            }
            fileChooserCallback = null;
        }

    override fun initView(savedInstanceState: Bundle?) {
        launch {
            initTitleBar()
            initWebView()
            mBinding.webView.loadUrl(BizUrl.FEEDBACK.url)
        }
    }


    private fun initWebView() {
        with(mBinding.webView) {
            webViewClient = object :
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
                    visibility = android.view.View.VISIBLE
                    super.onPageFinished(view, url)

                }
            }
            requestFocus()
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    fileChooserCallback = filePathCallback
                    chooseImages()
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    super.onPermissionRequest(request)
                    request?.grant(request.resources)
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }
            setBackgroundColor(arch.cayenne.lib.common.R.color.title_bg.getColor())
            setAttachedFragment(this@FeedbackMainFragment)
        }
    }

    private fun chooseImages() {
        try {
            startForResult.launch("image/*")
        } catch (e: Exception) {
            e.printStackTrace()
            fileChooserCallback?.onReceiveValue(arrayOf())
            fileChooserCallback = null
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun initTitleBar() {
        val type = arguments?.getString("type") ?: ""
        mBinding.root.touchBackPressed()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
    }

    override fun onResume() {
        mBinding.webView.onResume()
        mBinding.webView.resumeTimers()
        super.onResume()
    }

    override fun onPause() {
        mBinding.webView.onPause()
        mBinding.webView.pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        mBinding.webView.destroy()
        super.onDestroy()
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }


}