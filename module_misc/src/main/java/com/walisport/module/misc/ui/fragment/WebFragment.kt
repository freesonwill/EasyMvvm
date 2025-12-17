package com.walisport.module.misc.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.misc.databinding.FragmentSeniorPartnerBinding
import com.walisport.module.misc.ui.viewmodel.SeniorPartnerViewModel
import kotlin.reflect.KClass

/**
 * Web浏览页，加载充提教程、实时返水等H5页面
 */
class WebFragment : BaseFragment<SeniorPartnerViewModel, FragmentSeniorPartnerBinding>() {

    override val vbClass: KClass<FragmentSeniorPartnerBinding> = FragmentSeniorPartnerBinding::class
    override val vmClass: KClass<SeniorPartnerViewModel> = SeniorPartnerViewModel::class

    //private val manager: UserDataManager by inject(UserDataManager::class.java)
    private val args by navArgs<WebFragmentArgs>()

    override fun initView(savedInstanceState: Bundle?) {
        val url = args.url
        initTitleBar()
        initWebView()
        mBinding.webView.loadUrl(url!!)
        //监听系统返回键
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!doFragmentBack()) {
                        navigateUp()
                    }
                }
            }
        )
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
                    return true
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }
            setBackgroundColor(arch.cayenne.lib.common.R.color.title_bg.getColor())
            setAttachedFragment(this@WebFragment)
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun initTitleBar() {
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

    fun doFragmentBack(): Boolean {
        if (mBinding.webView.canGoBack()) {
            mBinding.webView.goBack()
            return true
        }
        return false
    }
}