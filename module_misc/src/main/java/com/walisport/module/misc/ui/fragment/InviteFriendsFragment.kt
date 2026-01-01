package com.walisport.module.misc.ui.fragment

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.data.model.JSResponseData
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.common.web.WLSWebViewClient
import com.walisport.module.misc.databinding.FragmentInviteFriendsBinding
import com.walisport.module.misc.ui.viewmodel.InviteFriendsViewModel
import com.walisport.module.misc.util.GallerySaveManager
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

/**
 * 邀请好友页面， 内容由Web提供

 */
class InviteFriendsFragment : BaseFragment<InviteFriendsViewModel, FragmentInviteFriendsBinding>() {

    override val vbClass: KClass<FragmentInviteFriendsBinding> = FragmentInviteFriendsBinding::class
    override val vmClass: KClass<InviteFriendsViewModel> = InviteFriendsViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)
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
            mBinding.webView.loadUrl(BizUrl.INVITE.url)
        }
        initPermission()
    }

    private fun initPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { //andorid 10以下需要手动申请权限
            if (!GallerySaveManager.hasStoragePermission(requireContext())) {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
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
                    return true
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }
            }
            setBackgroundColor(arch.cayenne.lib.common.R.color.title_bg.getColor())
            setAttachedFragment(this@InviteFriendsFragment)

            addJsBridgeListen {
                if (it.type == "saveImg") {
                    checkPermission(it)
                }
            }
        }
    }

    private fun checkPermission(data: JSResponseData) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//·andorid 10以下需要手动申请权限
            isWritePermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (isWritePermissionGranted) {
                data.params.file?.let { baseData ->
                    mViewModel.saveImage(baseData, requireContext())
                    showToast("图片已保存到相册")
                }
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else { //android 10及以上使用MediaStore保存图片不需要申请权限
            data.params.file?.let { baseData ->
                mViewModel.saveImage(baseData, requireContext())
                showToast("图片已保存到相册")
            }
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