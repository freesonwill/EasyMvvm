package com.walisport.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.walisport.lib.base.ui.interface_.IView

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment(), IView {
    protected abstract val mBinding: VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView(savedInstanceState)
        return mBinding.root
    }
}