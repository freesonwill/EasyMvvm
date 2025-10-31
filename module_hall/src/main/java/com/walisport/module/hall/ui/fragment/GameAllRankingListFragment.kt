package com.walisport.module.hall.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameAllRankingListData
import com.walisport.module.hall.databinding.FragmentGameAllRankingListBinding
import com.walisport.module.hall.ui.adapter.GameAllRankingListAdapter
import kotlin.reflect.KClass

class GameAllRankingListFragment : BaseFragment<EmptyViewModel, FragmentGameAllRankingListBinding>() {
    companion object {
        fun newInstance() = GameAllRankingListFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingListBinding> = FragmentGameAllRankingListBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    // 在 Fragment 的頂部定義一個變數來持有對父 ViewPager 的弱引用
//    private var parentViewPagerRef: WeakReference<ViewPager2>? = null

    val mockList by lazy {
        val l = arrayListOf<GameAllRankingListData>()
        for (i in 0..9) {
            l.add(
                GameAllRankingListData(
                    gameIcon = R.drawable.ic_little_tiger,
                    gameName = getString(R.string.tiger),
                    multiple = i * 20f,
                    countryIcon = arch.cayenne.lib.common.R.drawable.ic_usdt,
                    symbol = "¥",
                    result = 23456f
                )
            )
        }
        l
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
//        findParentViewPager(mBinding.root)
        with(mBinding) {
            rvCurrentRank.layoutManager = LinearLayoutManager(requireContext())
            rvCurrentRank.adapter = GameAllRankingListAdapter().apply {
                submitList(mockList)
            }

        }
    }

//    private fun findParentViewPager(view: View) {
//        var parent = view.parent
//        while (parent != null && parent !is ViewPager2) {
//            parent = parent.parent
//        }
//        if (parent is ViewPager2) {
//            parentViewPagerRef = WeakReference(parent)
//        }
//    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }


}