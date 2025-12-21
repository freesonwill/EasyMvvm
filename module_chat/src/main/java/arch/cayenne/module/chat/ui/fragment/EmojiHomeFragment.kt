package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.base.ui.adapter.PagerAdapter
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.CustomTabIndicatorUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.removeAllTips
import arch.cayenne.lib.common.utils.ext.setupHorizontalScrollDegree
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.databinding.FragmentEmojiHomeLayoutBinding
import arch.cayenne.module.chat.databinding.ItemTabEmojiLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.EmojiHomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 22/10/25 11:33
 * @description:
 */
class EmojiHomeFragment : BaseFragment<EmojiHomeViewModel, FragmentEmojiHomeLayoutBinding>() {
    override val vbClass: KClass<FragmentEmojiHomeLayoutBinding>
        get() = FragmentEmojiHomeLayoutBinding::class
    override val vmClass: KClass<EmojiHomeViewModel>
        get() = EmojiHomeViewModel::class
    private val chatViewModel: ChatHomeViewModel by sharedViewModel<ChatHomeViewModel, ChatHomeFragment>()


    override fun initView(savedInstanceState: Bundle?) {
        initTab()
    }

    override fun initListener() {
        mBinding.tvDel.setOnClickListener {
            chatViewModel.etDelFunction()
        }
        mBinding.tvSend.setOnClickListener {
            chatViewModel.sendTextToChat()
        }
    }


    override suspend fun createObserver() {
     chatViewModel.currentKeyBoardTypeLiveData.observe(viewLifecycleOwner){
         if(it == KeyBoardType.CHAT){
             mBinding.viewpager.setCurrentItem(0,false)
         }
     }
    }

    private fun initTab() {
        val list = resources.getStringArray(R.array.emoji_tab)
        val pageList = arrayListOf(
            PagerBean(list[0]) {
                EmojiFragment().apply {
                    arguments = Bundle().apply { putInt("type", EmojiTypeEnum.NORMAL.value) }
                }
            },
            PagerBean(list[1]) {
                EmojiFragment().apply {
                    arguments = Bundle().apply { putInt("type", EmojiTypeEnum.BID.value) }
                }
            }
        )
        mBinding.apply {
            viewpager.adapter = PagerAdapter(childFragmentManager, lifecycle, pageList)
            TabLayoutMediator(emojiTablayout, viewpager, false) { tab, position ->
                val tabView = ItemTabEmojiLayoutBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    null,
                    false
                )
                tabView.apply {
                    tabIcon.setImageResource(if (position == 0) R.drawable.tab_emoji else R.drawable.tab_bid)
                    tabTv.text = list[position]
                }
                tab.customView = tabView.root
            }.attach()
            emojiTablayout.removeAllTips()

            emojiTablayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
                override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                    tvDel.isVisible = tab.position == 0
                    tvSend.isVisible = tab.position == 0

//                    if (isTabClick) {
//                        CustomTabIndicatorUtils.animateIndicatorToPosition(
//                            mBinding.customIndicator,
//                            tab.position
//                        )
//                        val vp = viewpager
//                        vp.startFadeAnim {
//                            vp.setCurrentItem(tab.position, false)
//                            it.invoke()
//                        }
//                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                }

                override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                }
            })
            reflexPadding(tabLayout = emojiTablayout)
            // 自定義滑動行為
//            viewpager.setupViewPagerScroll(emojiTablayout, customIndicator, 0.15f)
            viewpager.setupHorizontalScrollDegree()
            emojiTablayout.setSelectedTabIndicator(arch.cayenne.module.betslip.R.drawable.bg_order_indicator)
        }
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                //拿到tabLayout的mTabStrip属性
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    //设置tab左右间距为8dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.topMargin = -7.dp2px
                    params.leftMargin =
                        if (i == 0) 0 else 79.dp2px          //     lp.leftMargin = if (position == 0) 84.dp2px else 79.dp2px
                    tabView.layoutParams = params
                    tabView.setPadding(0, 0, 0, 0)
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initSoftRecycler() {
//        val snapHelper = OnePageSnapHelper()
//        mBinding.emojiRecycler.apply {
//            isNestedScrollingEnabled = false
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            val softAdapter = EmojiHomeAdapter()
//            softAdapter.setItemListener(itemListener)
//            softAdapter.delListener = {
////                mBinding.liveChatEtInput.apply {
////                    if (mBinding.liveChatEtInput.text?.length == 0) {
////                        return@apply
////                    }
////                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
////                    dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
////                    softKeyBoardManager.etRequestFocus()
////                }
//            }
//            softAdapter.submitList(mViewModel.softData())
//            adapter = softAdapter
//            snapHelper.attachToRecyclerView(this)
//
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        val currentView = snapHelper.findSnapView(recyclerView.layoutManager)
//                        currentView?.let {
//                            val position = recyclerView.getChildAdapterPosition(currentView)
//                        }
//                    }
//                }
//            })
//        }
    }


    companion object {
        val TAG = EmojiHomeFragment::class.simpleName
        val DEL_ETINPUT = 101
    }
}