package com.xcjh.app.ui.home.schedule

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lxj.xpopup.XPopup
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.CurrentIndex
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.databinding.FrScheduleoneBinding
import com.xcjh.app.listener.OnChooseDateListener
import com.xcjh.app.utils.selectDate
import com.xcjh.app.view.XPBottomPopu
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.setOnclickNoRepeat

class ScheduleChildOneFragment : BaseFragment<ScheduleVm, FrScheduleoneBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var matchtype: String? = ""
    var matchtypeOld: String? = ""
    var status = ""
    var hasData = false
    var isVisble = false
    var calendarTime: String = ""
    var currentCount= 0
    var mOneTabIndex = 0
    var mTwoTabIndex = 0
    private lateinit var parentFragment: ScheduleFragment
    lateinit var bottomDilog: XPBottomPopu

    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val STATUS = "status"
        private val TAB = "tab"
        fun newInstance(matchtype: String, status: String, po: Int): ScheduleChildOneFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putString(STATUS, status);
            args.putInt(TAB, po);
            val fragment = ScheduleChildOneFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //  isVisble = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.getHotMatchData(matchtypeOld!!, status)

    }

    override fun onPause() {
        super.onPause()
        isVisble = false
    }

    override fun onResume() {
        super.onResume()
        // isVisble = mTabPosition == mPushPosition

        if (!isFirst && !hasData) {

            mViewModel.getHotMatchData(matchtypeOld!!, status)

        }

    }

    fun setPanrent(mparentFragment: ScheduleFragment) {
        parentFragment = mparentFragment
        // 子 Fragment 的逻辑操作

    }

    fun getCurrentIndex(): Int {
        // 子 Fragment 的逻辑操作
        return mDatabind.vp.currentItem
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {

        val bundle = arguments
        if (bundle != null) {
            matchtype = bundle.getString(ScheduleChildOneFragment.MATCHTYPE)!!
            matchtypeOld = matchtype
            status = bundle.getString(ScheduleChildOneFragment.STATUS)!!
            mOneTabIndex = bundle.getInt(ScheduleChildOneFragment.TAB)

        }

        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentCount=position
                if (parentFragment != null) {
                    var bean = CurrentIndex()
                    bean.currtOne = parentFragment!!.getCurrentIndex()
                    bean.currtTwo = position
                    appViewModel.updateSchedulePosition.postValue(bean)
                }

            }

        })
//        appViewModel.updateSchedulePosition.observeForever {
//
//            if (mOneTabIndex==it&&isAdded){
//                var index= mDatabind.vp.currentItem
//                appViewModel.updateScheduleTwoPosition.postValue(index)
//            }
//
//        }

        setOnclickNoRepeat(mDatabind.ivMeau) {
            when (it.id) {
                R.id.iv_meau -> {
                    calendarTime=  (mFragments[currentCount] as ScheduleChildTwoFragment).getCanleTime()
                    bottomDilog = XPBottomPopu(requireActivity())
                    var popwindow = XPopup.Builder(context)
                        .hasShadowBg(true)
                        .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
                        .isViewMode(true)
                        .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
                        .asCustom(bottomDilog).show()
                    bottomDilog.setOnLister(calendarTime, matchtypeOld!!, object : OnChooseDateListener {
                        override fun onDismiss() {
                            popwindow.dismiss()

                        }

                        override fun onSure(time: String?) {
                            popwindow.dismiss()
                            calendarTime = time!!

                            appViewModel.updateganlerTime.postValue(calendarTime)
                        }
                    })

//                    selectDate(requireActivity(), calendarTime) { time ->
//
//                        calendarTime = time
//
//                        appViewModel.updateganlerTime.postValue(calendarTime)
//                    }
                }
            }
        }
    }

    override fun createObserver() {
        val empty = requireActivity().layoutInflater!!.inflate(R.layout.layout_empty, null)

        mViewModel.hotMatch.observe(this) {
            if (it.isNotEmpty()) {
                //成功
                hasData = true
                if (matchtypeOld != "3") {
                    var bean = HotMatchBean(
                        "", resources.getString(R.string.all), 0,
                        matchtype.toString()
                    )
                    it.add(0, bean)
                } else {
                    matchtype = "1"
                    var bean1 = HotMatchBean("", resources.getString(R.string.foot_scr), 0, "1")
                    it.add(0, bean1)
                    var bean2 = HotMatchBean("", resources.getString(R.string.bas_scr), 0, "2")
                    it.add(1, bean2)
                }

                initEvent(it)

            } else {
                var listthis=ArrayList<HotMatchBean>()
                hasData = true
                if (matchtypeOld != "3") {
                    var bean = HotMatchBean(
                        "", resources.getString(R.string.all), 0,
                        matchtype.toString()
                    )
                    listthis.add(0, bean)
                } else {
                    matchtype = "1"
                    var bean1 = HotMatchBean("", resources.getString(R.string.foot_scr), 0, "1")
                    listthis.add(0, bean1)
                    var bean2 = HotMatchBean("", resources.getString(R.string.bas_scr), 0, "2")
                    listthis.add(1, bean2)
                }

                initEvent(listthis)
            }

        }


    }

    private fun initEvent(datas: ArrayList<HotMatchBean>) {
        mFragments.clear()
        var titles: MutableList<String> = ArrayList<String>()
        for (i in 0 until datas!!.size) {
            mFragments.add(
                ScheduleChildTwoFragment.newInstance(
                    datas[i].matchType,
                    datas[i].competitionId,
                    status,
                    mOneTabIndex,
                    i
                )
            )
            titles.add(datas[i].competitionName)
        }
        mDatabind.vp.initActivity(requireActivity(), mFragments, true)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.vp, titles,
            R.color.c_34a853,
            R.color.c_94999f,
            15f, 14f, true, true,
            R.color.translet, margin = 8
        )
        mDatabind.vp.offscreenPageLimit = mFragments.size

//        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
//        mDatabind.slide.setViewPager(mDatabind.vp)
//        mDatabind.vp.currentItem = 0
//        mDatabind.vp.offscreenPageLimit=4


    }
}