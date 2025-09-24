package arch.cayenne.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: wenxi
 * @date: 24/9/25 10:54
 * @description:
 */
@Entity
data class ChatConfigBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isFirstOpen: Boolean = true
)