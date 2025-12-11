package arch.cayenne.lib.common.web

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.biz.CommonBiz
import arch.cayenne.lib.common.utils.ext.requireActivity
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.DefaultHandler
import okhttp3.internal.userAgent

/**
 *
 * @date: 2025/10/14 15:15
 * @description:
 */
class WLSWebView : BridgeWebView {
    private var scrollListener: OnScrollChangedListener? = null
    private var attachedFragment: Fragment? = null

    interface OnScrollChangedListener{
        fun onScrollChanged(scrollX: Int, scrollY: Int):Unit
    }
    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        initWebSettings()
        initJsBridge()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollListener?.onScrollChanged(l, t)
    }

    fun setOnScrollChangedListener(listener: OnScrollChangedListener?) {
        scrollListener = listener
    }
    fun removeScrollChangedListener() {
        scrollListener = null
    }

    fun setAttachedFragment(fragment: Fragment){
        this.attachedFragment = fragment
    }

    private fun initWebSettings() {
        val webSettings = settings

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
            // 获取默认 User-Agent
            val defaultUA = settings.userAgentString // 在默认 UA 后添加自定义字符串
            val customUA = "$defaultUA 3N1/Android"  // 示例

            settings.userAgentString = customUA

        }

        setDefaultHandler(DefaultHandler())
    }

    private fun initJsBridge(){
        addJavascriptInterface(WLSJsInterface(this), "AndroidNative")
    }

    fun jump2CustomerService(){
        attachedFragment?.let {
//            "jump2CustomerService called".logd("WLSWebView")
            CommonBiz.jump2CustomerService(it)
        }
    }
}
