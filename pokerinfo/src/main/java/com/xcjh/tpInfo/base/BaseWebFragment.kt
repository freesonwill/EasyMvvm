package com.xcjh.tpInfo.base

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.viewbinding.ViewBinding
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.xcjh.base_lib.base.BaseViewModel


/**
 *  纯网页加载
 */
abstract class BaseWebFragment<VM : BaseViewModel, VB : ViewBinding> : BaseFragment<VM, VB>() {

    protected lateinit var mAgentWeb: AgentWeb
    protected abstract val agentWebParent: ViewGroup

    override fun initView(savedInstanceState: Bundle?) {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                agentWebParent,
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            )
            .closeIndicator()
            // .setWebView(webView)
            //.setWebView(NestedScrollAgentWebView(context))
            .setWebChromeClient(object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            })
            .setWebViewClient(object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.e("TAG", "--------onPageStarted------ $url")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e("TAG", "--------onReceivedError------ ")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.e("TAG", "--------onPageFinished------ ")
                }
            })
            // .setWebView(binding.agentWeb)
            .createAgentWeb()
            .ready()
            .get()
        // 去掉滚动条
        mAgentWeb.webCreator.webView.scrollBarSize = 0
        //  取消WebView中滚动或拖动到顶部、底部时的阴影
        mAgentWeb.webCreator.webView.overScrollMode = View.OVER_SCROLL_NEVER
        mAgentWeb.agentWebSettings.webSettings.javaScriptEnabled = true
        mAgentWeb.agentWebSettings.webSettings.allowFileAccess = true
        mAgentWeb.agentWebSettings.webSettings.allowFileAccessFromFileURLs = true

    }

    override fun onPause() {
        mAgentWeb.webCreator?.webView?.onPause()
        super.onPause()

    }

    override fun onResume() {
        mAgentWeb.webLifeCycle?.onResume()
        super.onResume()
    }


    override fun onDestroy() {
        mAgentWeb.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

}