package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import arch.cayenne.lib.base.data.StatusBarEnum
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class

    companion object {
        const val TAG = "DrawerContentFragment"
    }
    override fun initView(savedInstanceState: Bundle?) {

    }


    override fun onStop() {
        StatusBarConfig.statusBarType =StatusBarEnum.DEFAULT
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onStop()
    }
    override fun onStart() {
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType =StatusBarEnum.TOP_UP
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onStart()
    }

    override fun initListener() {
        with(mBinding) {
            ivArrowRight.clickNoRepeat {
                navigate(Uri.parse("walisport://module_message/messageFragment"))
            }
            llDrawerTutorial.clickNoRepeat {
                navigate(Uri.parse("walisport://module_handicap/HandicapFragment"))
            }
            llDrawerSetting.clickNoRepeat {
                navigate(Uri.parse("walisport://module_setting/settingFragment"))
            }

            llDrawerFeedback.clickNoRepeat {
                navigate(Uri.parse("walisport://module_feedback/feedbackFragment"))
            }
        }
    }

    override fun createObserver() {

    }
}