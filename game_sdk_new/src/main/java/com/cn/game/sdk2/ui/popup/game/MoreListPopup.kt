package com.cn.game.sdk2.ui.popup.game

import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.PopupCustomBubbleAttachBinding
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.ThreadUtils.appListenerScope
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.utils.view.clickNoRepeat


/**
 * 首页的弹出框
 */
class MoreListPopup private constructor(content: Context) : AttachPopupView(content) {

    companion object {
        private const val TAG = "MoreListPopup"
        private var instance: BasePopupView? = null
        fun create(
            context: Context,
            listener: OnMoreListPopupListener,
            clickPivot: Point
        ) {
            val clickX = clickPivot.x.toFloat()
            val clickY = clickPivot.y.toFloat()
            if (instance == null) {
                val bubbleAttach = MoreListPopup(context).apply {
                    setOnCustomBubbleAttachPopupListener(listener)
                }
                instance = XPopup.Builder(context)
                    .isTouchThrough(true)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun beforeShow(popupView: BasePopupView?) {
                            super.beforeShow(popupView)
                            bubbleAttach.apply {
                                scaleX = 0f
                                scaleY = 0f
                                pivotX = clickX
                                pivotY = clickY
                                animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setInterpolator(LinearInterpolator())
                                    .setDuration(150L)
                                    .start()
                            }
                        }

                        override fun beforeDismiss(popupView: BasePopupView?) {
                            super.beforeDismiss(popupView)
                            bubbleAttach.animate().cancel()
                            bubbleAttach.apply {
                                pivotX = clickX
                                pivotY = clickY
                                animate()
                                    .alpha(0f)
                                    .scaleX(0f)
                                    .scaleY(0f)
                                    .setInterpolator(LinearInterpolator())
                                    .setDuration(150L)
                                    .start()
                            }
                        }

                        override fun onDismiss(popupView: BasePopupView?) {
                            super.onDismiss(popupView)
                            bubbleAttach.dismiss()
                            instance = null
                        }
                    })
                    .animationDuration(200)
                    .isDestroyOnDismiss(false)
                    .atView(listener.bindView())
                    .navigationBarColor(android.R.color.transparent)
                    .hasShadowBg(false) // 去掉半透明背景
                    .offsetX((-6).dp2px)
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
                appListenerScope.launchWithCustomContext(TAG) {
                    appListener?.onHistoryOfBetAction()
                }
            }
            rlPopClickService.clickNoRepeat(true) {
                delayDismiss(100)
                appListenerScope.launchWithCustomContext(TAG) {
                    appListener?.onCustomerServiceAction()
                }
            }

            rlPopClickSwitchGame.clickNoRepeat(true) {
                delayDismiss(100)
                ViewHelper.instance.showGameList(context,listener!!.getFragmentManager(),listener!!.getSecondPopHeight())
            }
            rlPopClickAssist.clickNoRepeat(true){
                delayDismiss(100)
                ViewHelper.instance.showHelpDialog(context,true, listener?.getSecondPopHeight() ?: 0)
            }
        }
    }



    private fun setOnCustomBubbleAttachPopupListener(listener: OnMoreListPopupListener) {
        this.listener = listener
    }

    interface OnMoreListPopupListener {
        fun getFragmentManager(): FragmentManager
        fun bindView(): View
        fun getSecondPopHeight(): Int
    }
}