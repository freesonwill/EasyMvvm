package com.cn.game.sdk2.ui.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.GAME_ID_ENUM
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.popup.HomeXPopupDialog
import com.cn.game.sdk2.ui.popup.fast3.Fast3HelpPopup
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.runOnUiThread
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import java.lang.ref.WeakReference

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 17:43
 **/
class ViewHelper {

    companion object {
        private const val TAG: String = "ViewHelper"
        val instance: ViewHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ViewHelper()
        }
    }

    private enum class ViewFloatType {
        FastView, FastViewOverlay, HomeXPopupDialog, HelpXPopupDialog
    }

    //弱引用防止view不能被回收
    private val viewHolderMap = mutableMapOf<ViewFloatType, WeakReference<View>>()

    private var homeXPopupDialog: BasePopupView?
        get() = viewHolderMap[ViewFloatType.HomeXPopupDialog]?.get() as BasePopupView?
        set(value) {
            viewHolderMap[ViewFloatType.HomeXPopupDialog] = WeakReference(value)
        }

    private var helpXPopupDialog: BasePopupView?
        get() = viewHolderMap[ViewFloatType.HelpXPopupDialog]?.get() as BasePopupView?
        set(value) {
            viewHolderMap[ViewFloatType.HelpXPopupDialog] = WeakReference(value)
        }

    private var fastView: View?
        get() = viewHolderMap[ViewFloatType.FastView]?.get()
        set(value) {
            viewHolderMap[ViewFloatType.FastView] = WeakReference(value)
        }

    private var fastViewOverlay: View?
        get() = viewHolderMap[ViewFloatType.FastViewOverlay]?.get()
        set(value) {
            viewHolderMap[ViewFloatType.FastViewOverlay] = WeakReference(value)
        }

    //是否显示其他pop
    var isShowOtherPop: Boolean = false

    /************************** Method *********************************************/

    /**
     * 显示帮助文档
     */
    fun showHelpDialog(context: Context, isShow: Boolean = true, pageHeight: Int) {
        if (helpXPopupDialog != null) {
            when {
                isShow && !helpXPopupDialog!!.isShow -> {
                    helpXPopupDialog!!.show()
                }

                !isShow && helpXPopupDialog!!.isShow -> {
                    helpXPopupDialog!!.dismiss()
                }
            }
            return
        }
        val (offsetY, height) = arrayOf(0, pageHeight)
        helpXPopupDialog = XPopup.Builder(context).isTouchThrough(false)
            .setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    helpXPopupDialog = null
                }
            })
            //.customAnimator(EmptyAnimator(bubbleAttach, 0))
            .navigationBarColor(android.R.color.transparent).hasShadowBg(false) // 去掉半透明背景
            .isViewMode(true).animationDuration(100).hasStatusBar(false).hasNavigationBar(false)
            .enableDrag(true).dismissOnTouchOutside(true)
            .asCustom(Fast3HelpPopup(context, offsetY, height)).apply {
                if (context is LifecycleOwner) { //宿主销毁了，静态引用置null
                    context.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            dismiss()
                            helpXPopupDialog = null
                        }
                    })
                }
            }.show()
    }

    private fun tryCreateMainPopup(context: Context){
        if(null == homeXPopupDialog){
            val pop = HomeXPopupDialog(context, Fast3MainFragment(), GAME_ID_ENUM.GAME_FAST3.num).apply {
                homeXPopupDialog = this
            }
            XPopup.Builder(context)
                .hasShadowBg(false)
                .setPopupCallback(object : SimpleCallback() {

                    override fun beforeShow(popupView: BasePopupView?) {
                        super.beforeShow(popupView)
                        fastViewOverlay?.isVisible = false
                        fastView?.isVisible = false
//                    appListener?.onGameFloatingDetailViewStatus(true)
                        appListener?.runOnUiThread {
                            onGameFloatingDetailViewStatus(true)
                        }
                        gameAboutModel.fast3MainFloatVisible.value = false
                    }

                    override fun onShow(popupView: BasePopupView?) {
                        super.onShow(popupView)
                    }

                    override fun onDismiss(popupView: BasePopupView?) {
                        super.onDismiss(popupView)
                        gameAboutModel.fast3MainFloatVisible.value = true
                        if (!isShowOtherPop) {
                            fastViewOverlay?.isVisible = true
                            fastView?.isVisible = true
//                        appListener?.onGameFloatingDetailViewStatus(false)
                            //homeXPopupDialog = null
                            appListener?.runOnUiThread {
                                onGameFloatingDetailViewStatus(false)
                            }
                        }
                    }
                })
                //.popupAnimation(PopupAnimation.TranslateFromBottom)
                .animationDuration(200)
                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                .isViewMode(true)
                .isTouchThrough(true)
                .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                .isThreeDrag(false) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                .enableDrag(true)
                .dismissOnTouchOutside(true)
                .asCustom(pop).apply {
                    //宿主销毁了，
                    if (context is LifecycleOwner) {
                        context.lifecycle.addObserver(object : DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                super.onDestroy(owner)
                                dismiss()
                                homeXPopupDialog = null
                            }
                        })
                    }
                }
        }
    }


    private fun showGameMainPopup(context: Context, isShow: Boolean) {
        tryCreateMainPopup(context)
        if (!isShow) {
            homeXPopupDialog?.dismiss()
            return
        }
        homeXPopupDialog!!.show()
    }

    fun showFastViewPopWhenWin(){
        homeXPopupDialog?.show()
    }

    fun getGameEnterView(context: Context): View {
        tryCreateMainPopup(context)
        if (fastView != null) return fastView!!
        return LayoutInflater.from(context).inflate(R.layout.drag_fast_easy, null, false).also {
            fastView = it
            val lp = ViewGroup.LayoutParams(0, 0)
            lp.width = 58.dp2px
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.layoutParams = lp
            val llFastClick = it.findViewById<LinearLayout>(R.id.llFastClick)
            llFastClick.clickNoRepeat(true) {
                if (homeXPopupDialog != null) {
                    LogUtils.dTag(TAG, "homeXPopupDialog exists, no need to create it.")
                    homeXPopupDialog!!.show()
                    return@clickNoRepeat
                }
                if (!gameAboutModel.isOpen || !gameAboutModel.isLoginSuccess.value!!) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.toast_login_fault),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@clickNoRepeat
                }
                if (!gameAboutModel.isEnterGameSuccess) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.toast_enter_game_fault),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@clickNoRepeat
                }
                showGameMainPopup(context, true)
            }
        }.apply {
            if (context is LifecycleOwner) {
                context.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        fastView = null
                    }
                })
            }
        }
    }

    fun getFastViewOverlay(context: Context): View {
        if (fastViewOverlay != null) return fastViewOverlay!!
        return LayoutInflater.from(context).inflate(R.layout.fragment_fast3_overlay, null, false)
            .also {
                val lp = ViewGroup.LayoutParams(0, 0)
                lp.width = 106.dp2px
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.layoutParams = lp
                fastViewOverlay = it
            }.apply {
                if (context is LifecycleOwner) {
                    context.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            fastViewOverlay = null
                        }
                    })
                }
            }
    }

    fun clearAllView() {
        fastView = null
        fastViewOverlay = null
        homeXPopupDialog = null
        helpXPopupDialog = null
        viewHolderMap.clear()
    }
}