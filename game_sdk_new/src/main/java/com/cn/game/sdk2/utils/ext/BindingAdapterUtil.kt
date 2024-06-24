package com.cn.game.sdk2.utils.ext

import androidx.databinding.BindingAdapter
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.GameAreaView.LocationClickListener
import com.cn.game.sdk2.websocket.bean.Betting

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/24 15:04
 **/
object BindingAdapterUtil {

    @BindingAdapter("areaInfo")
    @JvmStatic
    fun setAreaInfo(view: GameAreaView, areaInfo: Betting) {
        view.areaInfo = areaInfo
    }

    @BindingAdapter("moneyOKClickListener")
    @JvmStatic
    fun setMoneyOKClickListener(view: GameAreaView, listener: MoneyOKView.OnMoneyOKClickListener) {
        view.moneyView.setMoneyOKClickListener(listener)
    }

    @BindingAdapter("OnLocationClickListener")
    @JvmStatic
    fun setOnLocationClickListener(view: GameAreaView, listener: LocationClickListener) {
        view.setOnLocationClickListener(listener)
    }
}