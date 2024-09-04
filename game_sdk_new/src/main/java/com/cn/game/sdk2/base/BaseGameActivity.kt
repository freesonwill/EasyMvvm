package com.cn.game.sdk2.base

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.xcjh.base_lib2.base.BaseViewModel
import com.xcjh.base_lib2.base.activity.BaseVmDbActivity


abstract class BaseGameActivity <  VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>() {

    override fun initView(savedInstanceState: Bundle?) {
     }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) //非默认值
            resources
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (res.configuration.fontScale != 1f) { //非默认值
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

//====================
    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    open fun finishTopClick(view: View?) {
        finish()
    }

    /**
     * 泛型的高级特性 泛型实例化
     * 跳转
     */
    inline fun <reified T> startNewActivity(block: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }


}