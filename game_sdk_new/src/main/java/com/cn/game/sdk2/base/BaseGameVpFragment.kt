package com.cn.game.sdk2.base

import androidx.databinding.ViewDataBinding
import com.xcjh.base_lib2.base.BaseViewModel

abstract class BaseGameVpFragment <VM : BaseViewModel, VB : ViewDataBinding> : BaseGameFragment<VM, VB>() {
    abstract val typeId: Long

}