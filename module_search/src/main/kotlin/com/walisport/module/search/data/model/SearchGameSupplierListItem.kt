package com.walisport.module.search.data.model

import arch.cayenne.lib.database.entity.BaseGameSupplierData

/**
 * Search 模組專用的供應商列表項數據類，與 hall 模組的 GameSupplierListItem 對應。
 */
sealed class SearchGameSupplierListItem {
    data class Header(val letter: Char) : SearchGameSupplierListItem()
    data class GameSupplierItem(
        val tournament: BaseGameSupplierData,
        val highlightStart: Int?,
        val highlightEnd: Int?,
        val isSelected: Boolean = false
    ) : SearchGameSupplierListItem()
}

