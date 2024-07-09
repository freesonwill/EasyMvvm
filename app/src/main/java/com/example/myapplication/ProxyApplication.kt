package com.example.myapplication

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk2.utils.GamePartyLibraryInitializer


class ProxyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}