package arch.cayenne.lib.test.ui.activity

import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.test.R
import kotlin.reflect.KClass

class HomeActivity : BaseNavActivity<EmptyViewModel>() {

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun navigationID(): Int {
        return R.navigation.nav_graph_test_vp_home
    }
}