package com.walisport.module.misc.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
import arch.cayenne.lib.common.utils.ext.ResourceExt
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ScreenUtils
import com.gyf.immersionbar.ImmersionBar
import com.walisport.module.misc.BuildConfig
import com.walisport.module.misc.R
import com.walisport.module.misc.databinding.FragmentDebugBinding
import com.walisport.module.misc.ui.viewmodel.DebugViewModel
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale
import kotlin.reflect.KClass

/**
 * 调试信息页面

 */
class DebugFragment : BaseFragment<DebugViewModel, FragmentDebugBinding>() {

    override val vbClass: KClass<FragmentDebugBinding> = FragmentDebugBinding::class
    override val vmClass: KClass<DebugViewModel> = DebugViewModel::class

    private val manager: UserDataManager by inject(UserDataManager::class.java)


    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBar(titleBarBinding.root, null)
        titleBarBinding.root.setBackgroundColor(arch.cayenne.lib.common.R.color.blue.getColor())
        titleBarBinding.tvTitleName.text = "调试信息"
        mBinding.tvStatusBarHeight.text =
            ImmersionBar.getStatusBarHeight(requireContext()).let {
                String.format(
                    Locale.ROOT,
                    "顶部安全高度：${calculateTopSafeInset(it)}  状态栏高度: $it px,\t${it.px2dp} dp, dp2px:${1f.dp2px},px2dp:${1f.px2dp},w-h:${ScreenUtils.getScreenWidth()}-${ScreenUtils.getScreenHeight()}",
                    it
                )
            }

        mBinding.tvUid.setText(manager.getValue<Int>(UserDataKey.KEY_UID, 0).toString())
        mBinding.tvToken.setText(manager.getValue<String>(UserDataKey.KEY_TOKEN, ""))
    }

    private fun calculateTopSafeInset(it: Int): Int {
        var space = 58 - it.px2dp

        //特殊机型枚举
        //如果是小米，且状态栏高度是137px
        if (Build.MANUFACTURER.equals(
                "Xiaomi",
                ignoreCase = true
            ) && it == 137
        ) {
            space = 17
        }

        // 如果是vivo，且状态栏高度是87px
        if (Build.MANUFACTURER.equals("vivo", ignoreCase = true) && it == 87) {
            space = 22
        }

        //如果是ROG， 且状态栏高度是78px
        if (Build.MANUFACTURER.equals("asus", ignoreCase = true) && it == 78) {
            space = 24
        }

        return space
    }


    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }

        }

        with(mBinding) {
            btnSetAccount.clickNoRepeat {
                if (tvUid.text.isNullOrEmpty()) {
                    showToast("请输入uid")
                    return@clickNoRepeat
                }

                if (tvToken.text.isNullOrEmpty()) {
                    showToast("请输入token")
                    return@clickNoRepeat
                }

                val uid = tvUid.text!!.trim().toString().toInt()
                val token = tvToken.text!!.trim().toString()
                manager.setKeyValue(UserDataKey.KEY_UID, uid)
                manager.setKeyValue(UserDataKey.KEY_TOKEN, token)

                showToast("设置成功，重启app生效")
            }


            btnShareLog.clickNoRepeat {
                if(BuildConfig.BUILD_TYPE != "release")
                    launch {
                        Utils.shareLogFile(requireContext())
                    }
            }

            comboDetail.clickNoRepeat {
                ComboDetailFragment.newInstance(
                    GsonUtils.fromJson(
                        ResourceExt.getAssets("mock_combo_detail_20@19@1.json"),
                        //ResourceExt.getAssets("mock_combo_detail_4@15.json"),
                        //ResourceExt.getAssets("mock_combo_detail_3@1.json"),
                        ComboDetailFragment.Parameter::class.java
                    )
                ).show(parentFragmentManager, "ComboDetailFragment")
            }

        }
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig, mBinding.root)
        super.onStart()
    }


}