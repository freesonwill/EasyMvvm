package arch.cayenne.lib.common.utils.helper

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.VIPLevel

/**
 * VIP 資源管理工具類
 * 統一管理 VIP 等級相關的資源映射
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
     * 漸變結束顏色映射
     */
    @ColorRes
    fun getShaderEndColor(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.color.shader_end_copper
            VIPLevel.Silver -> R.color.shader_end_silver
            VIPLevel.Gold -> R.color.shader_end_gold
            VIPLevel.Platinum -> R.color.shader_end_platinum
            VIPLevel.Diamond -> R.color.shader_end_diamond
            VIPLevel.BlackDiamond -> R.color.shader_end_black_diamond
            VIPLevel.StarDiamond -> R.color.shader_end_star_diamond
            else -> R.color.shader_end_copper
        }
    }

    /**
     * 前景背景資源映射
     */
    @DrawableRes
    fun getForegroundResource(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.drawable.bg_copper
            VIPLevel.Silver -> R.drawable.bg_silver
            VIPLevel.Gold -> R.drawable.bg_gold
            VIPLevel.Platinum -> R.drawable.bg_platinum
            VIPLevel.Diamond -> R.drawable.bg_diamond
            VIPLevel.BlackDiamond -> R.drawable.bg_black_diamond
            VIPLevel.StarDiamond -> R.drawable.bg_star_diamond
            else -> R.drawable.bg_copper
        }
    }

    /**
     * Shape 背景資源映射
     */
    @DrawableRes
    fun getBackgroundResource(level: VIPLevel): Int {
        return when (level) {
            VIPLevel.Copper -> R.drawable.bg_shape_copper
            VIPLevel.Silver -> R.drawable.bg_shape_silver
            VIPLevel.Gold -> R.drawable.bg_shape_gold
            VIPLevel.Platinum -> R.drawable.bg_shape_platinum
            VIPLevel.Diamond -> R.drawable.bg_shape_diamond
            VIPLevel.BlackDiamond -> R.drawable.bg_shape_black_diamond
            VIPLevel.StarDiamond -> R.drawable.bg_shape_star_diamond
            else -> R.drawable.bg_shape_copper
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
            VIPLevel.BlackDiamond -> R.drawable.ic_level_black_diamond
            VIPLevel.StarDiamond -> R.drawable.ic_level_star_diamond
            else -> R.drawable.ic_level_copper
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
            VIPLevel.BlackDiamond -> R.drawable.ic_level_name_black_diamond
            VIPLevel.StarDiamond -> R.drawable.ic_level_name_star_diamond
            else -> R.drawable.ic_level_name_copper
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

