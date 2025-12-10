package arch.cayenne.module.chat.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.lib.common.web.WLSWebViewClient
import arch.cayenne.module.chat.databinding.FragmentGameShareLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 19:50
 * @description:游戏注单分享
 */
class GameShareFragment:BaseFragment<BetShareViewModel,FragmentGameShareLayoutBinding>() {
    override val vbClass: KClass<FragmentGameShareLayoutBinding>
        get() = FragmentGameShareLayoutBinding::class
    override val vmClass: KClass<BetShareViewModel>
        get() = BetShareViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        //注册返回
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!doFragmentBack()) {
                        "js goBack ".logd("aaa")
//                        navigateUp()
                    }
                }
            })
    }

    override fun initData() {
        super.initData()
        setClient()
        mBinding.jsBridgeView.loadUrl("https://www.ve657.com/3n1-dev/orderDetail?settleId=s1")
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    /**
     * 设置webView监听Client
     */
    private fun setClient() {
        with(mBinding) {
            jsBridgeView.setWebViewClient(object :
                WLSWebViewClient(jsBridgeView) {

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