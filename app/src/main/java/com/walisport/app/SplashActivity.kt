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
import arch.cayenne.lib.common.BuildConfig as BuildConfigCom

class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    data class UserConfig(val name: String, val uid: Int, val token: String)

    private val users by lazy { GsonUtils.fromJson(BuildConfig.users,Array<UserConfig>::class.java)  }
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

    private var name:String? = null
    private var uid = 0
    private var token:String = ""

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
        initUidToken()
        "name:${name}, uid:$uid, token:$token".logd(TAG)
        mViewModel.saveUserData(uid, token)  //TODO 實作登入頁後就不需要這個了
        lifecycleScope.launch {
            delay(300)
            jumpToMainActivity()
        }
    }

    private fun initUidToken(){
        "manager.BUILD_TIME:${manager.getValue(UserDataKey.KEY_BUILD_TIME,"")},BuildConfig.BUILD_TIME:${BuildConfigCom.BUILD_TIME}".logd(TAG)
        //重新安装时，清理uid，token，否则会引发踢下线的bug
        if(manager.getValue(UserDataKey.KEY_BUILD_TIME,"") != BuildConfigCom.BUILD_TIME){
            manager.setKeyValue(UserDataKey.KEY_BUILD_TIME,BuildConfigCom.BUILD_TIME)
            manager.removeValueForKey(UserDataKey.KEY_UID)
            manager.removeValueForKey(UserDataKey.KEY_TOKEN)
        }
        uid = manager.getValue(UserDataKey.KEY_UID,-1).let {
            if(it == -1) pair.first else it
        }
        token = manager.getValue(UserDataKey.KEY_TOKEN,"").let {
            it.ifEmpty { pair.second }
        }
        name = users.find { it.uid== uid }?.name
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun jumpToMainActivity() {
        navigate(Intent(this, MainActivity::class.java), 0, 0)
        finish()
    }

}