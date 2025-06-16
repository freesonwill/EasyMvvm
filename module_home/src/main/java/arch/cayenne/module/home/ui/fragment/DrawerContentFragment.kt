package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.data.constants.KeyConfig
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class
    private var onFunctionClick: (() -> Unit)? = null

    companion object {
        const val TAG = "DrawerContentFragment"
    }
    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig,mBinding.root)
    }

    override fun initListener() {
        with(mBinding) {
            clDrawerNickname.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResultListener(
                    KeyConfig.VALUE_NICKNAME_RESULT,
                    viewLifecycleOwner
                ) { _, bundle ->
                    requireActivity().supportFragmentManager.clearFragmentResultListener(KeyConfig.VALUE_NICKNAME_RESULT)
                    val isSaveSuccess = bundle.getBoolean(KeyConfig.VALUE_SAVE_NICKNAME, false)
                    if (isSaveSuccess) {
                        refreshDefaultNickName()
                    }
                }
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_personal_info_fragment.deeplink())
            }
            clNotificationHeader.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            llRecharge.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_topup_fragment.deeplink())
            }
            llDrawerTutorial.clickNoRepeat {
                navigatePage(Uri.parse("walisport://module_handicap/HandicapFragment?homeId=${R.id.newHomeFragment}"))
            }
            llDrawerSetting.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }

            llDrawerFeedback.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
            }
            llBetSlip.clickNoRepeat {
                onFunctionClick?.invoke()
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
        }
    }
    private fun navigatePage(uri: Uri) {
        onFunctionClick?.invoke()
        navigate(uri)
    }
    fun setOnFunctionClickListener(listener: () -> Unit) {
        onFunctionClick = listener
    }
    override fun createObserver() {
        launch {
            mViewModel.selectedSkinType.observe(viewLifecycleOwner) {
                refreshDefaultNickName()
            }
        }
    }
    private fun refreshDefaultNickName() {
        val defaultResId = mViewModel.getDefaultResId()
        with(mBinding) {
            if (defaultResId == -1) {
                ivIconNickname.setImageResource(R.drawable.ic_drawer_nickname)
            } else {
                ivIconNickname.setImageDrawable(resources.getDrawable(defaultResId, null))
            }
            val defaultNickName = mViewModel.getDefaultNickName()
            "tvTitleNickname.text = $defaultNickName".logd()
            if (defaultNickName.isNotEmpty()) {
                tvTitleNickname.text = defaultNickName
            } else {
                tvTitleNickname.text = getString(R.string.drawer_nickname_title)
            }
        }
    }
}