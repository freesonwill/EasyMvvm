package com.luxury.lib_base.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.gyf.immersionbar.ImmersionBar
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
    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = createViewModel()
        mBinding = inflateBindingWithGeneric(layoutInflater)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        initStatusBar()
        setContentView(mBinding.root)
        initView(savedInstanceState)
        registerUiChange()
        //设置ARouter
        ARouter.getInstance().inject(this)
    }

    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observe(this) {
            showLoading(it)
        }
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observe(this) {
            dismissLoading()
        }
        registerObserver()
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

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