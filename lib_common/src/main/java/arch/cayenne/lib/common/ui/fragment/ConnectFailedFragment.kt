package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.CurConnectFailedType
import arch.cayenne.lib.common.databinding.FragmentConnectFailedBinding
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.ConnectFailedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.reflect.KClass

class ConnectFailedFragment : BaseFragment<EmptyViewModel, FragmentConnectFailedBinding>() {
    override val vbClass: KClass<FragmentConnectFailedBinding> = FragmentConnectFailedBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    private val connectFailedViewModel: ConnectFailedViewModel by activityViewModel()

    private var refreshListener: (() -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            dsl.setState(DynamicStateLayout.States.NETWORK_ANOMALY, getString(R.string.error_net)){
                refreshListener?.invoke()
            }
            mBinding.clFailed.visibility = View.GONE
            mBinding.root.visibility = View.GONE
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {
        connectFailedViewModel.curConnectFailedType.observe(viewLifecycleOwner) {
            when(it) {
                CurConnectFailedType.SHOW_MASK -> {
                    mBinding.root.visibility = View.VISIBLE
                    mBinding.clFailed.visibility = View.GONE
                }
                CurConnectFailedType.SHOW_FAILED -> {
                    mBinding.root.visibility = View.VISIBLE
                    mBinding.clFailed.visibility = View.VISIBLE
                }
                CurConnectFailedType.HIDE -> {
                    mBinding.root.visibility = View.GONE
                    mBinding.clFailed.visibility = View.GONE
                }
            }
        }
    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commitNow()
    }

    fun setRefreshListener(listener: (()-> Unit)) {
        refreshListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshListener = null
    }

    companion object {
        fun newInstance(): ConnectFailedFragment {
            return ConnectFailedFragment()
        }
    }
}