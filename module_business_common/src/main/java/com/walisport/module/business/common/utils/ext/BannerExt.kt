package com.walisport.module.business.common.utils.ext

import android.graphics.Color
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.CircleIndicator

/**
 * @date: 2026/1/9 14:55
 * @description: Banner 扩展
 */

fun Banner<*, *>.setGlobalBasicConfig():Banner<*, *> {
    val banner = this
    banner.setBannerRound(9.dp2px.toFloat())
    banner.isAutoLoop(true)
    // 设置滑动时长丝滑,不影响曲线,
    banner.setScrollTime(600)  // 0.6 秒
    banner.setLoopTime(5000)
    banner.setPageTransformer(CustomCurveTransformer())
    /*banner.setPageTransformer(CompositePageTransformer().apply {
        addTransformer(CustomCurveTransformer())
        addTransformer(MarginPageTransformer(8.dp2px))
    })*/
    return banner
}

fun Banner<*, *>.setGlobalIndicator():Banner<*, *> {
    val banner = this
    val width = 5.dp2px
    banner.setIndicator(CircleIndicator(banner.context)
        .apply { setBackgroundResource(arch.cayenne.lib.common.R.drawable.shape_1fffff_3px) })
        .setIndicatorSpace(0.dp2px)
        .setIndicatorNormalWidth(width)
        .setIndicatorSelectedWidth(width)
        .setIndicatorNormalColor(Color.TRANSPARENT)
        .setIndicatorSelectedColor(arch.cayenne.lib.common.R.color.color_D9D9D9.getColor())
        // 可选：指示器位置（居中、靠左、靠右）
        .setIndicatorGravity(IndicatorConfig.Direction.RIGHT)
        .setIndicatorMargins(IndicatorConfig.Margins(0, 0.dp2px, 8.dp2px, banner.height - 8.dp2px - width))
    return banner
}