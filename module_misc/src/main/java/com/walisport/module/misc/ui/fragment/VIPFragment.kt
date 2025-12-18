package com.walisport.module.misc.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.misc.databinding.FragmentVipBinding
import com.walisport.module.misc.ui.viewmodel.VIPViewModel
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

/**
 * VIP页面， 内容由Web提供

 */
class VIPFragment : BaseFragment<VIPViewModel, FragmentVipBinding>() {

    override val vbClass: KClass<FragmentVipBinding> = FragmentVipBinding::class
    override val vmClass: KClass<VIPViewModel> = VIPViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)

    override fun initView(savedInstanceState: Bundle?) {
        launch {
            initTitleBar()
            initWebView()
            mBinding.webView.loadUrl(BizUrl.VIP.url)
        }
    }

    private fun initWebView() {
        with(mBinding.webView) {
            webViewClient = object :
                WLSWebViewClient(this) {
                override fun onFormResubmission(
                    view: WebView? ,
                    dontResend: Message? ,
                    resend: Message
                ) {
                    super.onFormResubmission(view, dontResend, resend)
                    resend.sendToTarget()
                }

                override fun onPageStarted(view: WebView? , url: String? , favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView , url: String?) {
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
                    webView: WebView ,
                    filePathCallback: ValueCallback<Array<Uri>> ,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    return true
                }

                override fun onProgressChanged(view: WebView , newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }
            setBackgroundColor(arch.cayenne.lib.common.R.color.title_bg.getColor())
            setAttachedFragment(this@VIPFragment)
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
        setStatusBar(StatusBarConfig ,mBinding.root)
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
}