package com.walisport.app.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.app.R
import com.walisport.app.databinding.FragmentSplashBinding
import com.walisport.app.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.reflect.KClass

/**
 * @date: 2025/9/15 15:41
 * @description:
 */
class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>() {
    data class UserConfig(val name: String, val uid: Int, val token: String)


    private val manager: UserDataManager by inject(UserDataManager::class.java)

    override val vbClass: KClass<FragmentSplashBinding> = FragmentSplashBinding::class
    override val vmClass: KClass<SplashViewModel> = SplashViewModel::class

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.root)
        super.onStart()
    }

    private var keep: Boolean = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*val splashScreen = requireActivity().installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashViewProvider: SplashScreenViewProvider ->
            splashViewProvider.remove()
        }*/
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.loadMyAppSkin()
        requireActivity().installSplashScreen()
        //splashScreen.setKeepOnScreenCondition { keep }
        lifecycleScope.launch {
            delay(300)
            keep = false
            jumpToMainFragment()
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun jumpToMainFragment() {
        navigate("walisport://module_home/MainFragment".deeplink(),
            NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, inclusive = true) // 清空栈顶到导航图的起点
                .setLaunchSingleTop(true)                     // 避免重复实例化相同目的地
                .build(),
            enterAnim = null,
            exitAnim = null
        )
    }
}