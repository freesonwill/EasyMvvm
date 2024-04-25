package com.cn.game.sdk.ui.fast.fragment

import android.os.Bundle
import android.widget.RelativeLayout
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.databinding.FragmentCeBinding
import com.cn.game.sdk.view.OpenResultView
import com.xcjh.base_lib.utils.view.clickNoRepeat


class CeFragment  : BaseGameFragment<HomeDefaultVm, FragmentCeBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

        mDatabind.btnCeshi.clickNoRepeat {
            var dd= OpenResultView(requireContext())

            // 设置新 View 的宽度和高度
            // 设置新 View 的宽度和高度
            val width = 200 // 设置宽度

            val height = 200 // 设置高度

            val params = RelativeLayout.LayoutParams(width, height)

            // 设置新 View 在 xy 轴上的位置

            // 设置新 View 在 xy 轴上的位置
            val xPosition = 100 // 设置 x 轴位置

            val yPosition = -130 // 设置 y 轴位置

            params.leftMargin = xPosition // 设置左边距

            params.topMargin = yPosition // 设置上边距


            params.leftMargin = xPosition // 设置左边距
            mDatabind.rlRoot.addView(dd,params)

        }

    }


}