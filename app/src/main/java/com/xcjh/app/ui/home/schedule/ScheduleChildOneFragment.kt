package com.xcjh.app.ui.home.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.databinding.FrScheduleoneBinding
import com.xcjh.app.utils.selectTime
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.setOnclickNoRepeat

class ScheduleChildOneFragment : BaseFragment<ScheduleVm, FrScheduleoneBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var matchtype: String? = ""
    var matchtypeOld: String? = ""
    var status = 0
    var hasData = false
    var isVisble = false
    var calendarTime: String = ""
    var mTabPosition = 0
    var mOneTabIndex = 0
    var mTwoTabIndex = 0
    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val STATUS = "status"
        private val TAB = "tab"
        fun newInstance(matchtype: String, status: Int, po: Int): ScheduleChildOneFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putInt(STATUS, status);
            args.putInt(TAB, po);
            val fragment = ScheduleChildOneFragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isVisble = isVisibleToUser
    }

    override fun onPause() {
        super.onPause()
        isVisble = false
    }
    override fun onResume() {
        super.onResume()
       // isVisble = mTabPosition == mPushPosition
        if (!hasData) {

            mViewModel.getHotMatchData(matchtypeOld!!, status)

        }

    }
    override fun initView(savedInstanceState: Bundle?) {

        val bundle = arguments
        if (bundle != null) {
            matchtype = bundle.getString(ScheduleChildOneFragment.MATCHTYPE)!!
            matchtypeOld = matchtype
            status = bundle.getInt(ScheduleChildOneFragment.STATUS)
            mOneTabIndex = bundle.getInt(ScheduleChildOneFragment.TAB)
        }
        mViewModel.getHotMatchData(matchtypeOld!!, status)
        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                appViewModel.updateScheduleTwoPosition.postValue(position)

                if (position==(mFragments.size-1)){
                    appViewModel.updateViewpager.postValue(true)
                }else{
                    appViewModel.updateViewpager.postValue(false)
                }
            }

        })

        setOnclickNoRepeat(mDatabind.ivMeau) {
            when (it.id) {
                R.id.iv_meau -> {

                    selectTime(requireActivity(), calendarTime) { start, end ->

                        calendarTime =
                            start.year.toString() + "-" + TimeUtil.checkTimeSingle(start.month) + "-" + TimeUtil.checkTimeSingle(
                                start.day
                            )

                        appViewModel.updateganlerTime.postValue(calendarTime)
                    }
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

            }

        }


    }
    private fun initEvent(datas:ArrayList<HotMatchBean>) {
        mFragments.clear()
        var titles: MutableList<String> = ArrayList<String>()
        for (i in 0 until  datas!!.size) {
            mFragments.add(ScheduleChildTwoFragment.newInstance(datas[i].matchType,datas[i].competitionId,status,mOneTabIndex,i))
            titles.add(datas[i].competitionName)
        }
        mDatabind.vp.initActivity(requireActivity(), mFragments, true)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.vp, titles,
            R.color.c_f5f5f5,
            R.color.c_8a91a0,
            18f, 18f, false, true,
            R.color.translet, margin = 8
        )
        mDatabind.vp.offscreenPageLimit = mFragments.size

//        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
//        mDatabind.slide.setViewPager(mDatabind.vp)
//        mDatabind.vp.currentItem = 0
//        mDatabind.vp.offscreenPageLimit=4



    }
}