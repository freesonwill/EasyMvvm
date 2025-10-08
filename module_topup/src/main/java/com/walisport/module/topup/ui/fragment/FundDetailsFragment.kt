package com.walisport.module.topup.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.walisport.module.topup.databinding.FragmentFundDetailsBinding
import com.walisport.module.topup.ui.viewmodel.FundDetailsViewModel
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

/**
 * 资金明细页面， 内容由Web提供
 * @date: 2025/10/8 16:01
 * @description:
 */
class FundDetailsFragment : BaseFragment<FundDetailsViewModel, FragmentFundDetailsBinding>() {

    override val vbClass: KClass<FragmentFundDetailsBinding> = FragmentFundDetailsBinding::class
    override val vmClass: KClass<FundDetailsViewModel> = FundDetailsViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)


    override fun initView(savedInstanceState: Bundle?) {
        launch {
            initWebView()
            val uid = manager.getValue(UserDataKey.KEY_UID, -1)
            val token = manager.getValue(UserDataKey.KEY_TOKEN, "")

            mBinding.webView.loadUrl("https://www.google.com/")

        }
    }

    private suspend fun initWebView() {
        val webSettings = mBinding.webView.settings

        with(webSettings) {
            domStorageEnabled = true
            displayZoomControls = true
            databaseEnabled = true
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            blockNetworkImage = false
            setGeolocationEnabled(true)
            setGeolocationDatabasePath(
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    requireActivity().applicationContext.getDir(
                        "database",
                        android.content.Context.MODE_PRIVATE
                    ).path
                }
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

        with(mBinding.webView) {
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

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}