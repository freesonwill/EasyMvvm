package arch.cayenne.lib.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.R
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.ui.interface_.IView
import org.koin.androidx.viewmodel.ext.android.viewModelForClass
import kotlin.reflect.KClass

abstract class BaseVMDialogFragment<VM : BaseViewModel, VB : ViewBinding> : DialogFragment(), IView {
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
        dialog?.window?.apply {
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_base_dialog
                )
            )
        }
        initView(savedInstanceState)
        initListener()
        createObserver()
    }


    fun show(manager: FragmentManager) {
        val f = manager.findFragmentByTag(this::class.java.simpleName)
        if (f == null || !f.isAdded) {
            super.show(manager, this::class.java.simpleName)
        }
    }
}