package com.walisport.module.me.ui.fragment

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.VIPResourceHelper
import com.walisport.module.me.databinding.FragmentMeVipInfoBinding
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import kotlin.reflect.KClass

/**
 * 我的界面VIP级别信息区域
 */

class MeVIPInfoFragment : BaseFragment<MeVIPInfoViewModel, FragmentMeVipInfoBinding>() {

    override val vbClass: KClass<FragmentMeVipInfoBinding> = FragmentMeVipInfoBinding::class
    override val vmClass: KClass<MeVIPInfoViewModel> = MeVIPInfoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData() {
        super.initData()
        mViewModel.getAccountInfo()
    }

    override fun initListener() {
        with(mBinding) {
            root.clickNoRepeat {
                navigate(arch.cayenne.lib.res.R.string.nav_module_vip_fragment.deeplink())
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

    @SuppressLint("DefaultLocale")
    override suspend fun createObserver() {
        with(mViewModel) {
            onVipListener.observe(viewLifecycleOwner) {
                if (it != null) {
                    var percent = "0%"
                    var progress = 0f
                    val betScore = it.admittedBetScore.toFloat()
                    val reqScore = it.requiredAdmittedBetScore.toFloat()
                    val require = it.requiredAdmittedBetScore.toInt()
                    if (reqScore > 0L && betScore > 0L) {
                        progress = (betScore / reqScore) * 100f
                        percent = String.format("%.2f", progress) + "%"
                    }
                    val cny = CurrencySymbols.getSymbol(it.ccy) + require
                    val info = getString(arch.cayenne.lib.common.R.string.vip_level_require, cny)
                    updateVIPInfo(
                        vipLevel = it.vipLevel,
                        vipStage = it.vipStage,
                        percent = percent,
                        levelUpInfo = info,
                        progress
                    )
                }
            }
            launch {
                balanceFlow.collect {
                    mBinding.tvBalance.text = it
                }
            }
        }
    }

    private fun updateVIPInfo(
        vipLevel: Int,
        vipStage: Int,
        percent: String,
        levelUpInfo: String,
        progress: Float
    ) {
        val level = VIPResourceHelper.getVIPLevelFromInt(vipStage)
        with(mBinding) {
            ctVipInfo.background = VIPResourceHelper.getBackgroundResource(level)
            ctLevelInfo.background = VIPResourceHelper.getForegroundResource(level)
            ivLevel.setImageResource(VIPResourceHelper.getIconResource(level))
            ivLevelName.setImageResource(VIPResourceHelper.getLevelNameResource(level))
            val bottom = 20.dp2px.toFloat()
            val linearGradient = LinearGradient(
                0f, 0f,
                0f, bottom,
                intArrayOf(
                    VIPResourceHelper.getShaderStartColor().getColor(requireContext()),
                    VIPResourceHelper.getShaderEndColor(level).getColor(requireContext())
                ),
                null,
                Shader.TileMode.CLAMP
            )
            tvLevel.paint.shader = linearGradient
            tvLevel.text = getString(arch.cayenne.lib.common.R.string.vip_level_format, vipLevel)
            tvPercent.text = percent
            val color = VIPResourceHelper.getProgressStartColor(level)
            vipProgress.setProgressColor(color)
            vipProgress.setProgress(progress)
            tvLevelUpInfo.text = levelUpInfo
        }
    }

    companion object {
        const val TAG = "VIPInfoFragment"
    }
}