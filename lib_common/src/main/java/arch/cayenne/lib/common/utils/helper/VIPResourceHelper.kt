package arch.cayenne.lib.common.utils.helper

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.VIPLevel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable

/**
 * VIP 資源管理工具類
 * 統一管理 VIP 等級相關的資源映射
 * 12个VIP等级：青铜 白银 黄金 铂金 钻石 绿钻 红钻 黑钻 星钻 陨钻 星辰 宇宙
 */

object VIPResourceHelper {

    /**
     * 漸變開始顏色
     */
    @ColorRes
    fun getShaderStartColor(): Int {
        return R.color.shader_start
    }

    /**
     * VIP级别文字渐变結束顏色映射
     */
    @ColorRes
    fun getShaderEndColor(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.color.shader_end_copper
            VIPLevel.Silver -> R.color.shader_end_silver
            VIPLevel.Gold -> R.color.shader_end_gold
            VIPLevel.Platinum -> R.color.shader_end_platinum
            VIPLevel.Diamond -> R.color.shader_end_diamond
            VIPLevel.GreenDiamond -> R.color.shader_end_green_diamond
            VIPLevel.RedDiamond -> R.color.shader_end_red_diamond
            VIPLevel.BlackDiamond -> R.color.shader_end_black_diamond
            VIPLevel.StarDiamond -> R.color.shader_end_star_diamond
            VIPLevel.MeteoriteDiamond -> R.color.shader_end_meteorite_diamond
            VIPLevel.Stars -> R.color.shader_end_star
            VIPLevel.Universe -> R.color.shader_end_universe
        }
    }

    /**
     * 前景背景資源映射
     */
    fun getForegroundResource(level: VIPLevel): Drawable {
        return when (level) {
            VIPLevel.Copper -> R.drawable.bg_copper.getDrawable()
            VIPLevel.Silver -> R.drawable.bg_silver.getDrawable()
            VIPLevel.Gold -> R.drawable.bg_gold.getDrawable()
            VIPLevel.Platinum -> R.drawable.bg_platinum.getDrawable()
            VIPLevel.Diamond -> R.drawable.bg_diamond.getDrawable()
            VIPLevel.GreenDiamond -> R.drawable.bg_green_diamond.getDrawable()
            VIPLevel.RedDiamond -> R.drawable.bg_red_diamond.getDrawable()
            VIPLevel.BlackDiamond -> R.drawable.bg_black_diamond.getDrawable()
            VIPLevel.StarDiamond -> R.drawable.bg_star_diamond.getDrawable()
            VIPLevel.MeteoriteDiamond -> R.drawable.bg_meteorite_diamond.getDrawable()
            VIPLevel.Stars -> R.drawable.bg_star.getDrawable()
            VIPLevel.Universe -> R.drawable.bg_universe.getDrawable()
        }
    }

    /**
     * 体育面板下VIP区域背景資源映射
     */
    fun getSportBackgroundResource(level: VIPLevel): Drawable {
        return when (level) {
            VIPLevel.Copper -> R.drawable.bg_sport_copper.getDrawable()
            VIPLevel.Silver -> R.drawable.bg_sport_silver.getDrawable()
            VIPLevel.Gold -> R.drawable.bg_sport_gold.getDrawable()
            VIPLevel.Platinum -> R.drawable.bg_sport_platinum.getDrawable()
            VIPLevel.Diamond -> R.drawable.bg_sport_diamond.getDrawable()
            VIPLevel.GreenDiamond -> R.drawable.bg_sport_green_diamond.getDrawable()
            VIPLevel.RedDiamond -> R.drawable.bg_sport_red_diamond.getDrawable()
            VIPLevel.BlackDiamond -> R.drawable.bg_sport_black_diamond.getDrawable()
            VIPLevel.StarDiamond -> R.drawable.bg_sport_star_diamond.getDrawable()
            VIPLevel.MeteoriteDiamond -> R.drawable.bg_sport_meteorite_diamond.getDrawable()
            VIPLevel.Stars -> R.drawable.bg_sport_star.getDrawable()
            VIPLevel.Universe -> R.drawable.bg_sport_universe.getDrawable()
        }
    }

    /**
     * VIP信息区域背景颜色
     */
    fun getBackgroundResource(level: VIPLevel): Drawable {
        return when (level) {
            VIPLevel.Copper -> R.drawable.bg_shape_copper.getDrawable()
            VIPLevel.Silver -> R.drawable.bg_shape_silver.getDrawable()
            VIPLevel.Gold -> R.drawable.bg_shape_gold.getDrawable()
            VIPLevel.Platinum -> R.drawable.bg_shape_platinum.getDrawable()
            VIPLevel.Diamond -> R.drawable.bg_shape_diamond.getDrawable()
            VIPLevel.GreenDiamond -> R.drawable.bg_shape_green_diamond.getDrawable()
            VIPLevel.RedDiamond -> R.drawable.bg_shape_red_diamond.getDrawable()
            VIPLevel.BlackDiamond -> R.drawable.bg_shape_black_diamond.getDrawable()
            VIPLevel.StarDiamond -> R.drawable.bg_shape_star_diamond.getDrawable()
            VIPLevel.MeteoriteDiamond -> R.drawable.bg_shape_meteorite_diamond.getDrawable()
            VIPLevel.Stars -> R.drawable.bg_shape_star.getDrawable()
            VIPLevel.Universe -> R.drawable.bg_shape_universe.getDrawable()
        }
    }

    /**
     * 根据VIP级别来设置投注额渐变进度条的开始颜色
     */
    fun getProgressStartColor(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.color.shader_start_copper.getColor()
            VIPLevel.Silver -> R.color.shader_start_silver.getColor()
            VIPLevel.Gold -> R.color.shader_start_gold.getColor()
            VIPLevel.Platinum -> R.color.shader_start_platinum.getColor()
            VIPLevel.Diamond -> R.color.shader_start_diamond.getColor()
            VIPLevel.GreenDiamond -> R.color.shader_start_green_diamond.getColor()
            VIPLevel.RedDiamond -> R.color.shader_start_red_diamond.getColor()
            VIPLevel.BlackDiamond -> R.color.shader_start_black_diamond.getColor()
            VIPLevel.StarDiamond -> R.color.shader_start_star_diamond.getColor()
            VIPLevel.MeteoriteDiamond -> R.color.shader_start_meteorite_diamond.getColor()
            VIPLevel.Stars -> R.color.shader_start_star.getColor()
            VIPLevel.Universe -> R.color.shader_start_universe.getColor()
        }
    }

    /**
     * 百分比進度條資源映射
     */
    @DrawableRes
    fun getPercentResource(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.drawable.ic_percent_copper
            VIPLevel.Silver -> R.drawable.ic_percent_silver
            VIPLevel.Gold -> R.drawable.ic_percent_gold
            VIPLevel.Platinum -> R.drawable.ic_percent_platinum
            VIPLevel.Diamond -> R.drawable.ic_percent_diamond
            VIPLevel.BlackDiamond -> R.drawable.ic_percent_black_diamond
            VIPLevel.StarDiamond -> R.drawable.ic_percent_star_diamond
            else -> R.drawable.ic_percent_copper
        }
    }

    /**
     * VIP 等級圖標資源映射
     */
    @DrawableRes
    fun getIconResource(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.drawable.ic_level_copper
            VIPLevel.Silver -> R.drawable.ic_level_silver
            VIPLevel.Gold -> R.drawable.ic_level_gold
            VIPLevel.Platinum -> R.drawable.ic_level_platinum
            VIPLevel.Diamond -> R.drawable.ic_level_diamond
            VIPLevel.GreenDiamond -> R.drawable.ic_level_green_diamond
            VIPLevel.RedDiamond -> R.drawable.ic_level_red_diamond
            VIPLevel.BlackDiamond -> R.drawable.ic_level_black_diamond
            VIPLevel.StarDiamond -> R.drawable.ic_level_star_diamond
            VIPLevel.MeteoriteDiamond -> R.drawable.ic_level_meteorite_diamond
            VIPLevel.Stars -> R.drawable.ic_level_star
            VIPLevel.Universe -> R.drawable.ic_level_universe
        }
    }

    /**
     * VIP 等級名稱資源映射
     */
    @DrawableRes
    fun getLevelNameResource(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.drawable.ic_level_name_copper
            VIPLevel.Silver -> R.drawable.ic_level_name_silver
            VIPLevel.Gold -> R.drawable.ic_level_name_gold
            VIPLevel.Platinum -> R.drawable.ic_level_name_platinum
            VIPLevel.Diamond -> R.drawable.ic_level_name_diamond
            VIPLevel.GreenDiamond -> R.drawable.ic_level_name_green_diamond
            VIPLevel.RedDiamond -> R.drawable.ic_level_name_red_diamond
            VIPLevel.BlackDiamond -> R.drawable.ic_level_name_black_diamond
            VIPLevel.StarDiamond -> R.drawable.ic_level_name_star_diamond
            VIPLevel.MeteoriteDiamond -> R.drawable.ic_level_name_meteorite_diamond
            VIPLevel.Stars -> R.drawable.ic_level_name_star
            VIPLevel.Universe -> R.drawable.ic_level_name_universe
        }
    }

    /**
     * 根據 VIP 等級數字轉換為對應的 VIPLevel enum
     */
    fun getVIPLevelFromInt(level: Int): VIPLevel {
        return VIPLevel.fromLevel(level)
    }

    /**
     * 根據 VIP 等級數字（Long）轉換為對應的 VIPLevel enum
     */
    fun getVIPLevelFromInt(level: Long): VIPLevel {
        return VIPLevel.fromLevel(level.toInt())
    }
}

