package com.walisport.module.me.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.DrawerAction.ACTION_OPEN
import arch.cayenne.lib.common.data.constants.DrawerAction.KEY_ACTION
import arch.cayenne.lib.common.data.constants.DrawerAction.REQUEST_KEY_DRAWER
import arch.cayenne.lib.common.ui.viewmodel.UnReadMessageViewModel
import arch.cayenne.lib.common.utils.biz.CommonBiz
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.me.databinding.FragmentMeBinding
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlin.reflect.KClass


/**
 * 我的界面
 *
 * VIP原有12个等级
 *
 * 铜 白银 黄金 铂金 钻石 绿钻 红钻 黑钻 星钻 陨钻 星辰 宇宙
 *
 * 后台设定xx-xx位白银，xx-xx位黄金
 */

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {

    override val vbClass: KClass<FragmentMeBinding> = FragmentMeBinding::class
    override val vmClass: KClass<MeViewModel> = MeViewModel::class

    private val unreadMessageViewModel: UnReadMessageViewModel by viewModels()



    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            tvNickname.text = "中文sdf323"
            val day = 137
            tvJoinTime.text = "已加入${day}天"
        }

        initVIPInfo()
        initFeatures()
        initBottom()
    }

    private fun initVIPInfo() {
        childFragmentManager.findFragmentByTag(MeVIPInfoFragment.TAG) as? MeVIPInfoFragment
            ?: MeVIPInfoFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentVipInfo.id, it, MeVIPInfoFragment.TAG).commitNow()
            }
    }

    private fun initFeatures() {
        childFragmentManager.findFragmentByTag(MeFeaturesFragment.TAG) as? MeFeaturesFragment
            ?: MeFeaturesFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentFeatures.id, it, MeFeaturesFragment.TAG).commitNow()
            }
    }


    private fun initBottom() {
        childFragmentManager.findFragmentByTag(BottomFragment.TAG) as? BottomFragment
            ?: BottomFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(mBinding.fragmentBottom.id, it, BottomFragment.TAG).commitNow()
            }
    }


    override fun initListener() {
        with(mBinding) {

            ivDrawer.addScaleOnTouchAnimation()
            ivDrawer.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResult(
                    REQUEST_KEY_DRAWER,
                    bundleOf(KEY_ACTION to ACTION_OPEN)
                )
            }

            ivCustomer.addScaleOnTouchAnimation()
            ivCustomer.clickNoRepeat {
                CommonBiz.jump2CustomerService(this@MeFragment)
            }

            ivSetting.addScaleOnTouchAnimation()
            ivSetting.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }
        }
    }

    override suspend fun createObserver() {
        with(unreadMessageViewModel) {
            //未读消息监听
            unreadMsg.observe(viewLifecycleOwner) { flag ->
                mBinding.ivUnreadDot.visibility = if (flag) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        unreadMessageViewModel.createObserver()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llContent)
        super.onStart()
    }


}