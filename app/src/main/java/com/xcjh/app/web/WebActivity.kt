package com.xcjh.app.web

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.WebChromeClient
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityWebBinding
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.loge
import java.text.SimpleDateFormat
import java.util.*


class WebActivity : BaseActivity<MainVm, ActivityWebBinding>() {
    private val model: MainVm by viewModels()
    private var url: String? = ""//url
    private var title: String? = "" //会话标题
    private var type:Int=0//0是普通url   1是一段html新闻列表详情  2活动中心详情
    private var urlID:String=""
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.titleTop.root)
            .init()
        intent?.let {
            url = it.getStringExtra(Constants.WEB_URL).toString()
            title = it.getStringExtra(Constants.CHAT_TITLE).toString()
            type=it.getIntExtra(Constants.WEB_VIEW_TYPE,0)
            urlID= it.getStringExtra(Constants.WEB_VIEW_ID).toString()
        }
        mDatabind.titleTop.tvTitle.text = title
        initWeb()
        if(type==1){
            mViewModel.getNewsInfo(urlID)
        }else if(type==2){
            mViewModel.getActivityInfo(urlID)
        }


    }

    private lateinit var agentWeb: AgentWeb
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
                    Log.e("TAG", "===-----onPageFinished------ ")
                    if(type!=0){
                        view!!.evaluateJavascript("javascript:(function() { " +
                                "var elements = document.querySelectorAll(':not(h4)');" +
                                "for(var i = 0; i < elements.length; i++) {" +
                                "   elements[i].style.color = 'white';" +
                                "}" +
                                "})()", null)

//                        val javascript = "javascript:(function() { " +
//                                "var imgs = document.getElementsByTagName('img');" +
//                                "for(var i = 0; i < imgs.length; i++){" +
//                                "   imgs[i].style.maxWidth = '100%';" +
//                                "   imgs[i].style.height = 'auto';" +
//                                "}" +
//                                "})()"
                        val javascript = "javascript:(function() { " +
                                "var imgs = document.querySelectorAll('*');" +
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
                    }


                }
            })
            // .setWebView(binding.agentWeb)
            .createAgentWeb()
            .ready().get()

        if(type==0){
            agentWeb.urlLoader.loadUrl(url)
        }

    }

    override fun onPause() {
        "onPause".loge("===")
        agentWeb.webCreator.webView.onPause()
        // agentWeb.webLifeCycle.onPause() //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
        super.onPause()
    }

    override fun onResume() {
        "onResume".loge("===")
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun createObserver() {
        super.createObserver()
        //获取到网页详情
        mViewModel.newsBeanValue.observe(this){
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var dateTimeString:String=""
            var title:String=""
            title="<p style=\" color: white; font-size: 20px; font-weight: bold;\">${it.title}</p>"
            if(it.publishTime.isNotEmpty()){
                dateTimeString= sdf.format(Date(it.publishTime.toLong()))
                title += "<h4 style=\" color: #8A91A0; font-size: 14px;\">${dateTimeString}</h4>"
            }
            title += it.content
            title="<body style=\"background-color: #07061D; margin: 0; padding: 24px;\">${title}</body>"
            agentWeb.urlLoader.loadDataWithBaseURL(
                null,
                title,
                "text/html",
                "utf-8",
                null
            )
        }

        //获取到活动网页详情
        mViewModel.events.observe(this){
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var dateTimeString:String=""
            var title:String=""
            title="<p style=\" color: white; font-size: 20px; font-weight: bold;\">${it.title}</p>"
            if(it.updateTime .isNotEmpty()){
                dateTimeString= sdf.format(Date(it.updateTime.toLong()))
                title += "<h4 style=\" color: #8A91A0; font-size: 14px;\">${dateTimeString}</h4>"
            }
            title += it.content
            title="<body style=\"background-color: #07061D; margin: 0; padding: 24px;\">${title}</body>"
            agentWeb.urlLoader.loadDataWithBaseURL(
                null,
                title,
                "text/html",
                "utf-8",
                null
            )
        }
    }
}