package com.walisport.module.me.ui.fragment

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.me.R
import com.walisport.module.me.data.constants.VIPLevel
import com.walisport.module.me.data.model.FeaturesBean
import com.walisport.module.me.databinding.FragmentMeBinding
import com.walisport.module.me.ui.adapter.FeaturesAdapter
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

    private val shaderEndColorMap: Map<VIPLevel, Int> =
        mapOf(
            VIPLevel.Copper to R.color.shader_end_copper,
            VIPLevel.Platinum to R.color.shader_end_platinum,
            VIPLevel.BlackDiamond to R.color.shader_end_black_diamond
        )

    private val foregroundResMap = mapOf(
        VIPLevel.Copper to R.drawable.bg_copper,
        VIPLevel.Platinum to R.drawable.bg_platinum,
        VIPLevel.BlackDiamond to R.drawable.bg_black_diamond
    )

    private val backgroundResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.bg_shape_copper,
            VIPLevel.Platinum to R.drawable.bg_shape_platinum,
            VIPLevel.BlackDiamond to R.drawable.bg_shape_black_diamond
        )

    private val percentResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.ic_percent_copper,
            VIPLevel.Platinum to R.drawable.ic_percent_platinum,
            VIPLevel.BlackDiamond to R.drawable.ic_percent_black_diamond
        )

    private val iconResMap = mapOf(
        VIPLevel.Copper to R.drawable.ic_level_copper,
        VIPLevel.Platinum to R.drawable.ic_level_platinum,
        VIPLevel.BlackDiamond to R.drawable.ic_level_black_diamond
    )

    private val levelResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.ic_level_name_copper,
            VIPLevel.Platinum to R.drawable.ic_level_name_platinum,
            VIPLevel.BlackDiamond to R.drawable.ic_level_name_black_diamond
        )


    private val featuresAdapter by lazy {
        FeaturesAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {
            root.touchBackPressed()

            tvNickname.text = "中文sdf323"
            val day = 137
            tvJoinTime.text = "已加入${day}天"

        }

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
                    showToast(arch.cayenne.lib.common.R.string.drawer_fund_details.getString())
                },
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_bet_record,
                    arch.cayenne.lib.common.R.string.drawer_bet_record
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_bet_record.getString())
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
                    showToast(arch.cayenne.lib.common.R.string.drawer_gift.getString())
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
                    showToast(arch.cayenne.lib.common.R.string.drawer_help.getString())
                },
                FeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_feedback,
                    arch.cayenne.lib.common.R.string.drawer_feedback
                ) {
                    navigate(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
                },
            )
        )
    }

    override fun initListener() {
        with(mBinding) {

            ivDrawer.addScaleOnTouchAnimation()
            ivDrawer.clickNoRepeat { }

            ivCustomer.addScaleOnTouchAnimation()
            ivCustomer.clickNoRepeat { }

            ivSetting.addScaleOnTouchAnimation()
            ivSetting.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }

            ivVipEntry.addScaleOnTouchAnimation()
            ivVipEntry.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_vip_fragment.deeplink())
            }

            btWithdraw.addScaleOnTouchAnimation()
            btWithdraw.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_withdraw_fragment.deeplink())
            }

            btRecharge.addScaleOnTouchAnimation()
            btRecharge.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_topup_fragment.deeplink())
            }
        }

    }

    override suspend fun createObserver() {
        with(mViewModel) {
            vipLevelLiveData.observe(viewLifecycleOwner) {

                vipLevelLiveData.value?.let {
                    //这里是测试代码， 具体的等级转换关系由后台配置
                    val vipLevel = if (it < 10) {
                        VIPLevel.Copper
                    } else if (it in 10..19) {
                        VIPLevel.Silver
                    } else if (it in 20..29) {
                        VIPLevel.Gold
                    } else if (it in 30..39) {
                        VIPLevel.Platinum
                    } else if (it in 40..49) {
                        VIPLevel.Diamond
                    } else if (it in 50..59) {
                        VIPLevel.GreenDiamond
                    } else if (it in 60..69) {
                        VIPLevel.RedDiamond
                    } else if (it in 70..79) {
                        VIPLevel.BlackDiamond
                    } else if (it in 80..89) {
                        VIPLevel.StarDiamond
                    } else if (it in 90..99) {
                        VIPLevel.MeteoriteDiamond
                    } else if (it in 100..109) {
                        VIPLevel.Stars
                    } else {
                        VIPLevel.Universe
                    }

                    mBinding.ctVipInfo.background =
                        backgroundResMap.getValue(vipLevel).getDrawable()
                    mBinding.ctLevelInfo.background =
                        foregroundResMap.getValue(vipLevel).getDrawable()

                    mBinding.ivLevel.setImageResource(iconResMap.getValue(vipLevel))
                    mBinding.ivLevelName.setImageResource(levelResMap.getValue(vipLevel))

                    val bottom = 30.dp2px.toFloat()
                    // 创建线性渐变
                    val linearGradient = LinearGradient(
                        0f, 0f,  // 渐变起点 (x1, y1)
                        0f, bottom,  // 渐变终点 (x2, y2)
                        intArrayOf(
                            R.color.shader_start.getColor(),
                            shaderEndColorMap.getValue(vipLevel).getColor()
                        ),  // 渐变颜色数组
                        null,  // 渐变位置（null 表示均匀分布）
                        Shader.TileMode.CLAMP // 填充模式
                    )


                    mBinding.tvLevel.paint.shader = linearGradient
                    mBinding.tvLevel.text = "VIP ${it}"

                    mBinding.tvPercent.text = "57.91%"
                    mBinding.ivPercent.setImageResource(percentResMap.getValue(vipLevel))
                    mBinding.tvLevelUpInfo.text = "升级还需¥59w投注额"

                    mBinding.tvBalance.text = "¥19901.00"
                }


            }
        }

        mViewModel.createObserver()
    }

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        mBinding.root.fitsSystemWindows = false
        setStatusBar(StatusBarConfig, mBinding.llContent)
        super.onStart()
    }


}