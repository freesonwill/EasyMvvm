package com.xcjh.tpInfo.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.base.activity.BaseVmVbActivity
import com.xcjh.tpInfo.utils.dismissLoadingExt
import com.xcjh.tpInfo.utils.showLoadingExt

/**
 * @author zobo
 * 2023.02.15
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewBinding> : BaseVmVbActivity<VM, DB>()  {


    override fun initView(savedInstanceState: Bundle?){
        ImmersionBar.with(this)
            .statusBarDarkFont(false)//白色
            .init()
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
    inline fun <reified T> startNewActivity( block: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }
}