package com.coolone.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.contains
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.coolone.lib_base.R
import com.coolone.lib_base.base.inner.IBaseView
import com.coolone.lib_base.base.inner.INetView
import com.coolone.lib_base.ext.inflateBindingWithGeneric
import java.lang.reflect.ParameterizedType

/**
 *
 */
abstract class BaseVMActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    IBaseView, INetView {
    private lateinit var mSplashScreen: SplashScreen
    protected lateinit var mViewBinding: VB
    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSplashScreen = installSplashScreen()
        initContentView()
        initViewModel()
        initView(savedInstanceState)
        initObserver()
    }

    open fun enableToolbarVisible(): Boolean {
        return true
    }

    open fun getToolBarLayoutResId(): Int {
        return R.layout.toolbar_layout_custom
    }

    private fun initContentView() {
        val mRootView = LayoutInflater.from(this).inflate(R.layout.activity_base, null, false)
        //toolbar容器
        if (enableToolbarVisible()) {
            val toolbarVs = mRootView.findViewById<ViewStub>(R.id.vs_toolbar)
            toolbarVs.layoutResource = getToolBarLayoutResId()
            val mTopBarView = toolbarVs.inflate()
        }
        mRootView!!.findViewById<FrameLayout>(R.id.fl_container).also {
            mViewBinding = inflateBindingWithGeneric(layoutInflater)
            mViewBinding.lifecycleOwner = this
            if(!it.contains(mViewBinding.root)) it.addView(mViewBinding.root)
        }
        setContentView(mRootView)
    }

    private fun initViewModel() {
        mViewModel = createViewModel()
    }

    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    private fun <T> getVmClazz(obj: Any): T {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as T
    }
}