package arch.cayenne.module.chat.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Typeface
import arch.cayenne.module.chat.databinding.FragmentChatUserInfoBinding
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.ui.viewmodel.ChatUserInfoViewModel
import com.walisport.module.live.ui.widget.ChatInfoGestureListener
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch.ChatInfoSlideDirection
import kotlin.reflect.KClass
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.fragment.BaseSideSheetDialogFragment
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.FragmentExt.setFragmentResult
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableTextView
import com.google.android.material.tabs.TabLayoutMediator
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2sp
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.setupViewPagerScroll
import arch.cayenne.lib.common.utils.ext.startFadeAnim
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import com.google.android.material.bottomsheet.BottomSheetBehavior

//上滚-弹窗-头像-名字-列表
//下滚-列表拉到顶部-头像-名字-弹窗
class ChatUserInfoFragment :
    BaseBottomSheetFragment<ChatUserInfoViewModel, FragmentChatUserInfoBinding>() {

    companion object {
        const val TAG = "ChatUserInfoFragment"

        fun show(manager: FragmentManager, user: ChatRefUser) {
            val fragment = ChatUserInfoFragment()
            fragment.setChatUser(user)
            fragment.show(manager, TAG)
        }
    }


    override val vbClass: KClass<FragmentChatUserInfoBinding>
        get() = FragmentChatUserInfoBinding::class
    override val vmClass: KClass<ChatUserInfoViewModel>
        get() = ChatUserInfoViewModel::class
    var user:ChatRefUser? = null

    override fun initView(savedInstanceState: Bundle?) {
        loadFragment()
    }

    fun setChatUser(chatRefUser: ChatRefUser) {
        this.user = chatRefUser
    }

    private var isChatUserInfoAvatarLayoutDow: Boolean = false //按下的是否是资料区域
    val listFragment = listOf(
        PagerBean(R.string.chat_love_play.getString()) { ChatViewPagerFragment() },
        PagerBean(R.string.chat_max_win.getString()) { ChatViewPagerFragment() },
        PagerBean(R.string.chat_max_number.getString()) { ChatViewPagerFragment() },
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mBinding.apply {
            tvAt.setOnClickListener{
                setFragmentResultListener()
                dismiss()
            }
            tvReport.setOnClickListener {
                ChatReportFragment.show(this@ChatUserInfoFragment)
            }
        }

        // 上层 View 触摸事件
        mBinding.LayoutInterceptTouch.setOnTouchListener { _, event ->
            // 将触摸事件传递给下层 View
            mBinding.topScale.dispatchTouchEvent(event)
            false // 返回 false 不消耗事件，允许事件继续传递
        }

        mBinding.viewTop.clickNoRepeat {
            dismiss()
        }
        //资料卡区域
        mBinding.chatUserInfoAvatarLayoutScale.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isChatUserInfoAvatarLayoutDow = true //触摸事件为资料卡区域
                    true
                }

                MotionEvent.ACTION_UP -> {

                    true
                }

                else -> false
            }
            mBinding.skinTab.dispatchTouchEvent(event)
        }


        mBinding.LayoutInterceptTouch.seGestureListener(object : ChatInfoGestureListener {
            override fun onAdjustLayoutScrollUp(direction: ChatInfoSlideDirection) {
                //是否触发资料卡区域,执行资料卡区域动画
                if (isChatUserInfoAvatarLayoutDow) {
                    when (direction) {
                        ChatInfoSlideDirection.UP -> {
                            //    LogUtils.e("ChatUserInfoFragment--------onAdjustLayoutScrollUp----->${ChatInfoSlideDirection.UP},${direction}")
                            mBinding.chatUserInfoAvatarLayoutScale.quickAdjustLayoutUp()
                            mBinding.topScale.adjustLayout(0f, ChatInfoSlideDirection.UP, true)
                        }

                        ChatInfoSlideDirection.DOWN -> {
                            //  LogUtils.e("ChatUserInfoFragment--------onAdjustLayoutScrollUp----->${ChatInfoSlideDirection.DOWN},${direction}")
                            mBinding.chatUserInfoAvatarLayoutScale.animTingDow()
                            mBinding.topScale.adjustLayoutViewTopAnim(ChatInfoSlideDirection.DOWN) {}

                        }
                    }
                    isChatUserInfoAvatarLayoutDow = false
                }
            }

            override fun onAdjustLayoutScroll(deltaY: Float, direction: ChatInfoSlideDirection) {
                //  LogUtils.e("ChatUserInfoFragment--------->${deltaY},${direction}")

                if (isChatUserInfoAvatarLayoutDow) {//滑动资料区域
                    //根据手势资料卡区域放大缩小
                    mBinding.chatUserInfoAvatarLayoutScale.adjustLayoutTop(deltaY, direction)
                    mBinding.topScale.adjustLayout(deltaY, direction, false)
                } else {
                    //往下滑动,子类的rv是否滑到了第一条或者顶部
                    if (direction == ChatInfoSlideDirection.DOWN) {
                        var bool: Boolean? = mViewModel.sonVerticalScrollIsTop.value
                        bool?.let {
                            if (it) {
                                // 如果当前高度在 max-min  范围内，返回 true，表示可以滑动
                                if (mBinding.chatUserInfoAvatarLayoutScale.isDirectionToScroll()) {
                                    mBinding.topScale.adjustLayout(deltaY, direction)//弹窗往下
                                } else {
                                    mBinding.chatUserInfoAvatarLayoutScale.adjustLayout(
                                        deltaY,
                                        direction
                                    ) {
                                        //头像和名字区域往下
                                        mBinding.topScale.adjustLayoutViewTopAnim(direction) {
                                            //列表跟手
                                            mViewModel.setScrollTop(true)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        mBinding.topScale.adjustLayout(deltaY, direction)
                        mBinding.chatUserInfoAvatarLayoutScale.adjustLayout(
                            deltaY,
                            direction
                        ) {

                        }
                    }
                }
            }
        })

        mBinding.tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.let {
                    if (isTabClick) {
                        CustomTabIndicatorUtils.animateIndicatorToPosition(
                            mBinding.customIndicator,
                            tab.position
                        )
                        val vp = mBinding.vpPage
                        vp.startFadeAnim {
                            vp.setCurrentItem(tab.position, false)
                            it.invoke()
                        }
                    }
                }
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            R.color.tab_selected_text_color
                        )
                    )
                    textView.textSize = 13f.px2sp
                    textView.typeface = Typeface.DEFAULT_BOLD
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                tab.view.findViewById<SkinnableTextView>(R.id.tabText)?.let { textView ->
                    textView.setTextColor(
                        SkinnableResourceManager.getColor(
                            textView.context,
                            arch.cayenne.lib.common.R.color.color_999999
                        )
                    )
                    textView.textSize = 13f.px2sp
                    textView.typeface = Typeface.DEFAULT
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
        mBinding.vpPage.setupViewPagerScroll(mBinding.tabLayout, mBinding.customIndicator, 0.14f)
    }


    private fun loadFragment() {
        val tabSelectPosition = 0
        with(mBinding) {
            vpPage.adapter = PagerAdapter(childFragmentManager, lifecycle, listFragment)
            vpPage.offscreenPageLimit = listFragment.size
            TabLayoutMediator(tabLayout, vpPage, false) { tab, position ->
                tab.text = listFragment[position].title
                tab.setCustomView(R.layout.info_custom_tab)
                tab.customView?.findViewById<SkinnableTextView>(R.id.tabText)?.apply {
                    text = listFragment[position].title
                    setTextColor(
                        SkinnableResourceManager.getColor(
                            context,
                            if (position == tabSelectPosition) R.color.tab_selected_text_color else arch.cayenne.lib.common.R.color.color_999999
                        )
                    )
                    textSize = 13f.px2sp
                    typeface =
                        if (position == tabSelectPosition) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

                }
                tab.view.setOnClickListener { /* Handle click */ }
            }.attach()
            tabLayout.clearOnTabSelectedListeners()
            tabLayout.post {
                CustomTabIndicatorUtils.animateIndicatorToPosition(
                    mBinding.customIndicator,
                    tabSelectPosition,
                    false
                )
                mBinding.vpPage.setCurrentItem(tabSelectPosition, false)
            }
            tabLayout.removeAllTips()
        }
    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
        initBottomSheetStyle()
        super.onStart()
    }

    private fun initBottomSheetStyle() {
        val bottomSheet = dialog?.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        val screenHeight = resources.displayMetrics.heightPixels
        val targetHeight = (screenHeight * 1.0).toInt()
        val topOffset = screenHeight - targetHeight
        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet).apply {
            isFitToContents = false
            expandedOffset = topOffset
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
            skipCollapsed = false
            isHideable = false
        }
    }

    private fun setFragmentResultListener(){
        "user == nul ${user}".logd("aaa")
        val bundle = Bundle().apply {
            putParcelable(ChatPrivateUserFragment.CHAT_USER_RESULT,user)
        }
        parentFragmentManager.setFragmentResult( ChatPersonalDialogFragment.CHAT_PERSONAL_REQUEST,bundle)
    }
}