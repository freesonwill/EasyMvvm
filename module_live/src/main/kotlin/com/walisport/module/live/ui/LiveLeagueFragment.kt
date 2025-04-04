package com.walisport.module.live.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.ui.adapter.LeagueAdapter
import com.walisport.module.live.adapter.LeagueAdapter
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.databinding.FragmentLeagueBinding
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveLeagueFragment : BaseFragment<LeagueViewModel, FragmentLeagueBinding>() {
    override val vbClass: KClass<FragmentLeagueBinding> = FragmentLeagueBinding::class
    override val vmClass: KClass<LeagueViewModel> = LeagueViewModel::class

    private val itemDecoration: ItemDecoration = object : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(8.dp2px, 8.dp2px, 12.dp2px, 0)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerLeague.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = LeagueAdapter().apply {
                addItemDecoration(itemDecoration)
                val week1 = LeagueMatchBean(0, true, "12月8日 星期四", 0, "", "", "", "0")
                val week2 = LeagueMatchBean(0, true, "12月10日 星期六", 0, "", "", "", "0")
                val temp = LeagueMatchBean(0, false, "", 10001010, "", "", "阿森纳", "曼城")
                val list = listOf(temp, week1, temp, temp, temp, week2, temp, temp)
                submitList(list)
            }
        }
    }

    override fun initListener() {
        mBinding.ivLeagueClose.clickNoRepeat{
            findNavController().navigateUp()
        }
    }

    override fun createObserver() {

    }
}