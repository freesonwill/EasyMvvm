package com.cn.game.sdk2.ui.helper

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.enums.GAME_ID_ENUM
import com.cn.game.sdk2.ui.HomeXPopupDialog
import com.cn.game.sdk2.ui.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.view.Fast3HelpPopup
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.locationInWindow
import com.cn.game.sdk2.utils.tool.indicator.CommonPagerIndicator
import com.cn.game.sdk2.websocket.appListener
import com.cn.game.sdk2.websocket.gameAboutModel
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.interfaces.SimpleCallback
import com.xcjh.base_lib2.utils.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import java.lang.ref.WeakReference

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 17:43
 **/
object ViewHelper {
    private const val TAG: String = "ViewHelper"
    enum class ViewFloatType {
        FastView,FastViewOverlay,HomeXPopupDialog,HelpXPopupDialog
    }

    //弱引用防止view不能被回收
    private var viewHolderMap = mutableMapOf<ViewFloatType,WeakReference<View>>()

    private var homeXPopupDialog: BasePopupView?
        get() = viewHolderMap[ViewFloatType.HomeXPopupDialog]?.get() as BasePopupView?
        set(value) { viewHolderMap[ViewFloatType.HomeXPopupDialog] = WeakReference(value)}

    private var helpXPopupDialog: BasePopupView?
        get() = viewHolderMap[ViewFloatType.HelpXPopupDialog]?.get() as BasePopupView?
        set(value) { viewHolderMap[ViewFloatType.HelpXPopupDialog] = WeakReference(value)}

    private var fastView:View?
        get() = viewHolderMap[ViewFloatType.FastView]?.get()
        set(value) { viewHolderMap[ViewFloatType.FastView] = WeakReference(value)}

    private var fastViewOverlay:View?
        get() = viewHolderMap[ViewFloatType.FastViewOverlay]?.get()
        set(value) { viewHolderMap[ViewFloatType.FastViewOverlay] = WeakReference(value)}

    //是否显示其他pop
    var isShowOtherPop:Boolean = false

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
        val (offsetY,height) = homeXPopupDialog!!.findViewById<View>(R.id.topLayout).let {
            arrayOf(it.locationInWindow[1],it.height)
        }
        helpXPopupDialog = XPopup.Builder(context)
            .isTouchThrough(false)
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
            .animationDuration(100)
            .hasStatusBar(false)
            .hasNavigationBar(false)
            .enableDrag(true)
            .dismissOnTouchOutside(true)
            .asCustom(Fast3HelpPopup(context, offsetY,height))
            .apply {
                if(context is LifecycleOwner) { //宿主销毁了，静态引用置null
                    context.lifecycle.addObserver(object :DefaultLifecycleObserver{
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            dismiss()
                            helpXPopupDialog = null
                        }
                    })
                }
            }
            .show()
    }


    fun showFastViewPop(context: Context,isShow: Boolean){
        if(!isShow){
            homeXPopupDialog?.dismiss()
            return
        }
        if(homeXPopupDialog != null) {
            homeXPopupDialog!!.show()
            return
        }
        val pop = HomeXPopupDialog(context, Fast3MainFragment(),GAME_ID_ENUM.GAME_FAST3.num).apply {
            homeXPopupDialog = this
        }
        XPopup.Builder(context)
            .hasShadowBg(false)
            .setPopupCallback(object : SimpleCallback() {
                override fun beforeShow(popupView: BasePopupView?) {
                    super.beforeShow(popupView)
                    fastViewOverlay?.isVisible = false
                    fastView?.isVisible = false
                    appListener?.onGameFloatingDetailViewStatus(true)
                    gameAboutModel.fast3MainFloatVisible.value = false
                }
                override fun onShow(popupView: BasePopupView?) {
                    super.onShow(popupView)
                }

                override fun onDismiss(popupView: BasePopupView?) {
                    super.onDismiss(popupView)
                    gameAboutModel.fast3MainFloatVisible.value = true
                    if(!isShowOtherPop) {
                        fastViewOverlay?.isVisible = true
                        fastView?.isVisible = true
                        appListener?.onGameFloatingDetailViewStatus(false)
                        //homeXPopupDialog = null
                    }
                }
            })
            .popupAnimation(PopupAnimation.TranslateFromBottom)
            .animationDuration(200)
            .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
            .isViewMode(true)
            .isTouchThrough(true)
            .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            .isThreeDrag(false) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
            .enableDrag(true)
            .dismissOnTouchOutside(true)
            .asCustom(pop)
            .apply {
                //宿主销毁了，
                if(context is LifecycleOwner) {
                    context.lifecycle.addObserver(object :DefaultLifecycleObserver{
                        override fun onDestroy(owner: LifecycleOwner) {
                            super.onDestroy(owner)
                            dismiss()
                            homeXPopupDialog = null
                        }
                    })
                }
            }
            .show()
    }

    fun getFastView(context: Context):View{
        if(fastView != null) return fastView!!
        return LayoutInflater.from(context).inflate(R.layout.drag_fast_easy,null,false).also {
            fastView = it
            val lp = ViewGroup.LayoutParams(0,0)
            lp.width = 58.dp2px
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.layoutParams = lp
            val llFastClick = it.findViewById<LinearLayout>(R.id.llFastClick)
            llFastClick.setOnClickListener {
                if (homeXPopupDialog != null) {
                    Log.d(TAG, "homeXPopupDialog exists, no need to create it.")
                    homeXPopupDialog!!.show()
                    return@setOnClickListener
                }
                showFastViewPop(context,true)
            }
        }.apply {
            if(context is LifecycleOwner) {
                context.lifecycle.addObserver(object :DefaultLifecycleObserver{
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        fastView = null
                    }
                })
            }
        }
    }

    fun getFastViewOverlay(context: Context):View{
        if(fastViewOverlay != null) return fastViewOverlay!!
        return LayoutInflater.from(context).inflate(R.layout.fragment_fast3_overlay,null,false).also {
            val lp = ViewGroup.LayoutParams(0,0)
            lp.width = 106.dp2px
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.layoutParams = lp
            fastViewOverlay = it
        }.apply {
            if(context is LifecycleOwner) {
                context.lifecycle.addObserver(object :DefaultLifecycleObserver{
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        fastViewOverlay = null
                    }
                })
            }
        }
    }

    fun ViewPager.initGameViewPager2(views: ArrayList<View>): ViewPager {
        //设置适配器
        adapter = object : PagerAdapter(){
            override fun getCount(): Int {
                return views.count()
            }

            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return  view == obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = views[position]
                container.addView(view)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }
        }
        return this
    }

    fun ViewPager.initGameViewPager(
        fragmentManager: FragmentManager,
        fragments: ArrayList<Fragment>,
        titles: ArrayList<String>? = null
    ): ViewPager {
        //设置适配器
        adapter = object : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles?.get(position)
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                //保留此方法，不销毁PageItem，解决开始初始化page太多问题
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
                    setTextBold(this, false)
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



}