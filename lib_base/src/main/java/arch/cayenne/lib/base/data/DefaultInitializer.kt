package arch.cayenne.lib.base.data

import androidx.annotation.CallSuper
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer

/**
 * @author: zhangsan
 * @date: 2025/4/18 14:43
 * @description: 默认的DefaultInitializer，默认先加载ApplicationModuleInitializer
 */

interface DefaultInitializer<T> : Initializer<T> {
    /**
     * 想要复写，请return super.dependencies() + listOf(...)
     */
    @CallSuper
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

}

