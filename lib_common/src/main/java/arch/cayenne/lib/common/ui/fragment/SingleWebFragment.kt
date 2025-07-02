package arch.cayenne.lib.common.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.databinding.FragmentSingleWebBinding
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

/**
 * @author zhangsan
 * @date 2025/4/23 14:51
 * @description 加载 URL 的 Fragment
 *
 * @usage
 * Option1：
 * ```
 * navigate(R.string.deeplink_single_web_fragment.deeplink("title=baidu", "url=https://www.baidu.com/"))
 * // or
 * navigate(R.string.deeplink_single_web_fragment.deeplink("title=baidu&url=https://www.baidu.com/"))
 * ```
 *
 * Option2：
 * ```
 * navigate(R.string.deeplink_single_web_fragment.deeplink("title" to "baidu", "url" to "https://www.baidu.com/"))
 * ```
 */
class SingleWebFragment : BaseFragment<EmptyViewModel, FragmentSingleWebBinding>() {
    override val vbClass: KClass<FragmentSingleWebBinding> get() = FragmentSingleWebBinding::class
    override val vmClass: KClass<EmptyViewModel> get() = EmptyViewModel::class
    //是否发生了错误
    private var isError: Boolean = false
    private val args by navArgs<SingleWebFragmentArgs>()

    override fun initView(savedInstanceState: Bundle?) {
        val url = requireArguments().getString("url")  // 应该是 "https://baidu.com"
        val title = requireArguments().getString("title") // 应该是 "baidu"
        "navigate--->args-->$args,url:$url,title:$title".logd(TAG)
        mBinding.titleBar.loadGeneralTitleBar(args.title)
        //注册返回
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!doFragmentBack()) {
                        navigateUp()
                    }
                }
            })
    }

    override fun initData() {
        super.initData()
        settingConfig()
        setClient()
        args.url?.let { mBinding.jsBridgeView.loadUrl(it) }
    }

    override fun initListener() {
    }

    override fun createObserver() {

    }

    /**
     * 设置配置webView
     */
    private fun settingConfig() {
        val webSettings = mBinding.jsBridgeView.settings

        with(webSettings) {
            domStorageEnabled = true
            displayZoomControls = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            blockNetworkImage = false
            setGeolocationEnabled(true)
            setGeolocationDatabasePath(
                requireActivity().applicationContext.getDir("database", Context.MODE_PRIVATE).path
            )
            useWideViewPort = true
            loadWithOverviewMode = true
            defaultTextEncodingName = "UTF-8"
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

        }

        mBinding.jsBridgeView.setDefaultHandler(DefaultHandler())
    }

    /**
     * 设置webView监听Client
     */
    private fun setClient() {
        with(mBinding) {
            jsBridgeView.setWebViewClient(object :
                BridgeWebViewClient(jsBridgeView) {

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

            jsBridgeView.requestFocus()
            jsBridgeView.setWebChromeClient(object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
                    return true
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    showProgress(newProgress)
                }
            })
        }
    }

    fun showProgress(newProgress: Int) {
        mBinding.webProgress.progress = newProgress
        mBinding.webProgress.visibility = if (newProgress == 100) View.INVISIBLE else View.VISIBLE
    }

    /**
     * 加载错误业
     * @param code
     * @param error
     * @param throwable
     */
    fun loadErrorPage(code: Int, error: String? = null, throwable: Throwable? = null) {
        isError = true
        mBinding.jsBridgeView.loadUrl("about:blank") // 避免出现默认的错误界面
        launch {
            delay(10)
            mBinding.jsBridgeView.clearHistory()
            val html = "<html><body><h1>Error(code:$code,error:$error)</h1></body></html>"
            mBinding.jsBridgeView.loadDataWithBaseURL(
                null,
                html,
                "text/html",
                "utf-8",
                null
            )
        }
    }

    /**
     * 页面关闭回退拦截
     */
    fun doFragmentBack(): Boolean {
        //判断当前页面是否可以回退
        if (mBinding.jsBridgeView.canGoBack()) {
            mBinding.jsBridgeView.goBack()
            return true
        }
        return false
    }
}