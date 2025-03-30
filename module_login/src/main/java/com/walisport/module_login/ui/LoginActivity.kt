package com.walisport.module_login.ui

import android.os.Bundle
import com.walisport.lib_base.utils.LogUtilsExt.logd
import com.walisport.lib_common.ui.BaseNavActivity
import com.walisport.module_login.R

class LoginActivity : BaseNavActivity() {
    override fun navigationID(): Int = R.navigation.login_nav_graph

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        //val userId: String? = intent?.data?.getQueryParameter("userId")
        "LoginFragment--->$userId".logd(TAG)
    }
}