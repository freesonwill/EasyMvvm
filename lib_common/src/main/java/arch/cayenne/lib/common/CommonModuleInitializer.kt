package arch.cayenne.lib.common

import android.content.Context
import android.os.Build
import android.os.StrictMode
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.data.repo.CommonRepository
import arch.cayenne.lib.common.ui.viewmodel.ConnectFailedViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class CommonModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        MMKV.initialize(context)
        loadKoinModules(moduleList)
        enableStrictMode()
        return TAG
    }

    private val moduleList: List<Module> = listOf(module {
        factory { (scope: CoroutineScope) -> CommonRepository(scope, get(), get(), get(), get()) }
        factoryOf(::BalanceRepository)
        single { UserDataManager() }
        viewModel { ConnectFailedViewModel() }
    })

    /**
     * 开启严格模式(检测非法操作：UI线程耗时操作)
     */
    private fun enableStrictMode() {
        if (!BuildConfig.DEBUG) return // 只在调试模式开启严格模式
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectNetwork()   // 检测网络操作
                //.detectDiskReads() // 检测磁盘读取，File.exists()这个会触发警告，暂时关闭
                .apply { if(!arrayOf("OPPO").contains(Build.MANUFACTURER)) detectDiskWrites() } // 检测磁盘写入
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
