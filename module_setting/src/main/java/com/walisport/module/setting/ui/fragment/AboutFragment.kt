package com.walisport.module.setting.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.setting.R
import com.walisport.module.setting.databinding.FragmentAboutBinding
import com.walisport.module.setting.ui.viewmodel.SettingViewModel
import kotlin.reflect.KClass

/**
 * 关于我们界面
 */

class AboutFragment : BaseFragment<SettingViewModel, FragmentAboutBinding>() {

    override val vbClass: KClass<FragmentAboutBinding> = FragmentAboutBinding::class
    override val vmClass: KClass<SettingViewModel> = SettingViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.about_us, {
            findNavController().navigateUp()
        })
        mBinding.tvAboutVer.text = getVersion()
        mBinding.tvVersion.text = getVersion()
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.layVersion.clickNoRepeat {

        }
    }

    override suspend fun createObserver() {

    }

    private fun getVersion(): String {
        val app = Utils.getApp()
        val info = app.packageManager.getPackageInfo(app.packageName, 0)
        return "V${info.versionName}_${arch.cayenne.lib.common.BuildConfig.BUILD_TIME}"
    }
}