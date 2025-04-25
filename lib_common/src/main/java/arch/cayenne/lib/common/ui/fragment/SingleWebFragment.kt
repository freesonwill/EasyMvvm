package arch.cayenne.lib.common.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.FragmentSingleWebBinding
import arch.cayenne.lib.common.utils.NetworkUtils
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
        val binding = mBinding
        binding.jsBridgeView.getSettings().domStorageEnabled = true
        binding.jsBridgeView.setDefaultHandler(DefaultHandler())
        binding.jsBridgeView.getSettings().displayZoomControls = true
        binding.jsBridgeView.getSettings().mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        //打开缓存
        binding.jsBridgeView.getSettings().databaseEnabled = true
        // 开启
        //binding.jsBridgeView.getSettings().setAppCacheEnabled(true);
        // 设置缓存模式，非常重要，决定了webview缓存资源的方式
        binding.jsBridgeView.getSettings().cacheMode = WebSettings.LOAD_DEFAULT
        binding.jsBridgeView.getSettings().mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.jsBridgeView.getSettings().mixedContentMode =
                WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        binding.jsBridgeView.getSettings().blockNetworkImage = false //解决图片不显示
        //解决webview不能使用h5定位问题
        val dir: String =
            requireActivity().applicationContext.getDir("database", Context.MODE_PRIVATE).path
        binding.jsBridgeView.getSettings().setGeolocationDatabasePath(dir)
        binding.jsBridgeView.getSettings().setGeolocationEnabled(true) // 启用地理

        val webSettings: WebSettings = binding.jsBridgeView.getSettings()
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.allowContentAccess = true // 是否可访问Content Provider的资源，默认值 true
        webSettings.allowFileAccess = true // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.allowFileAccessFromFileURLs = true
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.allowUniversalAccessFromFileURLs = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.blockNetworkImage = false //解决图片不显示
    }

    /**
     * 设置webView监听Client
     */
    private fun setClient() {
        val binding = mBinding
        binding.jsBridgeView.setWebViewClient(object :
            BridgeWebViewClient(binding.jsBridgeView) {

            override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message) {
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

        binding.jsBridgeView.requestFocus()
        binding.jsBridgeView.setWebChromeClient(object : WebChromeClient() {
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