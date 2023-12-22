package com.xcjh.app.ui.home.msg

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.OnTabSelectListener
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.databinding.FrMsgBinding
import com.xcjh.app.ui.search.SeacherFriendActivity
import com.xcjh.app.ui.search.SeacherMsgActivity
import com.xcjh.app.utils.clearMsg
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.setOnclickNoRepeat


class MsgFragment : BaseFragment<MsgVm, FrMsgBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var index=0
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .titleBar(mDatabind.rlTitle)
            .init()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
//        Log.i("FFFFFFFFF","3333333333333333")
    }

    private fun initEvent() {
        mTitles = resources.getStringArray(R.array.str_msg_top)

        mFragments.add(MsgChildFragment.newInstance())
        mFragments.add(MsFriendFragment.newInstance())

        mDatabind.vp.initActivity(requireActivity(), mFragments, false)

        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.vp, arrayListOf(
                mTitles!![0],
                mTitles!![1]
            ),
            R.color.c_37373d,
            R.color.c_94999f,
            18f, 16f, true, false,
            R.color.c_34a853, margin = 30
        )

        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                index=position
                if (position==0){
                    mDatabind.ivclear.visibility=View.VISIBLE
                }else{
                    mDatabind.ivclear.visibility=View.GONE
                }
            }

        })

        setOnclickNoRepeat(mDatabind.ivclear) {
            when (it.id) {
                R.id.ivclear -> {
                    clearMsg(requireActivity()) { it ->
                        if (it){//点击了确定

                            appViewModel.updateMsgEvent.postValue("-1")
                        }

                    }
                }

            }
        }

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