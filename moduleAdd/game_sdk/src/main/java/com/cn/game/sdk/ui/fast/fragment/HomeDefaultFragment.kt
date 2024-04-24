package com.cn.game.sdk.ui.fast.fragment

import android.os.Bundle
import android.util.Log
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.base.BaseGameFragment
import com.cn.game.sdk.databinding.FragmentHomeDefaultBinding
import com.xcjh.base_lib.utils.view.clickNoRepeat

class HomeDefaultFragment : BaseGameFragment<HomeDefaultVm, FragmentHomeDefaultBinding>() {
    var type:Int=0


    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            type = it.getInt("type")
        }
        mDatabind.btn.clickNoRepeat {
            mViewModel.getShow()
        }

        appGameViewModel.ceshEvent.observe(this){

        }

    }
}