package arch.cayenne.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.StatusBarConfig
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.interface_.IStatusBar
import arch.cayenne.lib.base.ui.interface_.IView
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseSideSheetDialogFragment<VM : BaseViewModel, VB : ViewBinding> : DialogFragment(),
    IView, IStatusBar {
   protected open val TAG = this.javaClass.simpleName
    //VB,VM
    protected lateinit var mBinding: VB; private set
    protected lateinit var mViewModel: VM; private set
    abstract val vbClass: KClass<VB>
    abstract val vmClass: KClass<VM>

    protected open fun createVB(container: ViewGroup?): VB {
        return getViewBind(vbClass,container,false)
    }
    protected open fun createVM(): VM {
        return viewModelForClass(vmClass).value
    }

    //设置颜色，默认根据主题颜色设定
    private val statusBar: IStatusBar by lazy { StatusBarDelegate(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BaseSideSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = createVB(container)
        mViewModel = createVM()
        if (mBinding is ViewDataBinding) {
            (mBinding as ViewDataBinding).lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.root.fitsSystemWindows = true
        initView(savedInstanceState)
        initListener()
        initData()
        createObserver()
        trackLoadingTime()
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
            val TAG = this@BaseSideSheetDialogFragment.javaClass.simpleName
            val FRAGMENT_INFO =
                "${this@BaseSideSheetDialogFragment::class.java.simpleName}{${Integer.toHexString(this.hashCode())}}"
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
     * 获取activity的NavController
     *
     * @param id
     * @return
     */
    fun findActivityNavController(@IdRes id: Int = R.id.nav_host): NavController {
        return requireActivity().findNavController(id)
    }

    override fun createObserver() {
    }


    override fun setStatusBar(config: StatusBarConfig) {
        statusBar.setStatusBar(config)
    }
}