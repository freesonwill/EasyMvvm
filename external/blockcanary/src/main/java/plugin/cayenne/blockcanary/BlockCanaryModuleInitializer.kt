package plugin.cayenne.blockcanary

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import blockcanary.BlockCanary
import blockcanary.BlockCanaryConfig

/**
 * @date: 2025/7/15 23:31
 * @description: 卡顿检测库初始化
 */
class BlockCanaryModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        val application = context as android.app.Application
        // 一般在 application onCreate阶段配置
        val blockCanaryConfig = BlockCanaryConfig
            .newBuilder()
            .blockThresholdTime(500) // 阻塞时间超过500毫秒就会被记录
            .blockMaxThresholdTime(5000)
            .build()
        BlockCanary.install(application, blockCanaryConfig)
        "BlockCanary install....".logd(TAG)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        super.dependencies()
        return emptyList()
    }
}