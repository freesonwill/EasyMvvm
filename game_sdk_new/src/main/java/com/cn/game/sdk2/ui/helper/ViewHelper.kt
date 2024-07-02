package com.cn.game.sdk2.ui.helper

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.GAME_ID_ENUM
import com.cn.game.sdk2.ui.HomeXPopupDialog
import com.cn.game.sdk2.ui.fast3.Fast3HelpFragment
import com.cn.game.sdk2.ui.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.view.CustomBubbleAttachPopup
import com.cn.game.sdk2.ui.view.Fast3HelpPopup
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.tool.indicator.CommonPagerIndicator
import com.cn.game.sdk2.websocket.appListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.animator.EmptyAnimator
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern
import com.xcjh.base_lib.utils.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 17:43
 **/
object ViewHelper {
    private const val TAG: String = "ViewHelper"
    private const val TAG_FASTVIEW = "TAG_FASTVIEW"
    private const val TAG_FASTVIEW_OVERLAY = "TAG_FASTVIEW_OVERLAY"
    private var homeXPopupDialog: BasePopupView? = null
    private var helpXPopupDialog: BasePopupView? = null

    /**
     * 显示帮助文档
     */
    fun showHelpDialog(context: Context, isShow: Boolean = true) {
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
        helpXPopupDialog = XPopup.Builder(context)
            .isTouchThrough(true)
            .setPopupCallback(object : SimpleCallback() {
                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    helpXPopupDialog = null
                }
            })
             //.customAnimator(EmptyAnimator(bubbleAttach, 0))
            .navigationBarColor(android.R.color.transparent)
            .hasShadowBg(false) // 去掉半透明背景
            .isViewMode(true)
            .hasStatusBar(false)
            .hasNavigationBar(false)
            .asCustom(Fast3HelpPopup(context))
        helpXPopupDialog?.show()
    }

    fun showFastView(context: Context, isShow: Boolean = true) {
        if (!isShow) EasyFloat.hide(TAG_FASTVIEW)
        EasyFloat.with(context).setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setTag(TAG_FASTVIEW)
            .setGravity(Gravity.END, 0, 300.dp2px)
            .setLayout(R.layout.drag_fast_easy) {
                val llFastClick = it.findViewById<LinearLayout>(R.id.llFastClick)
                llFastClick.setOnClickListener {
                    if (homeXPopupDialog != null) {
                        Log.d(TAG, "homeXPopupDialog exists, no need to create it.")
                        return@setOnClickListener
                    }
                    XPopup.Builder(context)
                        .hasShadowBg(false)
                        .animationDuration(0)
                        .setPopupCallback(object : SimpleCallback() {
                            override fun onShow(popupView: BasePopupView?) {
                                super.onShow(popupView)
                                showFastViewOverlay(context, false)
                                appListener?.onGameFloatingDetailViewStatus(true)
                            }

                            override fun onDismiss(popupView: BasePopupView?) {
                                super.onDismiss(popupView)
                                showFastViewOverlay(context, true)
                                homeXPopupDialog = null
                                appListener?.onGameFloatingDetailViewStatus(false)
                            }
                        })
                        .popupAnimation(PopupAnimation.TranslateFromBottom)
                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .isViewMode(true)
                        .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
                        .isThreeDrag(false) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                        .enableDrag(false)
                        .asCustom(HomeXPopupDialog(context, Fast3MainFragment(),GAME_ID_ENUM.GAME_FAST3.num).apply {
                            homeXPopupDialog = this
                        })
                        .show()
                    //EasyFloat.hide(TAG_FASTVIEW)
                }
            }
            .show()
    }

    /**
     * 快三悬浮窗
     */
    fun showFastViewOverlay(context: Context, show: Boolean = true) {
        if (!show) {
            EasyFloat.hide(TAG_FASTVIEW_OVERLAY)
            return
        }
        EasyFloat.with(context).setSidePattern(SidePattern.DEFAULT)
            .setImmersionStatusBar(true)
            .setTag(TAG_FASTVIEW_OVERLAY)
            .setGravity(Gravity.START, 6.dp2px, 122.dp2px)
            .setLayout(R.layout.fragment_fast3_overlay)
            .show()
    }


    fun ViewPager.initGameViewPager(
        fragmentManager: FragmentManager,
        fragments: ArrayList<Fragment>,
        titles: ArrayList<String>? = null
    ): ViewPager {
        //设置适配器
        adapter = object : FragmentStatePagerAdapter(
            fragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles?.get(position)
            }
        }
        return this
    }


    /**
     * 该文件只添加扩展方法，其他top函数根据业务情况合理安置，便于查找、管理
     * 各种公共扩展方法
     */

    fun ViewPager.initActivityGame(
        fragmentManager: FragmentManager,
        fragments: ArrayList<Fragment>,
        titles: ArrayList<String>? = null
    ): ViewPager {
        //设置适配器
        adapter = object : FragmentStatePagerAdapter(
            fragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles?.get(position)
            }

        }

        return this
    }


    /*
     * ViewPager + MagicIndicator 指示器
     */
    fun MagicIndicator.bindViewPagerNewGame(
        viewPager: ViewPager,
        mStringList: List<String> = arrayListOf(),
        scrollEnable: Boolean = false,
        action: (index: Int) -> Unit = {}
    ) {
        // viewPager.offscreenPageLimit = mStringList.size
        val commonNavigator = CommonNavigator(context)
        if (scrollEnable) {
            commonNavigator.isSkimOver = true
        } else {
            commonNavigator.isAdjustMode = true
        }
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return mStringList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                requestDisallowInterceptTouchEvent(true)

                return ColorTransitionPagerTitleView(context).apply {
                    //设置文本
                    text = mStringList[index].toHtml()
                    //字体大小
                    textSize = 14f
                    setTextBold(this, true)
                    // setBackgroundColor(ContextCompat.getColor(appContext, R.color.red_F7736D))
                    //未选中颜色
                    normalColor = ContextCompat.getColor(context, R.color.g_9696b8)
                    //选中颜色
                    selectedColor = ContextCompat.getColor(context, R.color.g_f7cf41)
                    //点击事件
                    setOnClickListener {
                        viewPager.currentItem = index
                        action.invoke(index)
                    }
                }
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                return CommonPagerIndicator(context).apply {
                    mode = 0
                    // indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_select)
                }
            }

        }
        this.navigator = commonNavigator

        //viewPager 绑定 navigator
        ViewPagerHelper.bind(this, viewPager)
    }


    /**
     * 文字加粗无效的时候，如： textView.setTypeface(null, Typeface.BOLD) 或者 textView.typeface = Typeface.DEFAULT_BOLD
     */
    fun setTextBold(textView: TextView?, isBold: Boolean) {
        try {
            if (textView != null) {
                val paint: Paint? = textView.paint
                paint?.isFakeBoldText = isBold
            }
        } catch (_: Exception) {
        }
    }

    fun View.isAdd(): Boolean {
        return parent != null
    }

}