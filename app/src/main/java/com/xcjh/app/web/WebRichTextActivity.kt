package com.xcjh.app.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityWebRichTextBinding
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.loge


/**
 * 活动中心详情
 */
class WebRichTextActivity: BaseActivity<WebRichTextVm, ActivityWebRichTextBinding>() {
    private var id: String = ""//url
    private var title: String? = "" //会话标题


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()
        intent?.let {
            id = it.getStringExtra(Constants.WEB_URL).toString()
            title = it.getStringExtra(Constants.CHAT_TITLE).toString()
        }
        mDatabind.titleTop.tvTitle.text = title
        mViewModel.getActivityInfo(id)

    }

    private   var agentWeb: AgentWeb?=null
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWeb() {
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                mDatabind.agentWeb as ViewGroup,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            .closeIndicator()
            .setWebChromeClient(object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            })
            .setWebViewClient(object : com.just.agentweb.WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.e("TAG", "===-----onPageStarted------ $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e("TAG", "===-----onReceivedError------ " + error?.description.toString())
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    val javascript = "javascript:(function() { " +
                            "var imgs = document.getElementsByTagName('img');" +
                            "for(var i = 0; i < imgs.length; i++){" +
                            "   imgs[i].style.maxWidth = '100%';" +
                            "   imgs[i].style.height = 'auto';" +
                            "}" +
                            "})()"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        view!!.evaluateJavascript(javascript, null)
                    } else {
                        view!!.loadUrl(javascript)
                    }

                    Log.e("TAG", "===-----onPageFinished------ ")
                }
            })
            // .setWebView(binding.agentWeb)
            .createAgentWeb()
            .ready().get()

        //agentWeb.urlLoader.loadUrl("https://music.163.com/")
//        agentWeb.urlLoader.loadUrl(url)
//                val webView = agentWeb!!.webCreator.webView
//         webView.settings.loadWithOverviewMode = true
//        webView.settings.javaScriptEnabled=true
//        webView.settings.useWideViewPort = true
//        val css = "<style>img{max-width: 100%; height: auto;}</style>"
//        val htmlWithCss = css + mViewModel.events.value!!.content
//        webView.loadDataWithBaseURL(null, htmlWithCss, "text/html", "UTF-8", null)

//        val js = "<script type=\"text/javascript\">"+
//                "var imgs = document.getElementsByTagName('img');" + // 找到img标签
//                "for(var i = 0; i<imgs.length; i++){" +  // 逐个改变
//                "imgs[i].style.width = '100%';" +  // 宽度改为100%
//                "imgs[i].style.height = 'auto';" +
//                "}" +
//                "</script>"



        agentWeb!!.urlLoader.loadDataWithBaseURL(
            null,
             mViewModel.events.value!!.content,
            "text/html",
            "utf-8",
            null
        )


    }

    override fun onPause() {
        "onPause".loge("===")
        if(agentWeb!=null){
            agentWeb!!.webCreator.webView.onPause()
        }

        // agentWeb.webLifeCycle.onPause() //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        super.onPause()
    }

    override fun onResume() {
        "onResume".loge("===")
        if(agentWeb!=null){
            agentWeb!!.webLifeCycle.onResume()
        }

        super.onResume()
    }

    override fun onDestroy() {
        if(agentWeb!=null){
            agentWeb!!.webLifeCycle.onDestroy()
        }

        super.onDestroy()
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.events.observe(this){
            initWeb()
        }

    }
fun dd(){

}


}