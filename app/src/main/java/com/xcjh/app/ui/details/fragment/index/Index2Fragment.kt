package com.xcjh.app.ui.details.fragment.index

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.just.agentweb.AgentWeb
import com.xcjh.app.R
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.FragmentDetailTabIndexTab2Binding
import com.xcjh.app.databinding.ItemDetailTableBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.utils.setStateEmpty
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 *
 * type 1让球，2进球数
 */
class Index2Fragment(val type:Int=1) : BaseFragment<DetailVm, FragmentDetailTabIndexTab2Binding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {
        setStateEmpty(requireContext(), mDatabind.state,isCenter = false, marginT = 32, marginB = 52)
        mDatabind.rcvCommon.setHasFixedSize(true)
        mDatabind.rcvCommon.linear().setup {
            addType<OddsDetailBean> {
                R.layout.item_detail_table // 我发的消息
            }
            onBind {
                when (val item = _data) {
                    is OddsDetailBean -> {
                        val binding = getBinding<ItemDetailTableBinding>()

                        if (modelPosition == 0){
                            //binding.root.setBackgroundDrawable(context.getDrawable(R.drawable.shape_bottom_r10))
                            binding.vLine.visibleOrGone(false)
                        }
                        if (modelPosition+1 ==  models?.size){
                            binding.lltContent.setBackgroundResource(R.drawable.shape_bottom_r10)
                        }
                        binding.lltContent.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, if (modelPosition%2==0) R.color.c_181819 else R.color.c_1D1D1D)
                        )
                        if (item.companyName.isNotEmpty()) {
                            when (item.companyName.length) {
                                in 3..6 -> {
                                    binding.tvCompany.text = item.companyName.substring(0 until 2) + "*".repeat(item.companyName.length-2)
                                }
                                2 -> {
                                    binding.tvCompany.text = item.companyName.substring(0 until 1) + "*"
                                }
                                1 -> {
                                    binding.tvCompany.text = item.companyName
                                }
                                else -> {
                                    //大于6个字
                                    binding.tvCompany.text = item.companyName.substring(0 until 2) + "****"
                                }
                            }
                        }
                        //初盘数据
                        binding.tvChuZ.text = item.firstHomeWin
                        binding.tvChuP.text = item.firstDraw
                        binding.tvChuK.text = item.firstAwayWin

                        //即盘数据
                        if (item.close == 1) {
                            binding.tvJiZ.text = context.getString(R.string.close_win_p)
                            binding.tvJiP.text = context.getString(R.string.close_win_p)
                            binding.tvJiK.text = context.getString(R.string.close_win_p)
                            binding.tvJiZ.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                            binding.tvJiP.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                            binding.tvJiK.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                        } else {
                            binding.tvJiZ.text = item.currentHomeWin
                            binding.tvJiP.text = item.currentDraw
                            binding.tvJiK.text = item.currentAwayWin
                            setColor(binding.tvJiZ, item.firstHomeWin.toFloat(), item.currentHomeWin.toFloat())
                            setColor(binding.tvJiP, item.firstDraw.toFloat(), item.currentDraw.toFloat())
                            setColor(binding.tvJiK, item.firstAwayWin.toFloat(), item.currentAwayWin.toFloat())
                        }
                    }
                }
            }
        }
        /*
      */
    }

    override fun createObserver() {
        vm.odds.observe(this) {
            if (it != null) {
                if (type==1){
                    if (it.asiaInfo == null || it.asiaInfo?.size == 0) {
                        mDatabind.state.showEmpty()
                    }else{
                        mDatabind.state.showContent()
                        mDatabind.rcvCommon.models=(it.asiaInfo)
                    }
                }else{
                    if (it.bsInfo == null || it.bsInfo?.size == 0) {
                        mDatabind.state.showEmpty()
                    }else{
                        mDatabind.state.showContent()
                        mDatabind.rcvCommon.models=(it.bsInfo)
                    }
                }
            }
        }
    }
    private fun setColor(text: TextView, a: Float, b: Float) {
        if (a < b) {
            text.setTextColor(requireContext().getColor(R.color.c_D63823))
        } else if (a > b) {
            text.setTextColor(requireContext().getColor(R.color.c_pb_bar))
        } else {
            text.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
        }
    }
}