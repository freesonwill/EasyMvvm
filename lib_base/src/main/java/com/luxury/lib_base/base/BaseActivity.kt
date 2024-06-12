package com.luxury.lib_base.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.luxury.lib_res.R
import com.luxury.lib_base.base.interface_.IView
import com.luxury.lib_base.ext.inflateBindingWithGeneric
import com.luxury.lib_base.utils.DayModeUtil
import java.lang.reflect.ParameterizedType

/**
 * @Description: Activity基类
 * @Author: baoyuedong
 * @Date: 2024/6/12 11:49
 */
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity(), IView {
    protected lateinit var mBinding: VB
    protected lateinit var mModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = createViewModel()
        mBinding = inflateBindingWithGeneric(layoutInflater)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        initStatusBar()
        setContentView(mBinding.root)
        initView(savedInstanceState)
        initObserver()
        //设置ARouter
    }

    override fun initStatusBar() {
        //初始化设置澄清状态栏
        ImmersionBar.with(this).keyboardEnable(true)
            .statusBarDarkFont(!DayModeUtil.isNightMode(this), 0.2f)
            .transparentStatusBar().init()
    }

    private fun createViewModel(): VM {
        val factory = provideViewModelFactory()
        return if (factory == null)
            ViewModelProvider(this).get(getVmClazz(this))
        else
            ViewModelProvider(this, factory).get(getVmClazz(this))
    }

    private fun <T> getVmClazz(obj: Any): T {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as T
    }
}