package com.walisport.module.me.ui.fragment

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.me.R
import com.walisport.module.me.data.constants.VIPLevel
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

    private val shaderEndColor: Map<VIPLevel, Int> =
        mapOf(VIPLevel.BlackDiamond to R.color.shader_end_black_diamond)

    private val foregroundDrawable = mapOf(VIPLevel.BlackDiamond to R.drawable.bg_black_diamond)

    private val backgroundDrawable =
        mapOf(VIPLevel.BlackDiamond to R.drawable.bg_shape_black_diamond)

    private val iconDrawable = mapOf(VIPLevel.BlackDiamond to R.drawable.ic_level_black_diamond)

    private val levelDrawable =
        mapOf(VIPLevel.BlackDiamond to R.drawable.ic_level_name_black_diamond)

    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {
            root.touchBackPressed()

            tvNickname.text = "中文sdf323"
            val day = 137
            tvJoinTime.text = "已加入${day}天"


        }
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
                        backgroundDrawable.getValue(vipLevel).getDrawable()
                    mBinding.ctLevelInfo.background =
                        foregroundDrawable.getValue(vipLevel).getDrawable()

                    mBinding.ivLevel.setImageResource(iconDrawable.getValue(vipLevel))
                    mBinding.ivLevelName.setImageResource(levelDrawable.getValue(vipLevel))

                    val bottom = 30.dp2px.toFloat()
                    // 创建线性渐变
                    val linearGradient = LinearGradient(
                        0f, 0f,  // 渐变起点 (x1, y1)
                        0f, bottom,  // 渐变终点 (x2, y2)
                        intArrayOf(
                            R.color.shader_start.getColor(),
                            shaderEndColor.getValue(vipLevel).getColor()
                        ),  // 渐变颜色数组
                        null,  // 渐变位置（null 表示均匀分布）
                        Shader.TileMode.CLAMP // 填充模式
                    )


                    mBinding.tvLevel.paint.shader = linearGradient
                    mBinding.tvLevel.text = "VIP ${it}"

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