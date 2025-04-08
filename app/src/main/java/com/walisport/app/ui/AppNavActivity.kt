package com.walisport.app.ui

import android.os.Bundle
import android.view.ViewGroup
import com.walisport.app.R
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.ContextUtils
import com.walisport.lib.common.databinding.ActvityBaseNavBinding
import com.walisport.lib.common.ui.BaseNavActivity
import kotlin.reflect.KClass

class AppNavActivity : BaseNavActivity<EmptyViewModel>() {
    override fun navigationID(): Int = R.navigation.nav_graph_app

    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initData() {
        super.initData()
    }

}