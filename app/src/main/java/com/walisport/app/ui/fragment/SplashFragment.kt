package com.walisport.app.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.blankj.utilcode.util.GsonUtils
import com.walisport.app.BuildConfig
import com.walisport.app.R
import com.walisport.app.databinding.FragmentSplashBinding
import com.walisport.app.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * @date: 2025/9/15 15:41
 * @description:
 */
class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>() {
    data class UserConfig(val name: String, val uid: Int, val token: String)

    private val users by lazy {
        GsonUtils.fromJson(
            BuildConfig.users,
            Array<UserConfig>::class.java
        )
    }
    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        users.filter { it.name.startsWith("qatest") }
            .map { it.uid to it.token }
            .let { it[Random.nextInt(it.size)] }
        //Pair(55468822, "NTU0Njg4MjJfMTc1MTM1NTAxMDI3MDppUjNheWVyczZ4S3dyVEFX") //固定uid,token时放开
    } else {
        Pair(0, "")
    }
    private val manager: UserDataManager by inject(UserDataManager::class.java)

    private var name: String? = null
    private var uid = 0
    private var token: String = ""

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
        val splashScreen = requireActivity().installSplashScreen()
        //splashScreen.setKeepOnScreenCondition { keep }
        lifecycleScope.launch {
            delay(300)
            keep = false
            jumpToMainActivity()
        }
    }

    override fun initData() {
        super.initData()
        initUidToken()
        "name:${name}, uid:$uid, token:$token".logd(TAG)
        mViewModel.saveUserData(uid, token)  //TODO 實作登入頁後就不需要這個了
    }

    private fun initUidToken() {
        "manager.BUILD_TIME:${
            manager.getValue(
                UserDataKey.KEY_BUILD_TIME,
                ""
            )
        },BuildConfig.BUILD_TIME:${arch.cayenne.lib.common.BuildConfig.BUILD_TIME}".logd(TAG)
        uid = manager.getValue(UserDataKey.KEY_UID, -1).let {
            if (it == -1) pair.first else it
        }
        token = manager.getValue(UserDataKey.KEY_TOKEN, "").let {
            it.ifEmpty { pair.second }
        }
        name = users.find { it.uid == uid }?.name
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun jumpToMainActivity() {
        navigate("walisport://module_home/NewHomeFragment".deeplink(),
            NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, inclusive = true) // 清空栈顶到导航图的起点
                .setLaunchSingleTop(true)                     // 避免重复实例化相同目的地
                .build(),
            enterAnim = null,
            exitAnim = null

        )
    }
}