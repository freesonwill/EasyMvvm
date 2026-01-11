package arch.cayenne.module.account.ui.activity

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.common.ui.BaseNavActivity
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarColorExt
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import arch.cayenne.module.account.R
import arch.cayenne.module.account.ui.viewmodel.LoginActivityViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2026/1/10 17:27
 * @description:
 */

class LoginActivity : BaseNavActivity<LoginActivityViewModel>() {
    override fun navigationID(): Int = R.navigation.nav_graph_login
    override val vmClass: KClass<LoginActivityViewModel> = LoginActivityViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun configStatusBar(): StatusBarConfig {
        StatusBarConfig.statusBarColor = immersionBarColorExt(mViewModel.getSkinType())
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(mViewModel.getSkinType())
        return StatusBarConfig
    }

}
