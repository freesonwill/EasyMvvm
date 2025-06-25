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
        }
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

    fun showMask(activity: AppCompatActivity) {
        if (activity.supportFragmentManager.findFragmentByTag(this.javaClass.simpleName)?.isAdded == true) return
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commit()
    }

    fun showFailed(activity: AppCompatActivity) {
        if (activity.supportFragmentManager.findFragmentByTag(this.javaClass.simpleName) == null) return
        mBinding.clFailed.visibility = View.VISIBLE
    }

    fun hide(activity: AppCompatActivity) {
        if (activity.supportFragmentManager.findFragmentByTag(this.javaClass.simpleName) == null) return
        activity.supportFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    fun setRefreshListener(listener: (()-> Unit)) {
        refreshListener = listener
    }

    companion object {
        fun newInstance(): ConnectFailedFragment {
            return ConnectFailedFragment()
        }
    }
}