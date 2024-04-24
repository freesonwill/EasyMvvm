package com.cn.game.sdk.ui.fast


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cn.game.sdk.MyGameApplication
import com.cn.game.sdk.R
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameActivity
import com.cn.game.sdk.databinding.ActivityGameHomeBinding
import com.cn.game.sdk.ui.fast.fragment.HomeDefaultFragment
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat


class GameHomeActivity : BaseGameActivity<GameHomeVm, ActivityGameHomeBinding>() {
    private var mFragList = ArrayList<Fragment>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        supportActionBar?.hide()
        // 设置状态栏颜色为透明
        window.statusBarColor = getColor(android.R.color.transparent)
        var homeDefaultFragment = HomeDefaultFragment()
        val basketball = Bundle().apply {
            putInt("type",0)
        }
//        val viewModelProvider = ViewModelProvider(this)
//  viewModelProvider[mViewModel::class.java]
        mViewModel.getddd()
        mDatabind.ivHomeLogo.clickNoRepeat {
            myToast("!1111111111111")
//            appGameViewModel.ceshEvent.postValue(true)
        }
        appGameViewModel.ceshEvent.observe(this){
            Log.i("CCCCCCCCCCCc","333333333")
        }


        homeDefaultFragment.arguments = basketball
        mFragList.add(homeDefaultFragment)

        mDatabind.viewPager.initActivity(this, mFragList, true,1)
        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager, arrayListOf(
                getString(R.string.g_home_txt_default),
                getString(R.string.g_home_txt_more)

            ),
            R.color.g_f7cf41,
            R.color.g_9696b8,
            14f, 14f, true, true,
            0, lineIndicatorWidth=0,margin = 10
        ){

        }

        mDatabind.viewPager.offscreenPageLimit = mFragList.size
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 判断是否按下了返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 在这里执行你想要的操作，比如关闭当前活动
            finish();
            overridePendingTransition(0,  R.anim.slide_down)
            return true; // 返回 true 表示事件已经处理，不会继续传递
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun createObserver() {
        super.createObserver()

    }


}