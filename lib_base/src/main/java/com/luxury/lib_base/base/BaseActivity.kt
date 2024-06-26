package com.luxury.lib_base.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.luxury.lib_base.base.interface_.IView
import com.luxury.lib_base.ext.inflateBindingWithGeneric
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity(), IView {
    protected lateinit var mBinding: VB
    protected lateinit var mModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = createViewModel()
        mBinding = inflateBindingWithGeneric(layoutInflater)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        setContentView(mBinding.root)
        initView(savedInstanceState)
        initObserver()
        //设置状态栏
        //设置ARouter

    }


    private fun createViewModel(): VM {
        val factory = initViewModelFactory() ?: return ViewModelProvider(this)[getVmClazz(this)]
        return ViewModelProvider(this, factory).get(getVmClazz(this))
    }

    private fun <T> getVmClazz(obj: Any): T {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as T
    }
}