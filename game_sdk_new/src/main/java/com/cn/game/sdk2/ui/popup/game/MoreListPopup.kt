package com.cn.game.sdk2.ui.popup.game

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.PopupCustomBubbleAttachBinding
import com.cn.game.sdk2.ui.animator.AlphaPopupAnimator
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.view.game.GameListView
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.runOnUiThread
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.utils.view.clickNoRepeat


/**
 * 首页的弹出框
 */
class MoreListPopup private constructor(content: Context) : AttachPopupView(content){

    companion object {
        private var instance: BasePopupView? = null

        fun create(context: Context, listener: OnMoreListPopupListener) {
            if (instance == null) {
                val bubbleAttach = MoreListPopup(context).apply {
                    setOnCustomBubbleAttachPopupListener(listener)
                }
                instance = XPopup.Builder(context)
                    .isTouchThrough(true)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun onDismiss(popupView: BasePopupView?) {
                            super.onDismiss(popupView)
                            bubbleAttach.dismiss()
                            instance = null
                        }
                    })
                    .customAnimator(AlphaPopupAnimator(bubbleAttach, 100, floatArrayOf(0f, 1f)))
                    .animationDuration(100)
                    .isDestroyOnDismiss(false)
                    .atView(listener.bindView())
                    .navigationBarColor(android.R.color.transparent)
                    .hasShadowBg(false) // 去掉半透明背景
                    .offsetX((-8).dp2px)
                    .offsetY((5).dp2px)
                    .asCustom(bubbleAttach)
                instance!!.show()
            } else {
                instance!!.dismiss()
            }
        }
    }

    private var listener: OnMoreListPopupListener? = null

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
                ViewHelper.showHelpDialog(context,true, listener?.setSecondPopHeight() ?: 0)
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

    private fun setOnCustomBubbleAttachPopupListener(listener: OnMoreListPopupListener) {
        this.listener = listener
    }

    interface OnMoreListPopupListener {
        fun bindView(): View
        fun setSecondPopHeight(): Int
    }
}