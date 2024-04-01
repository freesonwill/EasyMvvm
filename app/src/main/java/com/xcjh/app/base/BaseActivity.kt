package com.xcjh.app.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ImmersionBar
import com.hjq.language.MultiLanguages
import com.xcjh.app.R
import com.xcjh.app.utils.dismissLoadingExt
import com.xcjh.app.utils.showLoadingExt
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.base.activity.BaseVmDbActivity

/**
 * @author zobo
 * 2023.02.15
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .navigationBarColor(R.color.c_ffffff)
            .navigationBarDarkIcon(true)
            .init()

    }


    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(newBase)

        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }
    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) { //设置字体大小不跟随系统
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        val configContext = createConfigurationContext(resources.configuration)

        return configContext.resources.apply {
            configuration.fontScale = 1.0f
            displayMetrics.scaledDensity = displayMetrics.density * configuration.fontScale
        }
    }
}