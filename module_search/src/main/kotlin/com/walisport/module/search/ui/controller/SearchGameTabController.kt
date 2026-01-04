package com.walisport.module.search.ui.controller

import android.view.View
import android.widget.TextView
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.view.CustomGameTabClickListener
import arch.cayenne.lib.common.ui.view.CustomGameTabGroupLayout
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.walisport.module.business.common.data.constants.GameSortType
import com.walisport.module.search.R
import arch.cayenne.lib.common.R as RC

/**
 * Search 模組專用：封裝與 CustomGameTabGroupLayout 相關的通用行為。
 *
 * 目標：
 * - UI / 互動邏輯與大廳的 GameContentFragment 保持一致（排序文案、返獎提示、間距調整）
 * - 在 Search 模組內獨立實作，不直接依賴 hall 模組
 * - 透過 callback 方式，讓 Fragment / ViewModel 負責真正的資料請求與列表更新
 */
class SearchGameTabController(
    private val tabGroup: CustomGameTabGroupLayout,
    private val rewardTipsView: TextView?,
    private val gridItemDecoration: GridSpacingItemDecoration?,
    private val initialSortType: GameSortType = GameSortType.HOT,
    /** Tab 被點擊時回傳供應商 id（0 代表全部 -> 回傳 null） */
    private val onSupplierChanged: (supplierId: Int?) -> Unit,
    /** 排序類型變更時 callback，由外部決定如何重新產生 / 請求資料 */
    private val onSortTypeChanged: (sortType: GameSortType) -> Unit,
    /** 「供應商」更多按鈕點擊（與 GameContentFragment 行為對齊，預留供 BottomSheet 使用） */
    private val onShowAllSupplierClick: (() -> Unit)? = null,
    /** 其它分類按鈕點擊（目前未使用，保留擴充） */
    private val onOtherCategoryClick: (() -> Unit)? = null,
) {

    /** 當前排序類型，預設與 GameContentFragment 一樣為 HOT */
    var currentSortType: GameSortType = initialSortType
        private set

    /** 綁定 Tab 資料列表，0 號為「全部」，其餘為具體供應商 */
    fun submitTabs(tabs: List<SimpleTabDataModel>, selectAll: Boolean = true) {
        tabGroup.submitTabList(tabs)
        if (selectAll && tabs.isNotEmpty()) {
            tabGroup.select(0)
        }
    }

    /** 初始化所有點擊事件與初始排序 UI */
    fun attach() {
        setupTabClick()
        setupSortButton()
        setupShowAllCategory()
        // 設定初始排序文字與 UI
        applySortType(currentSortType, notify = false)
    }

    /** 釋放所有 listener，避免 Fragment onDestroyView 之後還持有引用 */
    fun detach() {
        // 設置空的 no-op listener 來清除引用，避免內存洩漏
        tabGroup.setTabClickListener(object : CustomGameTabClickListener {
            override fun onTabClicked(id: Int) {
                // No-op: 不做任何操作
            }
        })
        tabGroup.setOnSortBtnClick {
            // No-op: 不做任何操作
        }
        tabGroup.setOnShowAllCategoryClick(
            listener = {
                // No-op: 不做任何操作
            },
            listener2 = {
                // No-op: 不做任何操作
            }
        )
    }

    private fun setupTabClick() {
        tabGroup.setTabClickListener(object : CustomGameTabClickListener {
            override fun onTabClicked(id: Int) {
                // id = 0 代表「全部」
                val supplierId = if (id == 0) null else id
                onSupplierChanged.invoke(supplierId)
            }
        })
    }

    private fun setupSortButton() {
        tabGroup.setOnSortBtnClick {
            val next = when (currentSortType) {
                GameSortType.HOT -> GameSortType.NEW
                GameSortType.NEW -> GameSortType.HOT_REWARD
                GameSortType.HOT_REWARD -> GameSortType.COLD_REWARD
                GameSortType.COLD_REWARD -> GameSortType.HOT
            }
            applySortType(next, notify = true)
        }
    }

    private fun setupShowAllCategory() {
        if (onShowAllSupplierClick != null || onOtherCategoryClick != null) {
            tabGroup.setOnShowAllCategoryClick(
                listener = { onShowAllSupplierClick?.invoke() },
                listener2 = { onOtherCategoryClick?.invoke() }
            )
        }
    }

    /**
     * 套用指定排序型別，並同步更新 UI / callback。
     * @param notify 是否要觸發 onSortTypeChanged（初始化時可傳 false）
     */
    fun applySortType(sortType: GameSortType, notify: Boolean = true) {
        currentSortType = sortType

        // 更新排序文字（使用 ResourceExt.getString 的擴展函數）
        val context = tabGroup.context
        tabGroup.setSortBtnText(
            when (sortType) {
                GameSortType.HOT -> SkinnableResourceManager.getString(context, RC.string.custom_tab_hot)
                GameSortType.NEW -> SkinnableResourceManager.getString(context, RC.string.custom_tab_new)
                GameSortType.HOT_REWARD -> SkinnableResourceManager.getString(context, RC.string.custom_tab_hot_reward)
                GameSortType.COLD_REWARD -> SkinnableResourceManager.getString(context, RC.string.custom_tab_cold_reward)
            }
        )

        // 返獎率提示顯示邏輯（火熱 / 冰冷 顯示）
        rewardTipsView?.visibility = when (sortType) {
            GameSortType.HOT_REWARD, GameSortType.COLD_REWARD -> View.VISIBLE
            else -> View.GONE
        }

        // 更新 Grid 間距，與 GameContentFragment 一致
        gridItemDecoration?.verticalSpacing = when (sortType) {
            GameSortType.HOT_REWARD, GameSortType.COLD_REWARD -> 14.dp2px
            else -> 12.dp2px
        }

        if (notify) {
            onSortTypeChanged.invoke(sortType)
        }
    }
}


