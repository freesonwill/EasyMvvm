package com.coolone.lib_base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.viewbinding.ViewBinding
import com.coolone.lib_base.base.BaseViewModel
import com.coolone.lib_base.inner.IToolBarView
import com.coolone.lib_base.inner.IView

/**
 * Description:
 * author       : baoyuedong
 * email        : baoyuedong@tsenf.io
 * createTime   : 2023/9/1 11:38
 **/
abstract class BaseActivity<V : ViewBinding, VB : BaseViewModel> : AppCompatActivity(), IView, IToolBarView {
    protected var mBinding: V? = null
    protected var mViewModel: VB? = null
    private var mRootView: View? = null
    private var toolbar:View? = null

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
    }

    private fun initContentView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(this).inflate(R.layout.activity_base, null, false)
            val toolbarVs = mRootView!!.findViewById<ViewStub>(R.id.vs_toolbar)
            toolbarVs.layoutResource = toolBarLayoutId()
            toolbar = toolbarVs.inflate()
            val fl_container = mRootView!!.findViewById<FragmentContainerView>(R.id.fl_container)
        }
        //ImmersionBar.with(this).navigationBarColor(R.color.black)
        setContentView(mRootView)
    }

    private fun initToolBar() {

    }

    private fun initView(savedInstanceState: Bundle?) {

    }

    private fun initData() {

    }

    abstract fun initViewModel(): VB

    /**
     * 获取布局文件
     * @return 布局layout的id
     */
    protected abstract fun layoutResId(): Int
}