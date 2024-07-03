package com.cn.game.sdk2.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentGamehallBinding
import com.cn.game.sdk2.ui.helper.ViewHelper.initGameViewPager

/**
 * 游戏大厅
 */
class Fast3GameHall @JvmOverloads constructor( context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0 ) : RelativeLayout(context, attrs, defStyleAttr) {


    /** 初始化 **/
    fun onInit(){
        val mDatabind = FragmentGamehallBinding.bind(this)
        /*mDatabind.viewPagerNew.initGameViewPager(
            childFragmentManager, mFragList, arrayListOf(
                requireContext().getString(R.string.g_home_txt_default),
                requireContext().getString(R.string.g_home_tab_single),
                requireContext().getString(R.string.g_home_tab_sum),
                requireContext().getString(R.string.g_home_tab_double),
                requireContext().getString(R.string.g_home_tab_leopard)
            )
        )*/
    }

}