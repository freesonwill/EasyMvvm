package com.cn.game.sdk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cn.game.sdk.R
import com.cn.game.sdk.base.BaseGameActivity
import com.cn.game.sdk.databinding.ActivityCeshiSdkBinding
import com.cn.game.sdk.ui.fast.GameHomeVm
import com.cn.game.sdk.ui.fast.fragment.CeFragment
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment

class CeshiSdkActivity : BaseGameActivity<GameHomeVm,ActivityCeshiSdkBinding >() {
    private var mFragList = ArrayList<Fragment>()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        var homeDefaultFragment = HomeDefaultFragment()
        val basketball = Bundle().apply {
            putInt("type",0)
        }
        homeDefaultFragment.arguments = basketball




        var homeDefaultFragmentNew = CeFragment()
        val basketballNew = Bundle().apply {
            putInt("type",0)
        }
        homeDefaultFragmentNew.arguments = basketballNew
        mFragList.add(homeDefaultFragmentNew)

        var homeDefaultFragmentNew222 = HomeDefaultFragment()
        val basketballNew222 = Bundle().apply {
            putInt("type",0)
        }
        homeDefaultFragmentNew222.arguments = basketballNew222
        mFragList.add(homeDefaultFragmentNew222)
//        mDatabind.viewPager.init(supportFragmentManager,mFragList,arrayListOf(
//            getString(R.string.g_home_txt_default),
//            getString(R.string.g_home_txt_more)))
//        mDatabind.viewPager.offscreenPageLimit =mFragList.size
//        mDatabind.magicIndicator.bindViewPagerNew(mDatabind.viewPager,arrayListOf(
//            getString(R.string.g_home_txt_default),
//            getString(R.string.g_home_txt_more)),scrollEnable=true)


//        mDatabind.viewPager2.initActivity(this, mFragListNew, true,2)
//
//        //初始化 magic_indicator
//        mDatabind.magicIndicator.bindViewPager2(
//            mDatabind.viewPager2, arrayListOf(
//                getString(R.string.g_home_txt_default),
//                getString(R.string.g_home_txt_more)
//
//            ),
//            R.color.g_f7cf41,
//            R.color.g_9696b8,
//            14f, 14f, true, true,
//            0, lineIndicatorWidth=0,margin = 10
//        ){
//            switchFragment(it)
//        }
//        mDatabind.viewPager2.offscreenPageLimit = mFragListNew.size



    }





}