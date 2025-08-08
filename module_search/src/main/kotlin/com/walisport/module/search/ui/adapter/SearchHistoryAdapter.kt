package com.walisport.module.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.search.databinding.ItemSearchHistoryBinding
import com.walisport.module.search.ui.view.FlowAdapter
import com.walisport.module.search.ui.view.FlowViewHolder

/**
 * 提供給 FlowLayout 使用的搜尋紀錄 Adapter，支援刪除模式與點擊搜尋功能。
 *
 * @param closeAction 點擊關閉按鈕時的回調，帶入該項目位置與文字。
 * @param onSearch 點擊項目文字時的回調，預設為空操作。
 */
class SearchHistoryAdapter(
    var closeAction: (position: Int, text: String) -> Unit,
    var onSearch: (key: String) -> Unit = {}
) : FlowAdapter<String, FlowViewHolder, ItemSearchHistoryBinding>() {

    // 是否處於刪除模式，決定是否顯示關閉按鈕
    private var isDelete: Boolean = false

    /**
     * 綁定資料與 view，根據是否為刪除模式決定關閉按鈕顯示與點擊行為。
     *
     * @param holder ViewHolder，內含 ViewBinding。
     * @param binding 該項目的 ViewBinding 實例。
     * @param position 該項目的位置。
     */
    override fun convertPlus(
        holder: FlowViewHolder,
        binding: ItemSearchHistoryBinding,
        position: Int
    ) {
        with(binding) {
            val data = getData()[position]

            // 顯示文字（最多 7 字，多餘加省略號）
            itemTv.text =
                if (data.isEmpty() || data.length <= 7) data
                else data.take(7) + "..."

            // 控制關閉按鈕的顯示與點擊事件
            ivClose.apply {
                visibility = if (isDelete) View.VISIBLE else View.GONE
                clickNoRepeat {
                    closeAction.invoke(position, data)
                }
            }

            // 點擊項目本身的行為：刪除或搜尋
            itemSearchHistory.clickNoRepeat {
                if (isDelete) closeAction.invoke(position, data)
                else onSearch(data)
            }
        }
    }

    /**
     * 建立 ViewBinding 實例，供 ViewHolder 使用。
     *
     * @param inflater LayoutInflater。
     * @param parent 父容器 ViewGroup。
     * @param viewType 項目類型（未使用）。
     */
    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSearchHistoryBinding {
        return ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    /**
     * 建立 ViewHolder，包裝 ViewBinding。
     *
     * @param binding ViewBinding 實例。
     * @param viewType 項目類型（未使用）。
     */
    override fun createViewHolder(
        binding: ItemSearchHistoryBinding,
        viewType: Int
    ): FlowViewHolder {
        return FlowViewHolder(binding)
    }

    /**
     * 設定是否為刪除模式，並刷新畫面。
     *
     * @param isDelete 是否進入刪除模式。
     */
    fun setDeleteMode(isDelete: Boolean) {
        this.isDelete = isDelete
        notifyDataChanged()
    }
}