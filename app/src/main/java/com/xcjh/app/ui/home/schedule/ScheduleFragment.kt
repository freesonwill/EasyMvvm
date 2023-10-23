package com.xcjh.app.ui.home.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flyco.tablayout.listener.OnTabSelectListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.FrCourseBinding
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.setOnclickNoRepeat


class ScheduleFragment : BaseFragment<MainVm, FrCourseBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? =null
    private val mtypes = arrayOf("0", "1","2","3")
    private val status = arrayOf(0,0,0,99)
    override fun initView(savedInstanceState: Bundle?) {

        initEvent()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initEvent() {
        mTitles=resources.getStringArray(R.array.str_schedule_tab_top)
        for (i in 0 until  mTitles!!.size) {
            mFragments.add(ConmentFragment.newInstance(mtypes[i],status[i],i))
        }
        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
        mDatabind.slide.setViewPager(mDatabind.vp)
        mDatabind.vp.currentItem = 0
        mDatabind.vp.offscreenPageLimit=4
        mDatabind.slide.setOnTabSelectListener(object :OnTabSelectListener{
            override fun onTabSelect(position: Int) {
                appViewModel.updateSchedulePosition.postValue(position)
            }

            override fun onTabReselect(position: Int) {
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