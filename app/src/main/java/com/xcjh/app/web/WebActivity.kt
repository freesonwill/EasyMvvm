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
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.WebChromeClient
import com.xcjh.app.R
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
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
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
                /*    if(type!=0){
                        view!!.evaluateJavascript("javascript:(function() { " +
                                "var elements = document.querySelectorAll(':not(h4)');" +
                                "for(var i = 0; i < elements.length; i++) {" +
                                "   elements[i].style.color = '#37373D';" +
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
                                "var elements = document.querySelectorAll(':not(h4)');" +
                                "for(var i = 0; i < elements.length; i++) {" +
                                "   elements[i].style.color = '#37373D';" +
                                "}" +
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
                    }*/


                }
            })
            // .setWebView(binding.agentWeb)
            .createAgentWeb()
            .ready().get()
        val webView = agentWeb.webCreator.webView
        val webSettings: WebSettings = webView.settings
        // 设置加载中背景色为灰色
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.c_ffffff))
        // 禁止缩放
//        webSettings.setSupportZoom(false)//设置可以支持缩放
//        webSettings.builtInZoomControls = false//设置出现缩放工具
//        webSettings.useWideViewPort = false//扩大比例的缩放
//        webSettings.displayZoomControls = false//隐藏缩放控件

         if(type==0){
            agentWeb.urlLoader.loadUrl(url)
        }
            //。如果触摸事件的 pointerCount（触摸点数）大于 1，表示用户正在进行多指操作，这时我们返回 true 来拦截触摸事件
        webView.setOnTouchListener { _, event ->
            if (event.pointerCount > 1) {
                // 多指触摸时，禁止缩放手势
                return@setOnTouchListener true
            }
            false
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
        val js = "<script type=\"text/javascript\">\n" +
                "var elements = document.querySelectorAll(':not(h4)');\n" +
                "for(var i = 0; i < elements.length; i++) {\n" +
                "   elements[i].style.color = '#37373D';" +
                "}\n" +
                "    var imgs = document.querySelectorAll('img');\n" +
                "    for (var i = 0; i < imgs.length; i++) {\n" +
                "        const dataSrc = imgs[i].getAttribute('data-src');\n" +
                "\n" +
                "        if (dataSrc) {\n" +
                "            imgs[i].setAttribute('src', dataSrc)\n" +
                "        }\n" +
                "        if(i>0){\n" +
                "            imgs[i].style.maxWidth = '100%';\n" +
                "            imgs[i].style.height = 'auto';\n" +
                "            imgs[i].style.borderRadius  = '4px';\n" +
                "        }\n" +
                "    }\n" +
                "</script>"
            //imgs[i].style.width = '100%'
//        imgs[i].style.maxWidth = '100%'
        //获取到网页详情 新闻列表
        mViewModel.newsBeanValue.observe(this){
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var dateTimeString:String=""
            var title:String=""
            title="<p style=\" color: #37373D; font-size: 18px; font-weight: bold; margin: 0;\">${it.title}</p>"
            if(it.publishTime.isNotEmpty()){
                dateTimeString= sdf.format(Date(it.publishTime.toLong()))
                title += "<div style=\" display: flex; align-items: center; margin: 0; padding: 0; \"> " +
                        "<div style=\"display: flex; align-items: center;\">" +
                        "<img src=\"file:///android_asset/football.png\"  alt=\"Example\" width=\"20px\" height=\"20px\"  >" +
                        " <h4 style=\"font-size: 12px; color: #575762; font-weight: normal; margin-left: 4px;\">${resources.getString(R.string.my_app_name)}</h4>" +
                        "</div>"+
                        "<h4 style=\" color: #94999F; font-size: 11px; margin-left: auto; font-weight: normal; padding: 0; \">${dateTimeString} ${resources.getString(R.string.release_txt_name)}</h4></div>"
            }
            title += it.content
            title="<body style=\"background-color: white;  padding: 0px; margin-left: 12px; margin-right: 12px;\">${title}</body>"+js
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
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var dateTimeString:String=""
            var title:String=""
            title="<p style=\" color: #37373D; font-size: 18px; font-weight: bold;\">${it.title}</p>"
            if(it.updateTime .isNotEmpty()){
                dateTimeString= sdf.format(Date(it.updateTime.toLong()))
                title +=   "<div style=\" display: flex; align-items: center;  margin: 0; padding: 0; \"> " +
                        "<div style=\"display: flex; align-items: center;\">" +
                        "<img src=\"file:///android_asset/football.png\"  alt=\"Example\" width=\"20px\" height=\"2px\"  >" +
                        " <h4 style=\"font-size: 12px; color: #575762; font-weight: normal; margin-left: 4px;\">${resources.getString(R.string.my_app_name)}</h4>" +
                        "</div>"+
                        "<h5 style=\" color: #94999F; font-size: 11px; margin-left: auto; font-weight: normal;\">${dateTimeString} ${resources.getString(R.string.release_txt_name)}</h4></div>"
            }
            title += it.content
            title="<body style=\"background-color: white; padding: 0px; margin-left: 12px; margin-right: 12px;\">${title}</body>"+js
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