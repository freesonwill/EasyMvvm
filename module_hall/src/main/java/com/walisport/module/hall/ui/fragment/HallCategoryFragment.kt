package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.R
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HotColdType
import com.walisport.module.hall.databinding.FragmentHallCategoryBinding
import com.walisport.module.hall.ui.adapter.GameContentAdapter
import kotlin.random.Random
import kotlin.reflect.KClass

class HallCategoryFragment: BaseFragment<EmptyViewModel, FragmentHallCategoryBinding>() {
    override val vbClass: KClass<FragmentHallCategoryBinding> = FragmentHallCategoryBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

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

    private val mockList by lazy {
        val l = ArrayList<GameContentData>()
        for (i in 0..23) {
            l.add(
                GameContentData(
                    cover = R.drawable.image_cover_demo,
                    hotOrCold = if (i % 2 == 0) HotColdType.HOT else HotColdType.COLD,
                    percent = 20.0f,
                    onlineCount = Random.nextInt(100,32767)
                )
            )
        }
        l
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar("老虎機")
            customTabGroup.submitTabList(mockVendorList)

            rvGame.layoutManager = GridLayoutManager(requireContext(),  3)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 3,
                horizontalSpacing = 9.dp2px,
                verticalSpacing = 20.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvGame.addItemDecoration(itemDecoration)
            rvGame.adapter = GameContentAdapter().apply {
                submitList(mockList)
            }
        }

        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}