package arch.cayenne.module.betslip.utisl

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.view.PullRefreshLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.ui.adapter.BetSlipAdapter

object BetSlipViewExt {

    /**
     * PullRefreshLayout 打开上拉加载和下啦刷新
     * */
    internal fun PullRefreshLayout.initLoadMore() {
        setEnableRefresh(true)
        setEnableLoadMore(true)
        setEnableScrollContentWhenRefreshed(true)
        setEnableScrollContentWhenLoaded(true)
    }

    /**
     * DynamicStateLayout 空数据的时候展示
     * */
    internal fun DynamicStateLayout.showEmptyData(showEmpty: Boolean, otherView: View) {
        if (showEmpty) {
            isVisible = true
            otherView.isVisible = false
            setState(
                DynamicStateLayout.States.DATA_EMPTY, getString(context, R.string.lineup_empty)
            )
        } else {
            this.isVisible = false
            otherView.isVisible = true
        }
    }

    /**
     * recylerView添加Divider 设置缓存区
     * */
    internal fun RecyclerView.betSlipInit() {
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ContextCompat.getDrawable(
                context, R.drawable.item_divide_live_bet_recycler
            )!!
        )
        setItemViewCacheSize(10)
        addItemDecoration(divider)
        itemAnimator = null
        setRecycledViewPool(RecyclerView.RecycledViewPool())
    }

    /**
     * PullRefreshLayout 加载更新数据
     * */
    internal fun PullRefreshLayout.loadMoreData(
        adapter: BetSlipAdapter,
        newList: List<BetSlipData>
    ) {
        val allList = mutableListOf<BetSlipData>()
        if (adapter.currentList.isNotEmpty()) {
            allList.addAll(adapter.currentList)
        }
        allList.addAll(newList)
//        val recyclerViewState = mBinding.recyclerView.layoutManager?.onSaveInstanceState()
        adapter.submitList(newList) {
            finishLoadMoreWithNoMoreData()
//            mBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

    }
}