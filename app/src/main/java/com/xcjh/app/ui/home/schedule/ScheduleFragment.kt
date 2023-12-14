package com.xcjh.app.ui.home.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.flyco.tablayout.listener.OnTabSelectListener
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.FrCourseBinding
import com.xcjh.app.vm.MainVm
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity


class ScheduleFragment : BaseFragment<MainVm, FrCourseBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    private val mtypes = arrayOf("0", "1", "2", "3")
    private val status = arrayOf(0, 0, 0, 99)
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mDatabind.rlTitle)
            .navigationBarColor(R.color.c_ffffff)
            .init()
        initEvent()
        MyWsManager.getInstance(requireActivity())!!
            .setC2CListener(javaClass.name, object : C2CListener {
                override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {

                }

                override fun onSystemMsgReceive(chat: FeedSystemNoticeBean) {

                }

                override fun onC2CReceive(chat: ReceiveChatMsg) {

                }

                override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {
                    LogUtils.d("收到推送消息")

                    appViewModel.appPushMsg.postValue(chat)

                }
            })
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initEvent() {
        mTitles = resources.getStringArray(R.array.str_schedule_tab_top)
        for (i in 0 until mTitles!!.size) {
            mFragments.add(ScheduleChildOneFragment.newInstance(mtypes[i], status[i], i))
        }
        mDatabind.vp.initActivity(requireActivity(), mFragments, true)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.vp, arrayListOf(
                mTitles!![0],
                mTitles!![1],
                mTitles!![2],
                mTitles!![3]
            ),
            R.color.c_37373d,
            R.color.c_94999f,
            18f, 16f, true, false,
            R.color.c_34a853, margin = 30
        )
        mDatabind.vp.offscreenPageLimit = 4
       // mDatabind.vp.isUserInputEnabled = false

//        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
//        mDatabind.slide.setViewPager(mDatabind.vp)
//        mDatabind.vp.currentItem = 0
//        mDatabind.vp.offscreenPageLimit=4
        appViewModel.updateViewpager.observeForever {

           // mDatabind.vp.isUserInputEnabled = it
        }

        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                appViewModel.updateSchedulePosition.postValue(position)
            }

        })

    }

    override fun createObserver() {
        /* appViewModel.updateLoginEvent.observe(this) {
             mViewModel.getUserBaseInfo()
         }*/
    }

    private inner class MyPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles?.get(position)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }

}