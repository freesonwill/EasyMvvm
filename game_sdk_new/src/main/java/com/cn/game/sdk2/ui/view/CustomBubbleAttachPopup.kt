package com.cn.game.sdk2.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.DialogHomeXpopupContainerBinding
import com.cn.game.sdk2.databinding.PopupCustomBubbleAttachBinding
import com.cn.game.sdk2.ui.HomeXPopupDialog.Companion.TAG
import com.cn.game.sdk2.ui.fast3.Fast3GameHallFragment
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.animator.EmptyAnimator
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.BubbleAttachPopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lxj.xpopup.util.XPopupUtils
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 首页的弹出框
 */
class CustomBubbleAttachPopup(content: Context) : AttachPopupView(content){

    override fun getImplLayoutId(): Int {
        return R.layout.popup_custom_bubble_attach
    }

    override fun onCreate() {
        super.onCreate()
     /*   setBubbleBgColor(ContextCompat.getColor(context,R.color.c_ffffff))
        setBubbleShadowSize(2)
        setArrowWidth(XPopupUtils.dp2px(context, 8f))
        setArrowHeight(XPopupUtils.dp2px(context, 8f)).setBubbleRadius(10)
        setArrowRadius(XPopupUtils.dp2px(context, 2f))*/
        PopupCustomBubbleAttachBinding.bind(popupImplView).apply {
            rlPopClickRecords.clickNoRepeat() {
                PromptSoundPlay.btnPlayMedia()
                delayDismiss(100)
            }
            rlPopClickService.clickNoRepeat() {
                PromptSoundPlay.btnPlayMedia()
                delayDismiss(100)
            }

            rlPopClickSwitchGame.clickNoRepeat(0) {
                PromptSoundPlay.btnPlayMedia()
                delayDismiss(100)
                customBubbleAttachListener?.switchGame()
                switchGame()
            }
            rlPopClickAssist.clickNoRepeat() {
                PromptSoundPlay.btnPlayMedia()
                delayDismiss(100)
                ViewHelper.showHelpDialog(context,true)
            }
        }
    }

    private fun switchGame(){
        val popupView = object :BottomPopupView(context) {
            //override fun getImplLayoutId(): Int  = R.layout.fragment_gamehall
            override fun getImplLayoutId(): Int  = R.layout.dialog_home_xpopup_container

            var binding: DialogHomeXpopupContainerBinding? = null
            val fragment = Fast3GameHallFragment()

            override fun onCreate() {
                super.onCreate()
                binding = DialogHomeXpopupContainerBinding.bind(popupImplView)
                val transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.fl_container, fragment).commit()
                Log.d(TAG, "onCreate")
            }

            private val fragmentManager
                get() = run {
                    when (context) {
                        is FragmentActivity -> (context as FragmentActivity).supportFragmentManager
                        is ContextWrapper -> (((context as ContextWrapper).baseContext) as Fragment).childFragmentManager
                        else -> throw IllegalStateException("illegal context:$context ,type:${context.javaClass.simpleName}")
                    }
                }

        }
        XPopup.Builder(context)
        .isTouchThrough(true)
        .setPopupCallback(object : SimpleCallback() {
            override fun onCreated(popupView: BasePopupView?) {
                super.onCreated(popupView)
            }
            override fun onDismiss(popupView: BasePopupView?) {
                super.onDismiss(popupView)
            }
        })
        .popupAnimation(PopupAnimation.TranslateFromBottom)
        .navigationBarColor(android.R.color.transparent)
        //.isViewMode(true)
        .hasShadowBg(false) // 去掉半透明背景
        .enableDrag(false)
        .dismissOnTouchOutside(false)
        .asCustom(popupView)
        .show()
    }

    var customBubbleAttachListener: CustomBubbleAttachListener?=null

    interface  CustomBubbleAttachListener{
        /**
         * 切换游戏
         */
        fun  switchGame()
    }

}