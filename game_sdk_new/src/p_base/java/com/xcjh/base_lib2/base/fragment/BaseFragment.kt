package com.xcjh.base_lib2.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ThreadUtils.mainScope
import androidx.viewbinding.ViewBinding
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib2.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.lang.reflect.Method


/**
 * 作者　:
 * 时间　: 2019/12/12
 * 描述　: ViewModelFragment基类，自动把ViewModel注入Fragment
 */

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : Fragment(), GameApp.GameSdkKoinComponent {

    //是否第一次加载
    private var isFirst: Boolean = true
    private var lazyJob: Job? = null

    protected abstract val mBinding: VB
    protected abstract val mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化监听器
     */
    open fun initListener(){}

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    /**
     * 创建观察者
     */
    abstract fun createObserver()

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            // 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿
            lazyJob = mainScope.launchWithCustomContext(this.javaClass.simpleName) {
                delay(lazyLoadTime())
                lazyLoadData()
                //在Fragment中，只有懒加载过了才能开启网络变化监听
                isFirst = false
            }
        }
    }



    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    /**
     * 延迟加载 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    open fun lazyLoadTime(): Long {
        return 300
    }

    override fun onDestroy() {
        super.onDestroy()
        lazyJob?.cancel()
    }
}

inline fun <reified T : ViewBinding> Fragment.viewBind(): Lazy<T> =
    lazy {
        T::class.java.inflateMethod?.invoke(null, layoutInflater) as T
    }

val <T> Class<T>.inflateMethod: Method?
    get() =
        try {
            getMethod("inflate", LayoutInflater::class.java)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
            null
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }