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
import arch.cayenne.lib.base.ui._interface.IView
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd


/**
 * @author: zhangsan
 * @date: 2025/4/15 18:32
 * @description: UI绑定组件，封装 ViewBinding + ViewModel 生命周期处理
 *
 * @property uiOwner: UI宿主
 * @property vmProvider： 提供viewModel
 * @property vbProvider： 提供viewBinding
 * @property keepViewOnNavigation: 在导航（Navigation）时是否保留 View（原生的会销毁）
 */
class UIBindDelegate<UIOwner, VM, VB>(
    private val uiOwner: UIOwner,
    private val vmProvider: () -> VM,
    private val vbProvider: (container: ViewGroup?) -> VB,
    private val keepViewOnNavigation:Boolean
) where UIOwner : IView, UIOwner : LifecycleOwner,
        VM : BaseViewModel,
        VB : ViewBinding {

    private val TAG = uiOwner::class.java.simpleName
    private var _binding: VB? = null
    private var _viewModel: VM? = null
    val binding: VB get() = _binding ?: error("binding is null")
    val viewModel: VM get() = _viewModel ?: error("viewModel is null")
    //是否第一次初始化
    private var firstInit: Boolean = false
    private var destroyRunnable:Runnable? = null

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        destroyRunnable?.let { binding.root.removeCallbacks(it) }
        if(_binding == null || !keepViewOnNavigation) {
            firstInit = true
            _binding = vbProvider(container)
            _viewModel = vmProvider()
            (binding as? ViewDataBinding)?.let {
                it.lifecycleOwner = if (uiOwner is Fragment) uiOwner.viewLifecycleOwner else uiOwner
            }
        } else {
            firstInit = false
        }
        return _binding!!.root
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(firstInit) {
            binding.root.fitsSystemWindows = true
            trackLoadingTime()
            viewModel.initViewModel()
            uiOwner.initView(savedInstanceState)
            uiOwner.initListener()
            uiOwner.initData()
        }
        uiOwner.createObserver()
    }

    fun onDestroyView() {
        if(!keepViewOnNavigation) performDestroy()
    }

    fun onDestroy(){
        performDestroy()
    }

    /**
     * 销毁
     */
    private fun performDestroy(){
        //延迟一帧置空，避免子类调用binding为null
        destroyRunnable = Runnable{
            _binding = null
            _viewModel = null
            destroyRunnable = null
        }
        binding.root.post(destroyRunnable)
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