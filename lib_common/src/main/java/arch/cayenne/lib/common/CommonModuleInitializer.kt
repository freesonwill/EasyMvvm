package arch.cayenne.lib.common

import android.content.Context
import android.os.Build
import android.os.StrictMode
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.data.repo.CommonRepository
import arch.cayenne.lib.common.data.repo.ReserveDialogRepository
import arch.cayenne.lib.common.ui.viewmodel.ConnectFailedViewModel
import arch.cayenne.lib.common.ui.viewmodel.ReserveDialogViewModel
import arch.cayenne.lib.common.ui.viewmodel.ShareViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

class CommonModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        MMKV.initialize(context)
        loadKoinModules(moduleList)
        enableStrictMode()
        clearUserDataManager()
        return TAG
    }

    /**
     * 每次编译清除UserDataManager，防止代码设置的不生效
     */
    private fun clearUserDataManager(){
        val manager = getKoin().get<UserDataManager>()
        if(manager.getValue(UserDataKey.KEY_BUILD_TIME,"") != BuildConfig.BUILD_TIME){
            arrayOf(
                UserDataKey.KEY_UID,
                UserDataKey.KEY_TOKEN,
                UserDataKey.KEY_ANIM_ROUTE,
                UserDataKey.KEY_ANIM_ZOOM,
                UserDataKey.KEY_ANIM_POPUP,
                UserDataKey.KEY_ANIM_DRAWER,
                UserDataKey.KEY_ANIM_SCROLLBAR,
            ).forEach { manager.removeValueForKey(it) }
            manager.setKeyValue(UserDataKey.KEY_BUILD_TIME, BuildConfig.BUILD_TIME)
        }
    }
    private val moduleList: List<Module> = listOf(module {
        factory { (scope: CoroutineScope) -> CommonRepository(scope, get(), get(), get(), get()) }
        single { UserDataManager() }
        viewModelOf(::ConnectFailedViewModel)
        viewModelOf(::ReserveDialogViewModel)
        viewModelOf(::ShareViewModel)

        factoryOf(::CommonRepository)
        factoryOf(::BalanceRepository)
        factoryOf(::ReserveDialogRepository)
    })

    /**
     * 开启严格模式(检测非法操作：UI线程耗时操作)
     */
    private fun enableStrictMode() {
        if (!BuildConfig.DEBUG) return // 只在调试模式开启严格模式
        "enableStrictMode MANUFACTURER:${Build.MANUFACTURER}".logd(TAG)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()   // 检测网络操作
                //.detectDiskReads() // 检测磁盘读取，File.exists()这个会触发警告，暂时关闭
                //.apply { if(!arrayOf("OPPO","vivo","Google","OnePlus").contains(Build.MANUFACTURER)) detectDiskWrites() } // 检测磁盘写入
                .detectCustomSlowCalls()
                .penaltyLog() // 日志输出
                .penaltyDeath() // 崩溃
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll() // 检测所有 VM 问题（如泄漏）
                .penaltyLog()
                .build()
        )
    }
}
