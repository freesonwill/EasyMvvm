package com.example.myapplication

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.cn.game.sdk.utils.GamePartyLibraryInitializer

class ProxyApplication : Application()  , ViewModelStoreOwner{
    private lateinit var mAppViewModelStore: ViewModelStore
    override fun onCreate() {
        super.onCreate()
        mAppViewModelStore = ViewModelStore()
        val ddd=GamePartyLibraryInitializer
        ddd.initialize(this,this)
    }
    override fun getViewModelStore(): ViewModelStore {
        return  mAppViewModelStore
    }

}