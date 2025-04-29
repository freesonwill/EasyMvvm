package arch.cayenne.lib.common.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.NavigationRes
import androidx.navigation.NavController
import androidx.navigation.findNavController
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ActvityBaseNavBinding
import kotlin.reflect.KClass

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:29
 * @description: 基础navigation的activity
 */
abstract class BaseNavActivity<VM: BaseViewModel> : BaseActivity<VM, ActvityBaseNavBinding>() {
    override val vbClass: KClass<ActvityBaseNavBinding> get() = ActvityBaseNavBinding::class
    protected fun findNavController(): NavController = supportFragmentManager
        .findFragmentById(R.id.nav_host)!!.findNavController()

    @NavigationRes
    abstract fun navigationID(): Int

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        findNavController().setGraph(navigationID(),intent.extras)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}