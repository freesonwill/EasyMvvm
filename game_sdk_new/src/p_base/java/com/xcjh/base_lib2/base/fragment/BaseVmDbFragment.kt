package com.xcjh.base_lib2.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.xcjh.base_lib2.base.BaseViewModel
import com.xcjh.base_lib2.utils.inflateBindingWithGeneric

/**
 * 作者　:
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment和Databind注入进来了
 * 需要使用Databind的清继承它
 */
abstract class BaseVmDbFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmFragment<VM>() {

    override fun layoutId() = 0

    //该类绑定的ViewDataBinding
    private var _binding: DB? = null
    val mDatabind: DB get() = _binding!!
    var preloadBinding : DB ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(null != preloadBinding){
            _binding = preloadBinding
            mDatabind.lifecycleOwner = viewLifecycleOwner
        }else {
            _binding = inflateBindingWithGeneric(inflater, container, false)
        }
        return mDatabind.root
//        val viewModelProvider = ViewModelProvider(this)
//        viewModelProvider[mViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}