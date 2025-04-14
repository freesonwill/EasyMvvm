package com.walisport.module.home

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:27
 * @description:
 */
class ModuleInitializer: Initializer<Unit> {

    override fun create(context: Context) {

    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
       return listOf(ApplicationModuleInitializer::class.java)
    }
}