package com.cn.game.sdk2.ui.popup.game

import android.content.Context
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.PopupCustomBubbleAttachBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.view.game.GameListView
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.runOnUiThread
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.xcjh.base_lib2.utils.view.clickNoRepeat


/**
 * 首页的弹出框
 */
class CustomBubbleAttachPopup(content: Context) : AttachPopupView(content){

    private var listener: OnCustomBubbleAttachPopupListener? = null

    override fun getImplLayoutId(): Int {
        return R.layout.popup_custom_bubble_attach
    }

    override fun onCreate() {
        super.onCreate()
        PopupCustomBubbleAttachBinding.bind(popupImplView).apply {
            rlPopClickRecords.isVisible = !gameAboutModel.simplifyMoreButtons
            rlPopClickService.isVisible = !gameAboutModel.simplifyMoreButtons
            rlPopClickRecords.clickNoRepeat(true) {
                delayDismiss(100)
                appListener?.runOnUiThread {
                    onHistoryOfBetAction()
                }
            }
            rlPopClickService.clickNoRepeat(true) {
                delayDismiss(100)
                appListener?.runOnUiThread {
                    onCustomerServiceAction()
                }
            }

            rlPopClickSwitchGame.clickNoRepeat(true) {
                delayDismiss(100)
                showGameList()
            }
            rlPopClickAssist.clickNoRepeat(true){
                delayDismiss(100)
                ViewHelper.showHelpDialog(context,true)
            }
        }
    }

    private fun showGameList() {
        val popupView = GameListView(context)
        popupView.targetHeight = listener?.setSecondPopHeight() ?: 0
        XPopup.Builder(context)
            .isTouchThrough(false)
            .popupAnimation(PopupAnimation.TranslateFromBottom)
            .navigationBarColor(android.R.color.transparent)
            .animationDuration(100)//默认300ms
            .isViewMode(true)
            .hasShadowBg(false) // 去掉半透明背景
            .enableDrag(true)
            .dismissOnTouchOutside(true)
            .asCustom(popupView)
            .show()
    }

    fun setOnCustomBubbleAttachPopupListener(listener: OnCustomBubbleAttachPopupListener) {
        this.listener = listener
    }

    interface OnCustomBubbleAttachPopupListener {
        fun setSecondPopHeight(): Int
    }
}