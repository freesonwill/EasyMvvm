package com.walisport.lib_databse

import android.app.Application
import com.walisport.lib_databse.testModuleList
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(testModuleList)
        }
    }
}