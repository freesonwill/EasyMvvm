package com.luxury.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.luxury.lib_base.base.interface_.IView
import com.luxury.lib_base.ext.inflateBindingWithGeneric
import java.lang.reflect.ParameterizedType


/**
 * @Description: Fragment基类
 * @Author: brain
 * @Date: 2024/6/12 11:49
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(), IView {
    protected lateinit var mBinding: VB
    protected lateinit var mModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mModel = createViewModel().also { it.onInit() }
        mBinding = inflateBindingWithGeneric(layoutInflater)
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = this
        }
        initView(savedInstanceState)
        registerObserver()
        return mBinding.root
    }

    open fun createViewModel(): VM {
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