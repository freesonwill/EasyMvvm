package com.cn.game.sdk2.ui.helper

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.cn.game.sdk.popup.MyGamePopupDialog
import com.cn.game.sdk2.R
import com.cn.game.sdk2.ui.Game1Fragment
import com.cn.game.sdk2.ui.HomeXPopupDialog
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 17:43
 **/
object ViewHelper {
    private const val TAG_FASTVIEW = "FastView"
    private var homeXPopupDialog: HomeXPopupDialog? = null

    fun showFastView(context: Context) {
        EasyFloat.with(context).setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setTag(TAG_FASTVIEW)
            .setGravity(Gravity.END, 0, 300)
            .setLayout(R.layout.drag_fast_easy) {
                val llFastClick = it.findViewById<LinearLayout>(R.id.llFastClick)
                llFastClick.setOnClickListener {
                    XPopup.Builder(context)
                        .hasShadowBg(false)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun onCreated(popupView: BasePopupView?) {
                                super.onCreated(popupView)
                            }

                            override fun onDismiss(popupView: BasePopupView?) {
                                super.onDismiss(popupView)
                                EasyFloat.show(TAG_FASTVIEW)
                                homeXPopupDialog = null
                            }
                        })
                        .popupAnimation(PopupAnimation.TranslateFromBottom)
                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .isViewMode(true)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .isThreeDrag(false) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                        .enableDrag(false)
                        .asCustom(MyGamePopupDialog(context))
//                        .asCustom(HomeXPopupDialog(context,LayoutInflater.from(context).inflate(R.layout.dialog_home_xpopup,null,false)).apply {
//                            homeXPopupDialog = this
//                            Game1Fragment().view
//                        }
//                        )
                        .show()
                    EasyFloat.hide(TAG_FASTVIEW)
                }
            }.show()
    }

}