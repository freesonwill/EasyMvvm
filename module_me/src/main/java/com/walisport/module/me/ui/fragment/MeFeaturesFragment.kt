package com.walisport.module.me.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.BizUrl
import arch.cayenne.lib.common.data.constants.HomePageEnum
import arch.cayenne.lib.common.utils.biz.CommonBiz
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import com.walisport.module.me.R
import com.walisport.module.me.data.model.FeaturesBean
import com.walisport.module.me.databinding.FragmentMeFeaturesBinding
import com.walisport.module.me.ui.adapter.FeaturesAdapter
import com.walisport.module.me.ui.viewmodel.FeaturesViewModel
import kotlin.reflect.KClass

/**
 * 我的界面中的功能区域
 */

class MeFeaturesFragment : BaseFragment<FeaturesViewModel, FragmentMeFeaturesBinding>() {

    override val vbClass: KClass<FragmentMeFeaturesBinding> = FragmentMeFeaturesBinding::class
    override val vmClass: KClass<FeaturesViewModel> = FeaturesViewModel::class

    private val featuresAdapter by lazy {
        FeaturesAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        initRvFeatures()
    }

    private fun initRvFeatures() {
        mBinding.rvFeatures.apply {
            //某些机型上， RecyclerView存在过滚动效果。 禁用scroll, 禁用过滚动效果
            layoutManager = object : GridLayoutManager(requireContext(), 4) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }// 每行4个
            adapter = featuresAdapter
            itemAnimator = null
        }
        var id = 0
        featuresAdapter.submitList(
            listOf(
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_fund_details,
                    arch.cayenne.lib.common.R.string.drawer_fund_details
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment
                            .deeplink("url" to BizUrl.FUND_DETAIL.url))
                },
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_bet_record,
                    arch.cayenne.lib.common.R.string.drawer_bet_record
                ) {
                    CommonBiz.jump2HomePage(this, HomePageEnum.BETSLIP)
                },
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_realtime_cashback,
                    arch.cayenne.lib.common.R.string.drawer_cash_back
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_realtime_cashback_fragment.deeplink())
                },

                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_gift,
                    arch.cayenne.lib.common.R.string.drawer_gift
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_promotion_fragment.deeplink())
                },

                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_invite,
                    arch.cayenne.lib.common.R.string.drawer_invite
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_invite_friends_fragment.deeplink())
                },
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_partner,
                    arch.cayenne.lib.common.R.string.drawer_partner
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_partner_fragment.deeplink())
                },

                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_help,
                    arch.cayenne.lib.common.R.string.drawer_help
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to BizUrl.HELP.url) )
                },
                FeaturesBean(
                    id++ , arch.cayenne.lib.common.R.drawable.ic_drawer_feedback ,
                    arch.cayenne.lib.common.R.string.drawer_feedback ,
                    true ,
                    getString(R.string.prize)
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
                },
            )
        )
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
        mViewModel.createObserver()
    }

    companion object {
        const val TAG = "MeFeaturesFragment"
    }
}