package arch.cayenne.module.home.ui.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.MatchListItem
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.module.home.data.model.MatchLoadMoreData
import arch.cayenne.module.home.data.model.MatchNoMoreData
import arch.cayenne.module.home.databinding.ItemLoadMoreDataBinding
import arch.cayenne.module.home.databinding.ItemMatchCardBinding
import arch.cayenne.module.home.databinding.ItemNoMoreDataBinding
import arch.cayenne.module.home.ui.adapter.compare.MatchItemCompare
import org.koin.java.KoinJavaComponent.getKoin
import java.lang.ref.WeakReference

class MatchItemAdapter(private val onMatchItemClickListener: OnMatchItemClickListener? = null) :
    BaseAdapter<MatchListItem, BaseViewHolder, ViewBinding>(MatchItemCompare()) {

    private val viewPool = RecyclerView.RecycledViewPool()
    private var showNoMoreData: Boolean = false

    private var lastItemType: Int = LAST_ITEM_LOAD_MORE

    private val mAnimation by lazy {
        AnimationUtils.loadAnimation(
            getKoin().get<Application>(),
            arch.cayenne.lib.common.R.anim.anim_loading
        )
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MatchWithMarkets -> TYPE_MATCH_ITEM
            is MatchNoMoreData -> TYPE_NO_MORE_DATA
            is MatchLoadMoreData -> TYPE_LOAD_MORE
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun submitList(list: List<MatchListItem>?) {
        super.submitList(list.addLastItem())
    }

    override fun submitList(list: List<MatchListItem>?, commitCallback: Runnable?) {
        super.submitList(list.addLastItem(), commitCallback)
    }

    fun List<MatchListItem>?.addLastItem(): List<MatchListItem>? {
        val isEmpty = (this?.size ?: 0) == 0
        val l = if (isEmpty || lastItemType == LAST_ITEM_NONE) {
            this?.toMutableList()
        } else if (lastItemType == LAST_ITEM_NO_MORE){
            this?.toMutableList()?.apply { add(MatchNoMoreData) }
        } else if (lastItemType == LAST_ITEM_LOAD_MORE) {
            this?.toMutableList()?.apply { add(MatchLoadMoreData) }
        } else {
            this?.toMutableList()
        }
        return l
    }

    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ViewBinding,
        position: Int
    ) {
        val item = getItem(position)
        when(item) {
            is MatchWithMarkets -> {
                val matchItemViewHolder = (holder as MatchItemViewHolder)
                matchItemViewHolder.init(item)
            }
            is MatchNoMoreData -> Unit
            is MatchLoadMoreData -> {
                val loadMoreDataBinding = binding as ItemLoadMoreDataBinding
                loadMoreDataBinding.ivProgress.startAnimation(mAnimation)
            }
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when(viewType) {
            TYPE_MATCH_ITEM -> ItemMatchCardBinding.inflate(inflater, parent, false)
            TYPE_NO_MORE_DATA -> ItemNoMoreDataBinding.inflate(inflater, parent, false)
            TYPE_LOAD_MORE -> ItemLoadMoreDataBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun createViewHolder(
        binding: ViewBinding,
        viewType: Int
    ): BaseViewHolder {
        return when(viewType) {
            TYPE_MATCH_ITEM ->  MatchItemViewHolder(binding as ItemMatchCardBinding, onMatchItemClickListener, viewPool)
            TYPE_NO_MORE_DATA -> BaseViewHolder(binding)
            TYPE_LOAD_MORE -> BaseViewHolder(binding)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (holder !is MatchItemViewHolder) {
            super.onBindViewHolder(holder, position, payloads)
        }
        if (holder is MatchItemViewHolder && payloads.isNotEmpty()) {
            val item = getItem(holder.absoluteAdapterPosition) as MatchWithMarkets
            holder.bindPayload(item, payloads)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    fun setLastItemType(type: Int) {
        lastItemType = type
    }

    companion object {
        private const val TYPE_MATCH_ITEM = 0
        private const val TYPE_NO_MORE_DATA = 1
        private const val TYPE_LOAD_MORE = 2

        const val LAST_ITEM_NONE = 0
        const val LAST_ITEM_LOAD_MORE = 1
        const val LAST_ITEM_NO_MORE = 2
    }
}

interface OnMatchItemClickListener {
    fun onLiveEntryClick(item: MatchWithMarkets)
    fun onFavoriteClick(view: ImageView, item: MatchWithMarkets)
    fun onOddsCellClick(cell: WeakReference<View>, selection: SelectionBeanLite, x: Float, y: Float)
}