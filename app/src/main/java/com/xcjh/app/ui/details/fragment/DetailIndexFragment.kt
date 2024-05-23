package com.xcjh.app.ui.details.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.databinding.FragmentDetailTabIndexBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.details.fragment.index.Index1Fragment
import com.xcjh.app.ui.details.fragment.index.Index2Fragment
import com.xcjh.base_lib.utils.bindBgViewPager2
import com.xcjh.base_lib.utils.initFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 指数
 */

class DetailIndexFragment(var matchId: String = "", var matchType: String = "1") :
    BaseVpFragment<DetailVm, FragmentDetailTabIndexBinding>() {

    // 在你的类中定义一个变量来存储协程的引用
    private var myCoroutine: Job? = null
    override val typeId: Long
        get() = 5

    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }


    override fun initView(savedInstanceState: Bundle?) {

        if ("1" == matchType) {//1：足球；2：篮球，这个版本篮球暂时不做，因为没有数据
            mDatabind.viewPager.initFragment(this, arrayListOf(Index1Fragment(), Index2Fragment(1), Index2Fragment(2)),isUserInputEnabled=true)
            mDatabind.magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg)
            mDatabind.magicIndicator.bindBgViewPager2(
                mDatabind.viewPager,
                arrayListOf(getString(R.string.win_loss),getString(R.string.handicap),getString(R.string.goal_num)),
                selectSize = 13f,
                unSelectSize = 13f,
                selectColor = com.xcjh.base_lib.R.color.white,
                normalColor = R.color.c_94999f,
                typefaceBold = true,
                scrollEnable = false,
                paddingH = 25.0,
                lineIndicatorColor = R.color.c_323235
            )
            mDatabind.viewPager.offscreenPageLimit = 3
        } else {
            //mDatabind.layTabIndexFootball.visibility = View.GONE
        }
        loadData()


        // 启动协程并存储引用
        myCoroutine = GlobalScope.launch(Dispatchers.Main) {
            // 使用 repeat 循环确保每隔10秒执行一次
            repeat(Int.MAX_VALUE) {
                // 每次执行前延迟10秒
                delay(10000L)
                // 在这里添加你想要执行的操作
                Log.i("GGGGGGG","执行-==================")
                if (isAdded && !isFirst) {
                    loadData()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(myCoroutine!=null){
            myCoroutine!!.cancel()
        }
    }
    override fun lazyLoadData() {
        //ViewModelProvider.get()
       // loadData()
    }

    private fun loadData() {
        //ViewModelProvider.get()
        vm.getOddsInfo(matchId)
    }


    override fun createObserver() {
        //appViewModel.appPolling.observeForever {
        appViewModel.appPolling.observe(activity as MatchDetailActivity) {
//            if (isAdded && !isFirst) {
//                loadData()
//            }
        }

    }
}