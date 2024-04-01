package com.xcjh.app.ui.home.msg

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.FrMsgBinding
import com.xcjh.app.utils.clearMsg
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.setOnclickNoRepeat

/**
 * 首页聊天
 */
class MsgFragment : BaseFragment<MsgVm, FrMsgBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var index = 0
    var isClick = false
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .navigationBarColor(R.color.c_ffffff)
            .navigationBarDarkIcon(true)
            .titleBar(mDatabind.rlTitle)
            .init()

        initEvent()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //  isVisble = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        super.onResume()
//        Log.i("FFFFFFFFF","3333333333333333")
    }

    private fun initEvent() {
        try {


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
                18f, 16f, true, true,
                R.color.c_34a853, margin = 15
            )

            mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    index = position
                    if (position == 0) {
                        mDatabind.ivclear.visibility = View.VISIBLE
                    } else {
                        mDatabind.ivclear.visibility = View.GONE
                    }
                }

            })

            setOnclickNoRepeat(mDatabind.ivclear) {
                when (it.id) {
                    R.id.ivclear -> {
                        if (isClick) {
                            //点击清除红点
                            clearMsg(requireActivity()) { it ->
                                if (it) {//点击了确定

                                    appViewModel.updateMsgEvent.postValue("-1")
                                }

                            }
                        }
                    }

                }
            }
            appViewModel.updateMainMsgNum.observeForever {
                if (isAdded) {
                    if (it == "0") {
                        isClick = false
                        mDatabind.ivclear.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                        mDatabind.ivclear.setTextColor(resources.getColor(R.color.c_cfd2d4))
                    } else {
                        isClick = true
                        mDatabind.ivclear.setBackgroundResource(R.drawable.select_msg_allread)
                        mDatabind.ivclear.setTextColor(resources.getColor(com.xcjh.base_lib.R.color.white))
                    }
                }
            }
        } catch (e: Exception) {
        }
    }


}