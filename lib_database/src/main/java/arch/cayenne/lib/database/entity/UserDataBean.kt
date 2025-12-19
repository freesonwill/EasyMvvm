package arch.cayenne.lib.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataBean(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,

    @Embedded(prefix = "avatar_")
    val avatar: AvatarEmbedded,

    val registerTime: Long,
    val vipLevel: Int,
    val score: Long,

    @ColumnInfo(name = "wallet_list")
    val list: List<WalletBean>,

    val admittedBetScore: Long,
    val requiredAdmittedBetScore: Long,
    val vipStage: String,
    val nicknameChangeCount: Int
)

data class AvatarEmbedded(
    val url: String,
    val thumbhash: String
)

data class WalletBean(
    val currency: String,
    val balance: Double,
    val convertedAmount: Long
)