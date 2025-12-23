package com.walisport.module.feedback.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.feedback.databinding.FragmentFeedbackMainBinding
import com.walisport.module.feedback.ui.viewmodel.FeedbackMainViewModel
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass


/**
 * 反馈详情页
 */
class FeedbackMainFragment : BaseFragment<FeedbackMainViewModel, FragmentFeedbackMainBinding>() {

    override val vbClass: KClass<FragmentFeedbackMainBinding> = FragmentFeedbackMainBinding::class
    override val vmClass: KClass<FeedbackMainViewModel> = FeedbackMainViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null //图片选择后返回h5
    private val requestImgCode = 101

//    private val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            result.data?.dataString?.let {
//                fileChooserCallback?.onReceiveValue(arrayOf(Uri.parse(it)))
//            }
//            fileChooserCallback = null;
//        } //监听图片选择

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            result?.let {
                fileChooserCallback?.onReceiveValue(arrayOf(it))
            }
            fileChooserCallback = null;
        }
    private var isWritePermissionGranted = false


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isWritePermissionGranted = isGranted
    }

    override fun initView(savedInstanceState: Bundle?) {
        launch {
            initTitleBar()
            initWebView()
            mBinding.webView.loadUrl(BizUrl.FEEDBACK.url)
        }
        checkReadPermission()
    }

    private fun checkReadPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { //vivo android9 手机不明崩溃，检查权限
            isWritePermissionGranted =
                requireContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!isWritePermissionGranted) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        } else {
            isWritePermissionGranted = true
        }
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
                    fileChooserCallback = filePathCallback
                    if (!isWritePermissionGranted) {
                        checkReadPermission()
                        fileChooserCallback?.onReceiveValue(null)
                        fileChooserCallback = null
                        return true
                    }
                    chooseImages()
                    return true
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    super.onPermissionRequest(request)
                    request?.grant(request.resources)
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }
            setBackgroundColor(arch.cayenne.lib.common.R.color.title_bg.getColor())
            setAttachedFragment(this@FeedbackMainFragment)
        }
    }

    private fun chooseImages() {
        try {
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                chooseImagesLegacy()
            }else{
            startForResult.launch("image/*")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "打开图片选择器失败 ${e.message}".loge(TAG)
            fileChooserCallback?.onReceiveValue(null)
            fileChooserCallback = null
        }
    }

    private fun chooseImagesLegacy() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            // 创建 Chooser，避免某些 ROM 的问题
            val chooser = Intent.createChooser(intent, "选择图片")
            startActivityForResult(chooser, requestImgCode)
        } catch (e: Exception) {
            e.printStackTrace()
            fileChooserCallback?.onReceiveValue(null)
            fileChooserCallback = null
        }
    }

    /**
     * 打开图片选择器
     * */
//    private fun chooseImages() {
//        try {
//            val mimeTypes = arrayOf("image/*")
//            val selectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                addCategory(Intent.CATEGORY_OPENABLE)
//                setType("image/*")
//                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                // 重要：这些标志可以加快处理
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                // 添加标志，告诉系统不要压缩
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
//                // 对于 Android 11+
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//                }
//            }
//            val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
//                putExtra(Intent.EXTRA_INTENT, selectionIntent)
//                // 添加标志避免系统处理
//                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            }
//            startActivityForResult(chooserIntent, requestImgCode)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "打开图片选择器失败 ${e.message}".loge(TAG)
//        }
//    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {//TODO 由于在android9报错，不知道具体原因，先使用老代码 针对vivo android9机型
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestImgCode && resultCode == android.app.Activity.RESULT_OK) {
            data?.data?.let {
                fileChooserCallback?.onReceiveValue(arrayOf(it))
            }
            fileChooserCallback = null;
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    private fun initTitleBar() {
        val type = arguments?.getString("type") ?: ""
        mBinding.root.touchBackPressed()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig, mBinding.root)
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