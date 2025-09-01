package com.walisport.app

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.blankj.utilcode.util.GsonUtils
import com.walisport.app.databinding.ActivitySplashBinding
import com.walisport.app.ui.MainActivity
import com.walisport.app.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.random.Random
import kotlin.reflect.KClass

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    data class UserConfig(val name: String, val uid: String, val token: String)

    private val pair: Pair<Int, String> = if (BuildConfig.BUILD_TYPE == "debug") {
        Pair(BuildConfig.uid, BuildConfig.token)
    } else if (BuildConfig.BUILD_TYPE != "release") {
        GsonUtils.fromJson(BuildConfig.users,Array<UserConfig>::class.java)
            .filter { it.name.startsWith("qatest") }
            .map { it.uid.toInt() to it.token }
            .let { it[Random.nextInt(it.size)] }
    } else {
        Pair(0, "")
    }
    private val manager: UserDataManager by inject(UserDataManager::class.java)

    private val uid = manager.getValue(UserDataKey.KEY_UID,-1).let {
        if(it == -1) pair.first else it
    }
    private val token = manager.getValue(UserDataKey.KEY_TOKEN,"").let {
        it.ifEmpty { pair.second }
    }

    override val vbClass: KClass<ActivitySplashBinding> = ActivitySplashBinding::class
    override val vmClass: KClass<SplashViewModel> = SplashViewModel::class

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarDarkFont = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(
            autoPadding = true,
            noPaddingViewIds = listOf(mBinding.splashBg.id)
        )
        return StatusBarConfig
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.loadMyAppSkin()
    }

    override fun initData() {
        super.initData()
        "uid:$uid, token:$token".logd(TAG)
        mViewModel.saveUserData(uid, token)  //TODO 實作登入頁後就不需要這個了
        lifecycleScope.launch {
            delay(100)
            jumpToMainActivity()
        }

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun jumpToMainActivity() {
        navigate(Intent(this, MainActivity::class.java))
        finish()
    }

}