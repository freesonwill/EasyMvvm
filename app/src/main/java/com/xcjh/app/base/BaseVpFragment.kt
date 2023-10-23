package com.xcjh.app.base

import androidx.databinding.ViewDataBinding
import com.xcjh.base_lib.base.BaseViewModel

abstract class BaseVpFragment<VM : BaseViewModel, VB : ViewDataBinding> : BaseFragment<VM, VB>() {
    abstract val typeId: Long

}