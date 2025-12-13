package arch.cayenne.module.home.data

import arch.cayenne.lib.database.entity.BaseGameSupplierData

sealed class GameSupplierListItem {
    data class Header(val letter: Char) : GameSupplierListItem()
    data class GameSupplierItem(
        val tournament: BaseGameSupplierData,
        val highlightStart: Int?,
        val highlightEnd: Int?,
        val isSelected: Boolean = false
    ) : GameSupplierListItem()
}
