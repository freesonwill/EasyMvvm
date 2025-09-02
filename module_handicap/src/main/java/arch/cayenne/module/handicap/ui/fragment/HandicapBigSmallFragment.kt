package arch.cayenne.module.handicap.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.handicap.databinding.FragmentHandicapBigSmallBinding
import arch.cayenne.module.handicap.ui.adapter.BigSmallAdapter
import arch.cayenne.module.handicap.ui.viewmodel.HandicapBigSmallViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的大小页面
 */

class HandicapBigSmallFragment : BaseFragment<HandicapBigSmallViewModel, FragmentHandicapBigSmallBinding>() {

    override val vbClass: KClass<FragmentHandicapBigSmallBinding> = FragmentHandicapBigSmallBinding::class
    override val vmClass: KClass<HandicapBigSmallViewModel> = HandicapBigSmallViewModel::class

    private var bigSmallAdapter = BigSmallAdapter()

    class BigSmallItemDecoration(
        private val btmSpacing: Int = 12.dp2px,
        private val leftRight: Int = 8.dp2px,
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = btmSpacing
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerBigSmall.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = bigSmallAdapter
            addItemDecoration(BigSmallItemDecoration())
        }
        mViewModel.addLetBallData()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {
        mViewModel.bigSmallData.observe(viewLifecycleOwner) {
            if (it != null) {
                bigSmallAdapter.submitList(it)
            }
        }
    }
}