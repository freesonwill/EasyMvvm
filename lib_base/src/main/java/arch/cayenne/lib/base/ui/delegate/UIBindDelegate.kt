package arch.cayenne.lib.base.ui.delegate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

/**
 * @author: zhangsan
 * @date: 2025/4/15 18:32
 * @description: UI绑定组件，封装 ViewBinding + ViewModel 生命周期处理
 */
class UIBindComponent<UIOwner, VM, VB>(
    private val uiOwner: UIOwner,
    private val vmProvider: () -> VM,
    private val vbProvider: (container: ViewGroup?) -> VB,
) where UIOwner : IView, UIOwner : LifecycleOwner,
        VM : BaseViewModel,
        VB : ViewBinding {

    private val TAG = uiOwner::class.java.simpleName
    private var _binding: VB? = null
    private var _viewModel: VM? = null
    val binding: VB get() = _binding ?: error("binding is null")
    val viewModel: VM get() = _viewModel ?: error("viewModel is null")

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        _binding = vbProvider(container)
        _viewModel = vmProvider()
        (binding as? ViewDataBinding)?.let {
            it.lifecycleOwner = if (uiOwner is Fragment) uiOwner.viewLifecycleOwner else uiOwner
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.fitsSystemWindows = true
        trackLoadingTime()
        viewModel.initViewModel()
        uiOwner.initView(savedInstanceState)
        uiOwner.initListener()
        uiOwner.createObserver()
        uiOwner.initData()
    }

    fun onDestroyView() {
        //延迟一帧置空，避免子类调用binding为null
        binding.root.post {
            _binding = null
            _viewModel = null
        }
    }

    /**
     * 统计加载时间
     */
    private fun trackLoadingTime() {
        if (!uiOwner.enableTrackLoadTime()) return
        uiOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val FRAGMENT_INFO = "${TAG}{${Integer.toHexString(this.hashCode())}}"
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
                uiOwner.lifecycle.removeObserver(this)
            }
        })
    }
}