package com.cn.game.sdk2.utils.ext

import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cn.game.sdk2.ui.view.MoneyOKView
import com.cn.game.sdk2.ui.view.game.GameAreaView
import com.cn.game.sdk2.ui.view.game.GameAreaView.LocationClickListener
import com.cn.game.sdk2.websocket.bean.Betting
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.dividerSpace


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/24 15:04
 **/
object BindingAdapterUtil {
    const val TAG = "BindingAdapterUtil"

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


    @JvmStatic
    @BindingAdapter(value = ["dividerSpace", "dividerOrientation"], requireAll = false)
    fun dividerSpace(recyclerView: RecyclerView, space: Int, orientation: Int?) {
        val or = when (orientation) {
            0 -> DividerOrientation.HORIZONTAL
            1 -> DividerOrientation.VERTICAL
            3 -> DividerOrientation.GRID
            else -> DividerOrientation.HORIZONTAL
        }
        recyclerView.dividerSpace(space, or)
    }

    @JvmStatic
    @BindingAdapter("layout_marginBottom")
    fun setLayoutMarginBottom(view: ConstraintLayout, margin: Int) {
        val lp = view.layoutParams as  RelativeLayout.LayoutParams
        lp.bottomMargin = margin
        view.layoutParams = lp
    }


}