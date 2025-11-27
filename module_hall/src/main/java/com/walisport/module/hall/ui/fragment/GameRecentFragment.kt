package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.databinding.FragmentGameRecentBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import kotlin.random.Random
import kotlin.reflect.KClass

class GameRecentFragment : BaseFragment<EmptyViewModel, FragmentGameRecentBinding>() {
    companion object {
        fun newInstance() = GameRecentFragment()
    }
    override val vbClass: KClass<FragmentGameRecentBinding> = FragmentGameRecentBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    private val mockList by lazy {
        val l = ArrayList<GameContentData>()
        for (i in 0..21) {
            l.add(
                GameContentData(
                    cover = R.drawable.image_cover_demo,
                    hotOrCold = HotColdType.NONE,
                    percent = 20.0f,
                    onlineCount = Random.nextInt(100,32767)
                )
            )
        }
        l
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvGame.layoutManager = GridLayoutManager(requireContext(),  3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 9.dp2px,
                verticalSpacing = 12.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvGame.addItemDecoration(itemDecoration)
            rvGame.adapter = GameContentAdapter().apply {
                submitList(mockList)
            }
            BackToTopHelper(rvGame, ivBackToTop)
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}