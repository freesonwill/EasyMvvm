package arch.cayenne.module.handicap.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.handicap.databinding.FragmentHandicapLetBallBinding
import arch.cayenne.module.handicap.ui.adapter.LetBallAdapter
import arch.cayenne.module.handicap.ui.viewmodel.HandicapLetBallViewModel
import kotlin.reflect.KClass

/**
 * 盘口教程页面下的让球页面
 */

class HandicapLetBallFragment :
    BaseFragment<HandicapLetBallViewModel, FragmentHandicapLetBallBinding>() {

    override val vbClass: KClass<FragmentHandicapLetBallBinding> =
        FragmentHandicapLetBallBinding::class
    override val vmClass: KClass<HandicapLetBallViewModel> = HandicapLetBallViewModel::class
    private var letBallAdapter = LetBallAdapter()

    class StandingsItemDecoration(
        private val spacing: Int = 12.dp2px,
        private val leftRight: Int = 8.dp2px,
        private val bottomSpacing: Int = 20.dp2px,
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
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
        mBinding.recyclerLetBall.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = letBallAdapter
            addItemDecoration(StandingsItemDecoration())
        }
        mViewModel.addLetBallData()
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.letBallData.observe(this) {
            if (it != null) {
                letBallAdapter.submitList(it)
            }
        }
    }
}