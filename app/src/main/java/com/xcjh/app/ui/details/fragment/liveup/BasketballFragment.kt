package com.xcjh.app.ui.details.fragment.liveup

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.NoticeBean
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.FragmentDetailTabIndexTab1Binding
import com.xcjh.app.databinding.FragmentDetailTabLiveupTabBinding
import com.xcjh.app.ui.details.DetailVm

/**
 * 其他直播间列表
 * type 0客队，1主队
 */
class BasketballFragment(var type: Int = 0, ) : BaseFragment<DetailVm, FragmentDetailTabLiveupTabBinding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun createObserver() {
        vm.basket.observe(this) {
            if (it != null) {
                if (type==0){
                    mDatabind.viewBasketballLineup.setData(it.away)
                }else{
                    mDatabind.viewBasketballLineup.setData(it.home)
                }
            }
        }
    }
}