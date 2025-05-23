package arch.cayenne.module.handicap.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.handicap.databinding.FragmentHandicapCornerBinding
import arch.cayenne.module.handicap.ui.adapter.CornerBallAdapter
import arch.cayenne.module.handicap.ui.viewmodel.HandicapCornerViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的角球页面
 */

class HandicapCornerFragment :
    BaseFragment<HandicapCornerViewModel, FragmentHandicapCornerBinding>() {

    override val vbClass: KClass<FragmentHandicapCornerBinding> =
        FragmentHandicapCornerBinding::class
    override val vmClass: KClass<HandicapCornerViewModel> = HandicapCornerViewModel::class
    private var cornerAdapter = CornerBallAdapter()

    class CornerItemDecoration(
        private val spacing: Int = 12.dp2px,
        private val leftRight: Int = 8.dp2px,
        private val bottomSpacing: Int = 20.dp2px,
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount ?: 0
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerCorner.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cornerAdapter
            addItemDecoration(CornerItemDecoration())
        }
        mViewModel.addCornerBallData()
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.cornerBallData.observe(viewLifecycleOwner) {
            if (it != null) {
                cornerAdapter.submitList(it)
            }
        }
    }
}