package com.walisport.module.login.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.BaseNavActivity
import com.walisport.module.login.R
import kotlin.reflect.KClass

class LoginActivity : BaseNavActivity<EmptyViewModel>() {
    override fun navigationID(): Int = R.navigation.nav_graph_login

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        //val userId: String? = intent?.data?.getQueryParameter("userId")
        "LoginFragment--->$userId".logd(TAG)
    }
}