package com.cn.game.sdk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.cn.game.sdk.R
import com.cn.game.sdk.base.BaseGameActivity
import com.cn.game.sdk.databinding.ActivityCeshiSdkBinding
import com.cn.game.sdk.databinding.ActivityGameHomeBinding
import com.cn.game.sdk.ui.fast.GameHomeVm
import com.cn.game.sdk.ui.fast.fragment.CeFragment
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity

class CeshiSdkActivity : BaseGameActivity<GameHomeVm,ActivityCeshiSdkBinding >() {
    private var mFragList = ArrayList<Fragment>()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        var homeDefaultFragment = HomeDefaultFragment()
        val basketball = Bundle().apply {
            putInt("type",0)
        }
        homeDefaultFragment.arguments = basketball

        mFragList.add(CeFragment())
        mFragList.add(homeDefaultFragment)
        mDatabind.viewPager2.initActivity(this, mFragList, true,2)

        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager2, arrayListOf(
                getString(R.string.g_home_txt_default),
                getString(R.string.g_home_txt_more)

            ),
            R.color.g_f7cf41,
            R.color.g_9696b8,
            14f, 14f, true, true,
            0, lineIndicatorWidth=0,margin = 10
        ){

        }
        mDatabind.viewPager2.offscreenPageLimit = mFragList.size
    }

}