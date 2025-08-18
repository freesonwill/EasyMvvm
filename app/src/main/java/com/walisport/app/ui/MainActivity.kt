package com.walisport.app.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.launch
import arch.cayenne.lib.common.data.constants.LoginEnum
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.common.ui.dialog.CommonDialog
import arch.cayenne.lib.common.ui.view.BetResultToastView
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.common.utils.helper.toastAnim.ToastSlideAnimation
import arch.cayenne.lib.common.utils.helper.toastGesture.ToastSlideGesture
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.module.bet.ui.fragment.BetSheetFragment
import arch.cayenne.module.bet.ui.fragment.FloatingButtonFragment
import arch.cayenne.module.bet.viewmodel.FloatingButtonControlViewModel
import arch.cayenne.module.betslip.ui.fragment.HomeBetSlipFragment
import com.walisport.app.R
import com.walisport.app.ui.viewmodel.MainViewModel
import com.walisport.module.message.ui.fragment.AppNotifyFragment
import com.walisport.module.message.ui.view.AppNotifyToastView
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass
import kotlin.system.exitProcess

class MainActivity : BaseNavActivity<MainViewModel>() {

    override fun navigationID(): Int = R.navigation.nav_graph_app
    override val vmClass: KClass<MainViewModel> = MainViewModel::class
    private val fabControlViewModel: FloatingButtonControlViewModel by viewModel()
    private val fabFragment: FloatingButtonFragment by lazy {
        FloatingButtonFragment.newInstance()
    }
    private val notifyFragment: AppNotifyFragment by lazy {
        AppNotifyFragment.newInstance()
    }

    override fun initView(savedInstanceState: Bundle?) {
        val metrics = resources.displayMetrics
        DensityInfo.density = metrics.density
        DensityInfo.scaledDensity = metrics.scaledDensity
        super.initView(savedInstanceState)
        createBetSheet()
        fabFragment.show(this)
        notifyFragment.show(this)
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.betResultListener.observe(this) {
            if (BetResultToastView.canShowToast(this)) {
                showBetResultToast(it)
            }
        }
        mViewModel.appNotifyListener.observe(this) {
            //notifyFragment.sendNotifyMsg(it)
            if (AppNotifyToastView.canShowToast(this)) {
                val toast = AppNotifyToastView(this@MainActivity)
                val statusHeight = ViewUtils.getStatusBarHeight(this)
                toast.sendNotifyMsg(it)
                showToast(toast, ToastSlideAnimation(statusHeight), ToastSlideGesture())
            }
        }
        fabControlViewModel.isShowButtonListener.observe(this) {
            if (it) {
                fabFragment.show()
            } else {
                fabFragment.hide()
            }
        }
        fabControlViewModel.onClickAnimationListener.observe(this) { (x, y) ->
            fabFragment.showDotAnimation(x, y)
        }
        mViewModel.aberrantNotify.observe(this) { code ->
            if (code == 1) {
                CommonDialog.newInstance(
                    title = getString(R.string.multiple_logins_title),
                    message = getString(R.string.multiple_logins_message),
                    okText = getString(R.string.single_confirm)
                ).apply {
                    setOnOkClickListener {
                        exitProcess(0)
                    }
                }.show(supportFragmentManager)
            } else if (code == 2) {
                //TODO go to login page
            }
        }
        mViewModel.loginResult.observe(this) { login ->
            if (login == null) return@observe
            when(login) {
                LoginEnum.SUCCESSFUL -> Unit
                LoginEnum.NOT_SUCCESSFUL -> {
                    //TODO 跳到登入頁
                }
                LoginEnum.API_FAILURE -> Unit
            }
        }
    }

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        return StatusBarConfig
    }

    private fun showBetResultToast(data: List<BetResultLiteBean>) {
        val toast = BetResultToastView(this@MainActivity)
        val statusHeight = ViewUtils.getStatusBarHeight(this)
        toast.setResult(data)
        showToast(toast, ToastSlideAnimation(statusHeight), ToastSlideGesture().apply {
            setOnClickListener {
                showBetSlipPage()
            }
        })
    }

    private fun showBetSlipPage() {
        val tag = HomeBetSlipFragment.TAG
        val fragmentManager = supportFragmentManager

        val betSlipFragment = fragmentManager.findFragmentByTag(tag)
        if (betSlipFragment != null) {
            return
        }

        val navHostFragment = fragmentManager.findFragmentById(mBinding.navHost.id)
        val curFragment =
            navHostFragment?.childFragmentManager?.fragments?.firstOrNull { it.isVisible }
        if (curFragment is HomeBetSlipFragment) {
            return
        }

        val dialogFragment = fragmentManager.fragments.filterIsInstance<DialogFragment>()
        if (dialogFragment.isNotEmpty()) {
            dialogFragment.forEach {
                it.dismiss()
            }
        }
        val transaction = fragmentManager.beginTransaction()
            .setCustomAnimations(
                arch.cayenne.lib.common.R.anim.slide_in_right,
                arch.cayenne.lib.common.R.anim.no_anim,
                arch.cayenne.lib.common.R.anim.no_anim,
                arch.cayenne.lib.common.R.anim.slide_out_right
            )

        val currentFragment = fragmentManager.findFragmentById(mBinding.navHost.id)
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        transaction.add(mBinding.navHost.id, HomeBetSlipFragment(), tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    private fun createBetSheet() {
        launch(Lifecycle.State.RESUMED, context = Dispatchers.IO) {
            mBinding.root.postDelayed( {
                BetSheetFragment.create(this@MainActivity)
            }, 500L)
        }
    }

}