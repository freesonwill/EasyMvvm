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
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
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

    // VIP 等級資源放置於 lib_common，統一使用 VIPResourceHelper 管理


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
                    // 使用 VIPResourceHelper 轉換等級
                    val vipLevel = VIPResourceHelper.getVIPLevelFromInt(it)

                    // 設置背景 - 使用 VIPResourceHelper
                    mBinding.ctVipInfo.background =
                        VIPResourceHelper.getBackgroundResource(vipLevel).getDrawable()
                    mBinding.ctLevelInfo.background =
                        VIPResourceHelper.getForegroundResource(vipLevel).getDrawable()

                    // 設置圖標 - 使用 VIPResourceHelper
                    mBinding.ivLevel.setImageResource(VIPResourceHelper.getIconResource(vipLevel))
                    mBinding.ivLevelName.setImageResource(
                        VIPResourceHelper.getLevelNameResource(
                            vipLevel
                        )
                    )

                    val bottom = 30.dp2px.toFloat()
                    // 创建线性渐变 - 使用 VIPResourceHelper
                    val linearGradient = LinearGradient(
                        0f, 0f,  // 渐变起点 (x1, y1)
                        0f, bottom,  // 渐变终点 (x2, y2)
                        intArrayOf(
                            VIPResourceHelper.getShaderStartColor().getColor(),
                            VIPResourceHelper.getShaderEndColor(vipLevel).getColor()
                        ),  // 渐变颜色数组
                        null,  // 渐变位置（null 表示均匀分布）
                        Shader.TileMode.CLAMP // 填充模式
                    )


                    mBinding.tvLevel.paint.shader = linearGradient
                    mBinding.tvLevel.text =
                        getString(arch.cayenne.lib.common.R.string.vip_level_format, it.toInt())

                    // 設置百分比 - 使用 VIPResourceHelper
                    mBinding.tvPercent.text = "57.91%"
                    mBinding.ivPercent.setImageResource(
                        VIPResourceHelper.getPercentResource(
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