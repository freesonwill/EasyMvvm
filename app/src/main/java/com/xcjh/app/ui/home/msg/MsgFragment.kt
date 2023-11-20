package com.xcjh.app.ui.home.msg

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.flyco.tablayout.listener.OnTabSelectListener
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.databinding.FrMsgBinding
import com.xcjh.app.ui.search.SeacherFriendActivity
import com.xcjh.app.ui.search.SeacherMsgActivity
import com.xcjh.app.utils.clearMsg
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.setOnclickNoRepeat


class MsgFragment : BaseFragment<MsgVm, FrMsgBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var index=0
    override fun initView(savedInstanceState: Bundle?) {

        initEvent()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initEvent() {
        mTitles = resources.getStringArray(R.array.str_msg_top)

        mFragments.add(MsgChildFragment.newInstance())
        mFragments.add(MsFriendFragment.newInstance())
        mDatabind.vp.adapter = MyPagerAdapter(childFragmentManager);
        mDatabind.slide.setViewPager(mDatabind.vp)
        mDatabind.vp.currentItem = 0
        mDatabind.slide.setOnTabSelectListener(object : OnTabSelectListener{
            override fun onTabSelect(position: Int) {

                index=position
            }

            override fun onTabReselect(position: Int) {

            }

        })
        setOnclickNoRepeat(mDatabind.ivclear,mDatabind.linsre) {
            when (it.id) {
                R.id.ivclear -> {
                    clearMsg(requireActivity()) { it ->
                        if (it){//点击了确定

                            appViewModel.updateMsgEvent.postValue("-1")
                        }

                    }
                }
                R.id.linsre->{
                    if (index==0) {
                        startNewActivity<SeacherMsgActivity>()
                    }else{
                        startNewActivity<SeacherFriendActivity>()
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