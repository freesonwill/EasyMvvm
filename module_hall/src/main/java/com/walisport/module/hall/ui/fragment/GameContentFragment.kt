package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.helper.BackToTopHelper
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.databinding.FragmentGameContentBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import kotlin.random.Random
import kotlin.reflect.KClass

class GameContentFragment : BaseFragment<EmptyViewModel, FragmentGameContentBinding>() {
    companion object {
        fun newInstance() = GameContentFragment()
    }
    override val vbClass: KClass<FragmentGameContentBinding> = FragmentGameContentBinding::class
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

    private val mockVendorList by lazy {
        val l = ArrayList<SimpleTabDataModel>()
        for (i in 0..5) {
            l.add(
                SimpleTabDataModel(
                    id = i,
                    simpleName = getString(R.string.wali),
                    icon = "",
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
            rvGame.adapter = GameContentAdapter(onItemClick = {
                navigate(arch.cayenne.lib.res.R.string.nav_module_gamedetail.deeplink())
            }).apply {
                submitList(mockList)
            }
            customTabGroup.submitTabList(mockVendorList)
            BackToTopHelper(rvGame, ivBackToTop)
        }
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}