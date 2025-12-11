package arch.cayenne.module.chat.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.annotation.MainThread
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import arch.cayenne.lib.common.data.model.JSResponseData
import arch.cayenne.module.chat.databinding.FragmentGameShareLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import arch.cayenne.module.chat.ui.viewmodel.GameBetShareViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 19:50
 * @description:游戏注单分享
 */
class GameBetShareFragment : BaseFragment<GameBetShareViewModel, FragmentGameShareLayoutBinding>() {
    override val vbClass: KClass<FragmentGameShareLayoutBinding>
        get() = FragmentGameShareLayoutBinding::class
    override val vmClass: KClass<GameBetShareViewModel>
        get() = GameBetShareViewModel::class
    private val shareViewModel:BetShareViewModel by sharedViewModel<BetShareViewModel,BetShareDialogFragment>()
    val gson = Gson()
    override fun initView(savedInstanceState: Bundle?) {
        initTitleBar()
        initWebView()
        mBinding.webView.loadUrl(BizUrl.GAME_BET_SHARE.url)
    }

    override fun initListener() {

        mBinding.webView.addJsBridgeListen {
           lifecycleScope.launch(Dispatchers.Main) {
               "jsBridge ${it.type}".logd("aaa")
               when(it.type){
                   "back" ->{
                       shareViewModel.closeDialog()
                   }
                   "expand" ->{
                       "打开expand".logd("aaa")
                       shareViewModel.expandDialog()
                   }
                   "openPage" ->{

                   }
               }
           }
        }
    }

    override suspend fun createObserver() {
    }

    private fun initTitleBar() {
//        val type = arguments?.getString("type") ?: ""
//        mBinding.root.touchBackPressed()
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
        }
    }

    override fun onStart() {
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
}