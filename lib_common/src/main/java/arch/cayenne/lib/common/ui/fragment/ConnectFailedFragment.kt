package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.databinding.FragmentConnectFailedBinding
import kotlin.reflect.KClass

class ConnectFailedFragment : BaseFragment<EmptyViewModel, FragmentConnectFailedBinding>() {
    override val vbClass: KClass<FragmentConnectFailedBinding> = FragmentConnectFailedBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }

    fun show(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, this, this.javaClass.simpleName)
            .commit()
    }

    fun hide(activity: AppCompatActivity) {
        activity.supportFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    companion object {
        fun newInstance(): ConnectFailedFragment {
            return ConnectFailedFragment()
        }
    }
}