package arch.cayenne.lib.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataBean(
    @PrimaryKey val id: Long,
    val name: String,

    // 使用 @Embedded 展開 Avatar
    @Embedded(prefix = "avatar_")
    val avatar: AvatarEmbedded,

    val registerTime: Long,
    val vipLevel: Int,
    val balanceTotal: Long,

    val balanceWallet: Map<String, Long>,

    val currentBetAmount: Double,
    val requiredBetAmount: Double,
    val vipStage: String,
    val nicknameChangeCount: Int,
)

data class AvatarEmbedded(
    val url: String,
    val thumbhash: String
)