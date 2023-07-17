package com.xcjh.app.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.xcjh.base_lib.utils.bindHomeViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityHomeBinding
import com.xcjh.app.ui.home.home.HomeFragment
import com.xcjh.app.ui.home.msg.MsgFragment
import com.xcjh.app.ui.home.schedule.ScheduleFragment
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.vm.MainVm

/**
 * 版本2 主页
 *
 */
class MainActivity : BaseActivity<MainVm, ActivityHomeBinding>() {

    private var mFragList: ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        ScheduleFragment(),//赛程
        MsgFragment(),
        ScheduleFragment(),
    )

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        CacheUtil.setFirst(false)
        //初始化viewpager2
        mViewBind.viewPager.initActivity(this, mFragList, false)
        //初始化 magic_indicator
        mViewBind.magicIndicator.bindHomeViewPager2(
            mViewBind.viewPager,
            arrayListOf(
                getString(R.string.Home),
                getString(R.string.Schedule),
                getString(R.string.Msg),
                getString(R.string.Mine)
            ),
            icons = arrayListOf(
                R.drawable.matches_h,
                R.drawable.account_h,
                R.drawable.matches_h,
                R.drawable.account_h,
            ),
            selectSize = 12f,
            unSelectSize = 12f,
            selectColor = R.color.c_main_txt,
            normalColor = R.color.c_8c8c8c,
            typefaceBold = false,
            scrollEnable = false,
            margin=5
        )
        mViewBind.viewPager.offscreenPageLimit = mFragList.size
       // mViewModel.getData()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    myToast(getString(R.string.exit_app))
                    exitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
        })
    }

    public override fun onStart() {
        super.onStart()
    }

    private var exitTime: Long = 0
   /* override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                myToast(getString(R.string.exit_app))
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }*/

    override fun createObserver() {
        super.createObserver()
    }


}