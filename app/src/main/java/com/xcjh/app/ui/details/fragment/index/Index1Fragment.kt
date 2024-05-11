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
import com.xcjh.app.databinding.FragmentDetailTabIndexTab1Binding
import com.xcjh.app.databinding.ItemDetailMultiTableBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.utils.setStateEmpty
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 其他直播间列表
 * matchType 1足球，2篮球
 */
class Index1Fragment() : BaseFragment<DetailVm, FragmentDetailTabIndexTab1Binding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {
        setStateEmpty(requireContext(), mDatabind.state,isCenter = false, marginT = 32, marginB = 52)
        mDatabind.rcvCommon.setHasFixedSize(true)
        mDatabind.rcvCommon.linear().setup {
            lateinit var mAgentWeb: AgentWeb
            addType<OddsDetailBean> {
                R.layout.item_detail_multi_table // 我发的消息
            }
            onBind {
                when (val item = _data) {
                    is OddsDetailBean -> {
                        val binding = getBinding<ItemDetailMultiTableBinding>()

                        if (modelPosition == 0){
                            //binding.root.setBackgroundDrawable(context.getDrawable(R.drawable.shape_bottom_r10))
                            binding.vLine.visibleOrGone(false)
                        }
                        if (modelPosition+1 == models?.size){
                            binding.lltContent.setBackgroundResource(R.drawable.shape_bottom_r10)
                        }
                        binding.lltContent.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, if (modelPosition%2==0) R.color.c_181819 else R.color.c_1D1D1D)
                        )
//                        if (item.companyName.isNotEmpty()) {
//                            if (item.companyName.length > 2) {
//                                binding.tvCompany.text = item.companyName.substring(0 until 2) + "*"
//                            } else {
//                                binding.tvCompany.text = item.companyName.substring(0 until 1) + "*"
//                            }
//                        }

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
                        binding.tvChuHome.text = item.firstHomeWin//主胜
                        binding.tvChuPin.text = item.firstDraw//平
                        binding.tvChuKe.text = item.firstAwayWin
                        try {
                            binding.tvChuPf.text = "${(item.firstLossRatio.toFloat()*100).toInt()}%"//赔付率
                        }catch (e:Exception){
                            binding.tvChuPf.text = "-"//赔付率
                        }

                        if (item.close == 1) {
                            //即盘数据 封
                            binding.tvJiHome.text = context.getString(R.string.close_win_p) //主胜
                            binding.tvJiPin.text = context.getString(R.string.close_win_p)
                            binding.tvJiAway.text = context.getString(R.string.close_win_p)
                            binding.tvJiPf.text = context.getString(R.string.close_win_p)
                            binding.tvJiHome.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                            binding.tvJiPin.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                            binding.tvJiAway.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                            binding.tvJiPf.setTextColor(requireContext().getColor(R.color.c_d7d7e7))
                        } else {
                            //即盘数据
                            binding.tvJiHome.text = item.currentHomeWin //主胜
                            binding.tvJiPin.text = item.currentDraw//平
                            binding.tvJiAway.text = item.currentAwayWin//客胜

                            setColor(binding.tvJiHome, item.firstHomeWin.toFloat(), item.currentHomeWin.toFloat())
                            setColor(binding.tvJiPin, item.firstDraw.toFloat(), item.currentDraw.toFloat())
                            setColor(binding.tvJiAway, item.firstAwayWin.toFloat(), item.currentAwayWin.toFloat())
                            try {
                                binding.tvJiPf.text = "${(item.currentLossRatio.toFloat()*100).toInt()}%"//赔付率
                            }catch (e:Exception){
                                binding.tvJiPf.text = "-"//赔付率
                            }
                        }
                    }
                }
            }
        }
    }

    override fun createObserver() {
        vm.odds.observe(this) {
            if (it != null) {
                //mDatabind.rcvCommon.models = (list)
                if (it.euInfo == null || it.euInfo?.size == 0) {
                    mDatabind.state.showEmpty()
                }else{
                    mDatabind.state.showContent()
                    mDatabind.rcvCommon.models=(it.euInfo)
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