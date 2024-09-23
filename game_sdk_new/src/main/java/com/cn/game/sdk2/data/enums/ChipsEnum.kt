package com.cn.game.sdk2.data.enums

import com.cn.game.sdk2.R

/**
 * 統一管理chips的點數 & 資源圖
 */
enum class ChipsEnum(val money: Int, private val noValueRes: Int, val enableRes: Int, val disableRes: Int, val selectedRes: Int) {
    SHI(1000, noValueRes = R.mipmap.game_sdk_icon_ok_shi, enableRes = R.mipmap.game_sdk_icon_no_shi, disableRes = R.mipmap.game_sdk_icon_shortage_shi, selectedRes = R.mipmap.game_sdk_icon_select_shi),
    WU_SHO(5000, noValueRes = R.mipmap.game_sdk_icon_ok_wushi, enableRes = R.mipmap.game_sdk_icon_no_wushi, disableRes = R.mipmap.game_sdk_icon_shortage_wushi, selectedRes = R.mipmap.game_sdk_icon_select_wushi),
    YI_BAI(10000, noValueRes = R.mipmap.game_sdk_icon_ok_yibai, enableRes = R.mipmap.game_sdk_icon_no_yibai, disableRes = R.mipmap.game_sdk_icon_shortage_yibai, selectedRes = R.mipmap.game_sdk_icon_select_yibai),
    LIANG_BAI(20000, noValueRes = R.mipmap.game_sdk_icon_ok_liangbai, enableRes = R.mipmap.game_sdk_icon_no_liangbai, disableRes = R.mipmap.game_sdk_icon_shortage_liangbai, selectedRes = R.mipmap.game_sdk_icon_select_liangbai),
    WU_BAI(50000, noValueRes = R.mipmap.game_sdk_icon_ok_wubai, enableRes = R.mipmap.game_sdk_icon_no_wubai, disableRes = R.mipmap.game_sdk_icon_shortage_wubai, selectedRes = R.mipmap.game_sdk_icon_select_wubai),
    QIAN(100000, noValueRes = R.mipmap.game_sdk_icon_ok_qian, enableRes = R.mipmap.game_sdk_icon_no_qian, disableRes = R.mipmap.game_sdk_icon_shortage_qian, selectedRes = R.mipmap.game_sdk_icon_select_qian),
    LIANG_QIAN(200000, noValueRes = R.mipmap.game_sdk_icon_ok_liangqian, enableRes = R.mipmap.game_sdk_icon_no_liangqian, disableRes = R.mipmap.game_sdk_icon_shortage_liangqian, selectedRes = R.mipmap.game_sdk_icon_select_liangqian),
    WU_QIAN(500000, noValueRes = R.mipmap.game_sdk_icon_ok_wuqian, enableRes = R.mipmap.game_sdk_icon_no_wuqian, disableRes = R.mipmap.game_sdk_icon_shortage_wuqian, selectedRes = R.mipmap.game_sdk_icon_select_wuqian),
    YI_WAN(1000000, noValueRes = R.mipmap.game_sdk_icon_ok_yiwan, enableRes = R.mipmap.game_sdk_icon_no_yiwan, disableRes = R.mipmap.game_sdk_icon_shortage_yiwan, selectedRes = R.mipmap.game_sdk_icon_select_yiwan),
    LIANG_WAN(2000000, noValueRes = R.mipmap.game_sdk_icon_ok_liangwan, enableRes = R.mipmap.game_sdk_icon_no_liangwan, disableRes = R.mipmap.game_sdk_icon_shortage_liangwan, selectedRes = R.mipmap.game_sdk_icon_select_liangwan),
    WU_WAN(5000000, noValueRes = R.mipmap.game_sdk_icon_ok_wuwan, enableRes = R.mipmap.game_sdk_icon_no_wuwan, disableRes = R.mipmap.game_sdk_icon_shortage_wuwan, selectedRes = R.mipmap.game_sdk_icon_select_wuwan),
    SHI_WAN(10000000, noValueRes = R.mipmap.game_sdk_icon_ok_shiwan, enableRes = R.mipmap.game_sdk_icon_no_shiwan, disableRes = R.mipmap.game_sdk_icon_shortage_shiwan, selectedRes = R.mipmap.game_sdk_icon_select_shiwan);

    companion object {
        fun getNoValueResByMoney(money: Int): Int {
            return when(money) {
                in SHI.money until WU_SHO.money -> SHI.noValueRes
                in WU_SHO.money until YI_BAI.money -> WU_SHO.noValueRes
                in YI_BAI.money until LIANG_BAI.money -> YI_BAI.noValueRes
                in LIANG_BAI.money until WU_BAI.money -> LIANG_BAI.noValueRes
                in WU_BAI.money until QIAN.money -> WU_BAI.noValueRes
                in QIAN.money until LIANG_QIAN.money -> QIAN.noValueRes
                in LIANG_QIAN.money until WU_QIAN.money -> LIANG_QIAN.noValueRes
                in WU_QIAN.money until YI_WAN.money -> WU_QIAN.noValueRes
                in YI_WAN.money until LIANG_WAN.money -> YI_WAN.noValueRes
                in LIANG_WAN.money until WU_WAN.money -> LIANG_WAN.noValueRes
                in WU_WAN.money until SHI_WAN.money -> WU_WAN.noValueRes
                else -> SHI_WAN.noValueRes
            }
        }

        fun createChipsList(): List<ChipBean> {
            return values().map { ChipBean(it) }
        }
    }
}

data class ChipBean(
    val chip: ChipsEnum,
    var isSelected: Boolean = false
)