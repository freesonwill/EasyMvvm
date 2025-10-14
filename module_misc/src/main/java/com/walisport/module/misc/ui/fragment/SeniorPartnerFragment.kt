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
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.misc.databinding.FragmentSeniorPartnerBinding
import com.walisport.module.misc.ui.viewmodel.SeniorPartnerViewModel
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

/**
 * 高级合伙人页面， 内容由Web提供

 */
class SeniorPartnerFragment : BaseFragment<SeniorPartnerViewModel, FragmentSeniorPartnerBinding>() {

    override val vbClass: KClass<FragmentSeniorPartnerBinding> = FragmentSeniorPartnerBinding::class
    override val vmClass: KClass<SeniorPartnerViewModel> = SeniorPartnerViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)


    override fun initView(savedInstanceState: Bundle?) {
        launch {
            initWebView()
            val uid = manager.getValue(UserDataKey.KEY_UID, -1)
            val token = manager.getValue(UserDataKey.KEY_TOKEN, "")

            mBinding.webView.loadUrl("https://www.google.com/")

        }
    }

    private fun initWebView() {
        with(mBinding.webView) {

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

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoPadding = false)
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig,mBinding.root)
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