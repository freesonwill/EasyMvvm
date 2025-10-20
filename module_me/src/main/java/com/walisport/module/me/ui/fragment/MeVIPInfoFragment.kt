package com.walisport.module.me.ui.fragment

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.me.R
import com.walisport.module.me.data.constants.VIPLevel
import com.walisport.module.me.databinding.FragmentMeVipInfoBinding
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
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

class MeVIPInfoFragment : BaseFragment<MeVIPInfoViewModel, FragmentMeVipInfoBinding>() {

    override val vbClass: KClass<FragmentMeVipInfoBinding> = FragmentMeVipInfoBinding::class
    override val vmClass: KClass<MeVIPInfoViewModel> = MeVIPInfoViewModel::class

    private val shaderEndColorMap: Map<VIPLevel, Int> =
        mapOf(
            VIPLevel.Copper to R.color.shader_end_copper,
            VIPLevel.Silver to R.color.shader_end_silver,
            VIPLevel.Gold to R.color.shader_end_gold,
            VIPLevel.Platinum to R.color.shader_end_platinum,
            VIPLevel.Diamond to R.color.shader_end_diamond,
            VIPLevel.BlackDiamond to R.color.shader_end_black_diamond,
            VIPLevel.StarDiamond to R.color.shader_end_star_diamond

        )

    private val foregroundResMap = mapOf(
        VIPLevel.Copper to R.drawable.bg_copper,
        VIPLevel.Silver to R.drawable.bg_silver,
        VIPLevel.Gold to R.drawable.bg_gold,
        VIPLevel.Platinum to R.drawable.bg_platinum,
        VIPLevel.Diamond to R.drawable.bg_diamond,
        VIPLevel.BlackDiamond to R.drawable.bg_black_diamond,
        VIPLevel.StarDiamond to R.drawable.bg_star_diamond
    )

    private val backgroundResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.bg_shape_copper,
            VIPLevel.Silver to R.drawable.bg_shape_silver,
            VIPLevel.Gold to R.drawable.bg_shape_gold,
            VIPLevel.Platinum to R.drawable.bg_shape_platinum,
            VIPLevel.Diamond to R.drawable.bg_shape_diamond,
            VIPLevel.BlackDiamond to R.drawable.bg_shape_black_diamond,
            VIPLevel.StarDiamond to R.drawable.bg_shape_black_diamond
        )

    private val percentResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.ic_percent_copper,
            VIPLevel.Silver to R.drawable.ic_percent_silver,
            VIPLevel.Gold to R.drawable.ic_percent_gold,
            VIPLevel.Platinum to R.drawable.ic_percent_platinum,
            VIPLevel.Diamond to R.drawable.ic_percent_diamond,
            VIPLevel.BlackDiamond to R.drawable.ic_percent_black_diamond,
            VIPLevel.StarDiamond to R.drawable.ic_percent_star_diamond

        )

    private val iconResMap = mapOf(
        VIPLevel.Copper to R.drawable.ic_level_copper,
        VIPLevel.Silver to R.drawable.ic_level_silver,
        VIPLevel.Gold to R.drawable.ic_level_gold,
        VIPLevel.Platinum to R.drawable.ic_level_platinum,
        VIPLevel.Diamond to R.drawable.ic_level_diamond,
        VIPLevel.BlackDiamond to R.drawable.ic_level_black_diamond,
        VIPLevel.StarDiamond to R.drawable.ic_level_star_diamond

    )

    private val levelResMap =
        mapOf(
            VIPLevel.Copper to R.drawable.ic_level_name_copper,
            VIPLevel.Silver to R.drawable.ic_level_name_silver,
            VIPLevel.Gold to R.drawable.ic_level_name_gold,
            VIPLevel.Platinum to R.drawable.ic_level_name_platinum,
            VIPLevel.Diamond to R.drawable.ic_level_name_diamond,
            VIPLevel.BlackDiamond to R.drawable.ic_level_name_black_diamond,
            VIPLevel.StarDiamond to R.drawable.ic_level_name_star_diamond

        )


    override fun initView(savedInstanceState: Bundle?) {

    }



    override fun initListener() {
        with(mBinding) {



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
                    mBinding.ivLevelName.setImageResource(
                        levelResMap.getValue(
                            vipLevel
                        )
                    )

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
                    mBinding.ivPercent.setImageResource(
                        percentResMap.getValue(
                            vipLevel
                        )
                    )
                    mBinding.tvLevelUpInfo.text = "升级还需¥59w投注额"

                    mBinding.tvBalance.text = "¥19901.00"
                }


            }
        }

        mViewModel.createObserver()
    }

    companion object {
        const val TAG = "VIPInfoFragment"
    }



}