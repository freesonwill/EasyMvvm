package com.xcjh.app.ui.home.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.FragmentHomeBinding
import com.xcjh.app.ui.home.home.tab.CompetitionTypeListFragment
import com.xcjh.app.ui.home.home.tab.MainNewsListFragment
import com.xcjh.app.ui.home.home.tab.MainRecommendFragment
import com.xcjh.app.ui.home.home.tab.MainRecommendNewFragment
import com.xcjh.app.ui.search.SearchActivity
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.view.clickNoRepeat


class HomeFragment : BaseFragment<MainVm, FragmentHomeBinding>() {

    private var mFragList = ArrayList<Fragment>()

    override fun initView(savedInstanceState: Bundle?) {

//        mFragList.add(MainRecommendFragment())
        mFragList.add(MainRecommendNewFragment())
        var soccerFragment = CompetitionTypeListFragment()
        val soccer = Bundle().apply {
            putInt("type", 1)
        }
        soccerFragment.arguments = soccer
        mFragList.add(soccerFragment)
        var basketballFragment = CompetitionTypeListFragment()
        val basketball = Bundle().apply {
            putInt("type", 2)
        }
        basketballFragment.arguments = basketball
        mFragList.add(basketballFragment)
        mFragList.add(MainNewsListFragment())

        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mDatabind.rlTitle)
            .init()

        //初始化viewpager2
        mDatabind.viewPager.initActivity(requireActivity(), mFragList, true,4)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager, arrayListOf(
                getString(R.string.main_txt_recommended),
                getString(R.string.main_txt_soccer),
                getString(R.string.main_txt_basketball),
                getString(R.string.main_txt_news)
            ),
            R.color.c_37373d,
            R.color.c_94999f,
            18f, 16f, true, false,
            R.color.c_34a853, margin = 30
        )
        mDatabind.viewPager.offscreenPageLimit = mFragList.size

        mDatabind.ivHomeSearch.clickNoRepeat {
            startNewActivity<SearchActivity>()
        }



        mDatabind.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                if (position == 0) {
//                    mDatabind.ivHomeGg.setImageDrawable(
//                        ContextCompat.getDrawable(requireContext(), R.drawable.icon_home_bg)
//                    )
//                } else {
//                    mDatabind.ivHomeGg.setImageDrawable(
//                        ContextCompat.getDrawable(requireContext(), R.color.black)
//                    )
//                }


            }
        })
    }


    override fun onResume() {
//        Log.i("FFFFFFFFF","11111111111")
        super.onResume()
//        ImmersionBar.with(this)
//            .statusBarDarkFont(false)//黑色
//            .titleBar(mDatabind.rlTitle)
//            .navigationBarColor(R.color.c_34a853)
//            .init()

    }

    override fun createObserver() {
        /* appViewModel.updateLoginEvent.observe(this) {
             mViewModel.getUserBaseInfo()
         }*/
        appViewModel.homeViewPagerEvent.observe(this) {
            mDatabind.magicIndicator.onPageSelected(it)
            mDatabind.viewPager.currentItem = it
        }

    }


}