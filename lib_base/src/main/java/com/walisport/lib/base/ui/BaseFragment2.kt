package com.walisport.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.walisport.lib.base.R
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.lib.base.ui.interface_.StatusBarConfig
import com.walisport.lib.base.ui.interface_.IStatusBar
import com.walisport.lib.base.ui.interface_.IView
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.base.utils.LogUtilsExt.printStackTrace

/**
 * @author: zhangsan
 * @date: 2025/3/13 18:38
 * @description: ViewModelFragment基类，自动把ViewModel注入Fragment
 */
abstract class BaseFragment2<VM : BaseViewModel, VB : ViewBinding> : Fragment(), IView, IStatusBar {
    protected abstract fun createVB():VB
    protected abstract fun createVM():VM
    protected lateinit var mBinding: VB private set
    protected lateinit var mViewModel: VM private set
    protected open val TAG = this.javaClass.simpleName

    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = createVB()
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = viewLifecycleOwner
        }
        "onCreateView~~~~~~$this,root:${mBinding.root},isAdded:${isAdded}".logd(TAG)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
        trackLoadingTime()
    }

    override fun setStatusBar(config: StatusBarConfig) {
        statusBar.setStatusBar(config)
    }

    /**
     * 是否开启统计加载时间
     */
    open fun enableTrackLoadTime() = false

    /**
     * 统计加载时间
     */
    private fun trackLoadingTime() {
        if (!enableTrackLoadTime()) return
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            val TAG = this@BaseFragment2.javaClass.simpleName
            val FRAGMENT_INFO =
                "${this@BaseFragment2::class.java.simpleName}{${Integer.toHexString(this.hashCode())}}"
            var t1 = longArrayOf(0, 0, 0)

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                t1[0] = System.currentTimeMillis()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                t1[1] = System.currentTimeMillis()
                //"$FRAGMENT_INFO costMills onCreate->onStart: ${t1[1]-t1[0]}".logd(TAG)
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                t1[2] = System.currentTimeMillis()
                "$FRAGMENT_INFO costMills onCreate->onStart: ${t1[1] - t1[0]}, onStart->onResume:${t1[2] - t1[1]}, onCreate->onResume: ${t1[2] - t1[0]}".logd(
                    TAG
                )
                lifecycle.removeObserver(this)
            }
        })
    }

    /**
     * fragment是否在前台
     * @return
     */
    protected fun isAtFront(): Boolean {
        val v = view ?: return false
        return v.isLaidOut
    }

    /**
     * 获取childNavController： fragment嵌套了fragment
     *
     * @param id
     * @return
     */
    fun findChildNavController(@IdRes id: Int): NavController {
        return (childFragmentManager.findFragmentById(id) as NavHostFragment).findNavController()
    }

    /**
     * 获取activity的NavController
     *
     * @param id
     * @return
     */
    fun findActivityNavController(@IdRes id: Int = R.id.nav_host): NavController {
        return requireActivity().findNavController(id)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
        "onSaveInstanceState~~~~~~$this".logd(TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "onCreate~~~~~~$this".logd(TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        "onDestroy~~~~~~$this".logd(TAG)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        "onDestroyView~~~~~~$this".logd(TAG)
        "onDestroyView~~~~~~$this".printStackTrace(TAG)
    }
}
