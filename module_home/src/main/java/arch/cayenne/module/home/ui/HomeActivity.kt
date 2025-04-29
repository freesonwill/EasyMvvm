package arch.cayenne.module.home.ui

import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.module.home.R
import kotlin.reflect.KClass

class HomeActivity : BaseNavActivity<EmptyViewModel>() {

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun navigationID(): Int {
        return R.navigation.nav_graph_home
    }
}