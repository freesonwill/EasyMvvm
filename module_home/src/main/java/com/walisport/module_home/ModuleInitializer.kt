package com.walisport.module_home

import android.content.Context
import androidx.startup.Initializer

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:27
 * @description:
 */
class ModuleInitializer: Initializer<Unit> {

    override fun create(context: Context) {

    }

    override fun dependencies(): List<Class<out Initializer<*>>> {

       return emptyList()
    }
}