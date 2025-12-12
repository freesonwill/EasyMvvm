package arch.cayenne.lib.database.entity

import androidx.room.Entity

abstract class BaseGameSupplierData {
    abstract val index: Int
    abstract val id: Int
    abstract val name: String
    abstract val gameTypeId: Int //游戏分类id
    abstract val icon: String
    abstract val hot: Boolean
    abstract var isSelected: Int
}

@Entity(primaryKeys = ["index",])
data class GameSupplierDataModel(
    override val index: Int,
    override val id: Int,
    override val name: String,
    override val gameTypeId: Int,
    override val icon: String,
    override val hot: Boolean,
    override var isSelected: Int,
) : BaseGameSupplierData() {
    companion object {
        fun createAllItem(): GameSupplierDataModel {
            return GameSupplierDataModel(
                index = 0,
                id = 0,
                name = "ALL",
                gameTypeId = 0,
                icon = "",
                hot = false,
                isSelected = 0
            )
        }
    }
}

