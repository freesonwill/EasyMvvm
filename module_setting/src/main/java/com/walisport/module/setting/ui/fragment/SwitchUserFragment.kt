package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentSwitchBinding
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 切换账户页面
 */

class SwitchUserFragment : BaseFragment<SettingViewModel, FragmentSwitchBinding>() {

    override val vbClass: KClass<FragmentSwitchBinding> = FragmentSwitchBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(
            R.string.change_user,
            { findNavController().navigateUp() },
            { setEditStatus(true) },
            R.string.edit.getString()
        )
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

    private fun setEditStatus(isEditable: Boolean) {

    }
}