package arch.cayenne.lib.common.web

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebSettings
import arch.cayenne.lib.common.utils.ext.requireActivity
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.DefaultHandler

/**
 *
 * @date: 2025/10/14 15:15
 * @description:
 */
class WLSWebView : BridgeWebView {

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

        }

        setDefaultHandler(DefaultHandler())
    }

    private fun initJsBridge(){
        addJavascriptInterface(WLSJsInterface(this), "wls")
    }
}