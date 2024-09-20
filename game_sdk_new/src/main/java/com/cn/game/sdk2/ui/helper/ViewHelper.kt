package com.cn.game.sdk2.ui.helper

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.doOnDetach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.GAME_ID_ENUM
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.popup.HomeXPopupDialog
import com.cn.game.sdk2.ui.popup.fast3.Fast3HelpPopup
import com.cn.game.sdk2.ui.view.game.GameListView
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.utils.ThreadUtils.appListenerScope
import com.cn.game.sdk2.utils.ThreadUtils.launchWithCustomContext
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.view.clickNoRepeat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.ref.WeakReference

/**
 * Description: View辅助类
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

    private var homeXPopupDialog: HomeXPopupDialog?
        get() = viewHolderMap[ViewFloatType.HomeXPopupDialog]?.get() as HomeXPopupDialog?
        set(value) {
            viewHolderMap[ViewFloatType.HomeXPopupDialog] = WeakReference(value)
        }

    private var helpXPopupDialog: BasePopupView?
        get() = viewHolderMap[ViewFloatType.HelpXPopupDialog]?.get() as BasePopupView?
        set(value) {
            viewHolderMap[ViewFloatType.HelpXPopupDialog] = WeakReference(value)
        }
    private var gameListDialog:GameListView? = null

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

                override fun beforeDismiss(popupView: BasePopupView?) {
                    isShowOtherPop = false
                    showGameMainPopup(context, true)
                    super.beforeDismiss(popupView)
                }

                override fun beforeShow(popupView: BasePopupView?) {
                    isShowOtherPop = true
                    showGameMainPopup(context, false)
                    super.beforeShow(popupView)
                }
            })
            //.customAnimator(EmptyAnimator(bubbleAttach, 0))
            .navigationBarColor(android.R.color.transparent)
            .hasShadowBg(false) // 去掉半透明背景
            .isViewMode(true)
            .animationDuration(150)
            .hasStatusBar(false)
            .hasNavigationBar(false)
            .enableDrag(true)
            .dismissOnTouchOutside(true)
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

    private fun tryCreateMainPopup(context: Context, animationDuration: Int = 200) {
        if (null == homeXPopupDialog) {
            val pop = object :HomeXPopupDialog(context, Fast3MainFragment(), GAME_ID_ENUM.GAME_FAST3.num) {
                override fun onOpening() {
                    if(!isShowOtherPop) {
                        fastViewOverlay?.let { if(it.alpha == 1f) fadeOut(it) }
                        fastView?.let { if(it.alpha == 1f) fadeOut(it) }
                    }
                }
                override fun onClosing() {
                    if (!isShowOtherPop) {
                        fastViewOverlay?.let { if(it.alpha == 0f) fadeIn(it) }
                        fastView?.let { if(it.alpha == 0f) fadeIn(it) }
                    }
                }
            }.apply {
                homeXPopupDialog = this
            }
            XPopup.Builder(context)
                .hasShadowBg(false)
                .setPopupCallback(object : SimpleCallback() {
                    override fun beforeShow(popupView: BasePopupView?) {
                        super.beforeShow(popupView)
                        if(!isShowOtherPop) {
                            appListenerScope.launchWithCustomContext(TAG) {
                                appListener?.onGameFloatingDetailViewStatus(true)
                            }
                        }
                        gameAboutModel.fast3MainFloatVisible.value = false
                    }

                    override fun onDismiss(popupView: BasePopupView?) {
                        super.onDismiss(popupView)
                        gameAboutModel.fast3MainFloatVisible.value = true
                        if (!isShowOtherPop) {
                            appListenerScope.launchWithCustomContext(TAG) {
                                appListener?.onGameFloatingDetailViewStatus(false)
                            }
                        }
                    }
                })
                .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                .animationDuration(animationDuration)
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

    /**
     * 展示游戏大厅
     * @param context 上下文
     * @param fm FragmentManager
     * @param targetHeight view高度
     * @param miniGameId 游戏id
     */
    fun showGameList(
        context: Context,
        fm: FragmentManager,
        targetHeight: Int,
        miniGameId: Int? = null
    ) {
        if(gameListDialog != null){
            gameListDialog?.switchPage(miniGameId)
            return
        }
        val popupView = GameListView(context, fm = fm, miniGameId=miniGameId)
        popupView.targetHeight = targetHeight
        XPopup.Builder(context)
            .isTouchThrough(false)
            .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
            .navigationBarColor(android.R.color.transparent)
            .animationDuration(150)//默认300ms
            .isViewMode(true)
            .hasShadowBg(false) // 去掉半透明背景
            .enableDrag(true)
            .dismissOnTouchOutside(true)
            .setPopupCallback(object: SimpleCallback() {
                override fun beforeDismiss(popupView: BasePopupView?) {
                    isShowOtherPop = false
                    showGameMainPopup(context, true)
                    super.beforeDismiss(popupView)
                }

                override fun beforeShow(popupView: BasePopupView?) {
                    isShowOtherPop = true
                    showGameMainPopup(context, false)
                    super.beforeShow(popupView)
                }
            })
            .asCustom(popupView)
            .show()
        gameListDialog = popupView
        popupView.doOnDetach {
            gameListDialog = null
            LogUtils.eTag(TAG, "gameListDialog set null")
        }
    }

    private fun fadeIn(view: View) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {
            val alpha = it.animatedValue as Float
            view.alpha = alpha
        }
        animator.duration = 100
        animator.start()
    }

    private fun fadeOut(view: View) {
        val animator = ValueAnimator.ofFloat(1f, 0f)
        animator.addUpdateListener {
            val alpha = it.animatedValue as Float
            view.alpha = alpha
        }
        animator.duration = 100
        animator.start()
    }


    private fun showGameMainPopup(context: Context, isShow: Boolean = true) {
        tryCreateMainPopup(context)
        if (!isShow) {
            homeXPopupDialog?.dismiss()
            return
        }
        homeXPopupDialog!!.show()
    }

    fun showFastViewPopWhenWin() {
        homeXPopupDialog?.show()
    }

    /**
     * 验证是否能进入游戏
     * @return true 不能进入; false 能进入
     */
    private fun verifyEnterGame(context: Context):Boolean{
        if (!gameAboutModel.isOpen || !gameAboutModel.isLoginSuccess.value!!) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.toast_login_fault),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!gameAboutModel.isEnterGameSuccess) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.toast_enter_game_fault),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
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
                LogUtils.eTag(TAG, "isEnterGameSuccess:${gameAboutModel.isEnterGameSuccess},isOpen:${gameAboutModel.isOpen},isLoginSuccess:${gameAboutModel.isLoginSuccess.value},homeXPopupDialog:${homeXPopupDialog}")
                if(!verifyEnterGame(context)) return@clickNoRepeat
                if (homeXPopupDialog != null) {
                    LogUtils.eTag(TAG, "homeXPopupDialog exists, no need to create it.")
                    homeXPopupDialog!!.show()
                    return@clickNoRepeat
                }
                showGameMainPopup(context, true)
            }
        }.apply {
            observerShowGameInfo(this)
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

    /**
     * 监听ShowGameInfo打开投注界面和游戏大厅
     * @param v gamesdk游戏浮窗
     */
    private fun observerShowGameInfo(v: View) {
        v.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            private val obsrv: Observer<Pair<Int, Boolean>> = Observer<Pair<Int, Boolean>> {
                ThreadUtils.mainScope.launch {
                    val context = v.context
                    if(!verifyEnterGame(context)) return@launch
                    val miniGameId = gameAboutModel.miniGameId
                    val isGameList = gameAboutModel.isGameList
                    if (!isGameList) { //投注主界面
                        showGameMainPopup(v.context)
                    } else { //投注界面-->游戏列表界面
                        showGameMainPopup(context)
                        withTimeout(1000){
                            while(homeXPopupDialog == null || homeXPopupDialog?.isShow == false) delay(10)
                        }
                        homeXPopupDialog!!.dismiss()
                        val fm = homeXPopupDialog!!.fragment.childFragmentManager
                        val height = homeXPopupDialog!!.fragment.requireView().height
                        showGameList(context, fm, height, miniGameId)
                    }
                }
            }

            override fun onViewAttachedToWindow(p0: View) {
                gameAboutModel.isShowGameInfo.observeForever(obsrv)
            }

            override fun onViewDetachedFromWindow(p0: View) {
                gameAboutModel.isShowGameInfo.removeObserver(obsrv)
            }
        })
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