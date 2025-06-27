package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.FragmentConnectFailedBinding
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import kotlin.reflect.KClass

class ConnectFailedFragment : BaseFragment<EmptyViewModel, FragmentConnectFailedBinding>() {
    override val vbClass: KClass<FragmentConnectFailedBinding> = FragmentConnectFailedBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

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

    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commitNow()
    }

    fun showMask() {
        mBinding.root.visibility = View.VISIBLE
        mBinding.clFailed.visibility = View.GONE
    }

    fun showFailed() {
        mBinding.root.visibility = View.VISIBLE
        mBinding.clFailed.visibility = View.VISIBLE
    }

    fun hide() {
        mBinding.root.visibility = View.GONE
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