package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.data.StatusBarEnum
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class
    private var onFunctionClick: (() -> Unit)? = null
    companion object {
        const val TAG = "DrawerContentFragment"
    }
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.post{
            StatusBarConfig.statusBarType =StatusBarEnum.TOP_UP
            setStatusBar(StatusBarConfig,mBinding.root)
        }
    }

    override fun initListener() {
        with(mBinding) {
            clNotificationHeader.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            llRecharge.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_topup_fragment.deeplink())
            }
            llDrawerTutorial.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_handicap_fragment.deeplink())
            }
            llDrawerSetting.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }

            llDrawerFeedback.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
            }
            llBetSlip.clickNoRepeat {
                onFunctionClick?.invoke()
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
        }
    }
    private fun navigatePage(uri: Uri) {
        onFunctionClick?.invoke()
        navigate(uri)
    }
    fun setOnFunctionClickListener(listener: () -> Unit) {
        onFunctionClick = listener
    }
    override fun createObserver() {

    }
}