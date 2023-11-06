package com.xcjh.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.xcjh.app.R
import com.xcjh.app.bean.DiffEntity
import com.xcjh.app.databinding.EmptyViewBinding
import com.xcjh.app.databinding.ItemDifferBinding
import com.xcjh.app.databinding.LoadingBinding

/**
 * test
 */
class TestAdapter : BaseQuickAdapter<DiffEntity, TestAdapter.VH>() {

    // 自定义ViewHolder类
    class VH(
        parent: ViewGroup,
        val binding: ItemDifferBinding = ItemDifferBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: DiffEntity?) {
        // 设置item数据
        if (item != null) {
            holder.binding.tvId.text = item.id.toString()
            holder.binding.tvTitle.text = item.title
        }
    }

}


class Test1Adapter : BaseViewBindingQuickAdapter<DiffEntity, ItemDifferBinding>() {

    override fun onBindViewHolder(holder: VH<ItemDifferBinding>, position: Int, item: DiffEntity?) {
        val binding = holder.binding as ItemDifferBinding
        if (item != null) {
            binding.tvId.text = item.id.toString()
            binding.tvTitle.text = item.title
        }
    }

}

class Test2Adapter : BaseQuickAdapter<DiffEntity, QuickViewHolder>() {

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        // 返回一个 ViewHolder
        return QuickViewHolder(R.layout.item_differ, parent)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DiffEntity?) {
        // 设置item数据
        if (item != null) {
            holder.setText(R.id.tvId, item.id.toString()).setText(R.id.tvTitle, item.title)
        }
    }

    /*  override var items: List<DiffEntity>
          get() {
              return super.items
          }
          set(value) {
              super.items = value
          }*/
}

private fun getDiffUtilMore(): MutableList<DiffEntity> {
    val list: MutableList<DiffEntity> = arrayListOf()
    var n = 100
    for (i in n until n + 5) {
        list.add(
            DiffEntity(
                i,
                "Item $i",
                "This item $i content",
                "06-12"
            )
        )
    }
    return list
}

class TestMultiAdapter : BaseMultiItemAdapter<DiffEntity>(arrayListOf()) {

    // 类型 1 的 viewholder
    class ItemVH(val binding: ItemDifferBinding) : RecyclerView.ViewHolder(binding.root)

    // 类型 2 的 viewholder
    class HeaderVH(val binding: LoadingBinding) : RecyclerView.ViewHolder(binding.root)

    // 在 init 初始化的时候，添加多类型
    init {
        addItemType(ITEM_TYPE, object : OnMultiItemAdapterListener<DiffEntity, ItemVH> { // 类型 1
            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ItemVH {
                // 创建 viewholder
                val viewBinding =
                    ItemDifferBinding.inflate(LayoutInflater.from(context), parent, false)
                return ItemVH(viewBinding)
            }

            override fun onBind(holder: ItemVH, position: Int, item: DiffEntity?) {
                // 绑定 item 数据
                if (item != null) {
                    holder.binding.tvId.text = item.id.toString()
                    holder.binding.tvTitle.text = item.title
                }
            }
            override fun onBind(holder: ItemVH, position: Int, item: DiffEntity?, payloads: List<Any>) {
                if (item!=null){
                    if (payloads.isNotEmpty()){

                        holder.binding.tvTitle.text = item.title+"===="
                    }
                }
            }

            override fun isFullSpanItem(itemType: Int): Boolean {
                // 使用GridLayoutManager时，此类型的 item 是否是满跨度
                return false
            }
        }).addItemType(
            SECTION_TYPE,
            object : OnMultiItemAdapterListener<DiffEntity, HeaderVH> { // 类型 2
                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): HeaderVH {
                    // 创建 viewholder
                    val viewBinding =
                        LoadingBinding.inflate(LayoutInflater.from(context), parent, false)
                    return HeaderVH(viewBinding)
                }

                override fun onBind(holder: HeaderVH, position: Int, item: DiffEntity?) {
                    // 绑定 item 数据
                }
                override fun onBind(holder: HeaderVH, position: Int, item: DiffEntity?, payloads: List<Any>) {
                    if (item!=null){

                    }
                }
                override fun isFullSpanItem(itemType: Int): Boolean {
                    // 使用GridLayoutManager时，此类型的 item 是否是满跨度
                    return true;
                }

            }).onItemViewType { position, list -> // 根据数据，返回对应的 ItemViewType
            if (list[position].id % 3 == 1) {
                SECTION_TYPE
            } else {
                ITEM_TYPE
            }
        }
    }

    companion object {
        private const val ITEM_TYPE = 0
        private const val SECTION_TYPE = 1
    }
}