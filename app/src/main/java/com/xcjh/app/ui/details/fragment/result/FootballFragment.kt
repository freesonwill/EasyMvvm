package com.xcjh.app.ui.details.fragment.result

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModelProvider
import com.xcjh.app.R
import com.xcjh.app.adapter.ImportEventAdapter
import com.xcjh.app.adapter.TextLiveAdapter
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.IncidentsBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.FragmentDetailTabResultTab1Binding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.utils.nice.Utils.dp2px
import com.xcjh.app.utils.setEmpty
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import com.xcjh.base_lib.utils.view.visibleOrGone

/**
 * 其他直播间列表
 * type 0文字直播 1重要事件
 */
class FootballFragment(var type: Int = 0, private var match: MatchDetailBean) :
    BaseFragment<DetailVm, FragmentDetailTabResultTab1Binding>() {
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    private val textAdapter by lazy {
        TextLiveAdapter().apply {
            setLogo(
                match.homeLogo,
                match.awayLogo
            )
        }
    }
    private val eventAdapter by lazy { ImportEventAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        //文字直播和重要事件列表适配器
        mDatabind.rcvFootball.run {
            vertical()
            adapter = if (type == 0) textAdapter else eventAdapter
            distance(0, 0, 0, 0)
        }
       mDatabind.footballBottom.visibleOrGone(type==0)
    }

    override fun createObserver() {
        //文字直播
        vm.text.observe(this) {
            if (type == 1) return@observe
            if (it != null && it.size > 0) {
                textAdapter.submitList(it)
            } else {
                textAdapter.submitList(null)
                textAdapter.isEmptyViewEnable = true
                textAdapter.emptyView = layoutInflater.inflate(R.layout.layout_empty, null)
                // LayoutEmptyBinding.inflate(LayoutInflater.from(context), null, false).root
            }
        }

        //重要事件
        vm.incidents.observe(this) {
            if (type == 0) return@observe
            if (it != null && it.size > 0) {
                var event = ArrayList<IncidentsBean>()
                var homeS = 0//主场得分
                var awayS = 0//客队得分
                val reversed = it.reversed()
                for (item: IncidentsBean in reversed) {
                    //重要事件  红黄牌 进球 换人
                    when (item.type) {
                        1, 3, 4, 9, 11, 12, 15 -> {
                            //中场数据获取
                            if (item.type == 1) {
                                homeS = item.homeScore
                                awayS = item.awayScore
                            }
                            if (item.type == 11) {
                                item.homeScore = homeS
                                item.awayScore = awayS
                            }
                            if (item.type == 12) {
                                item.homeScore = homeS
                                item.awayScore = awayS
                            }
                            event.add(item)
                        }
                    }
                }
                val reversed1 = event.reversed() as ArrayList
                reversed1.add(IncidentsBean(type = 0, time = 0))
                eventAdapter.submitList(reversed1)
            } else {
                eventAdapter.submitList(null)
                eventAdapter.isEmptyViewEnable = true
                eventAdapter.emptyView = setEmpty(requireContext(), isCenter = false, marginT = 32, marginB = 52)
            }
        }
    }
}