package arch.cayenne.lib.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserDataBean(
    @PrimaryKey val id: Int = 0,
    val Uid: Long,
    var nickname: String,
    @Embedded(prefix = "avatar_")
    val avatar: AvatarEmbedded,
    val registerTime: Long,
    val vipLevel: Int,
    val score: Long,//用户积分
    val ccy: String,//用户默认货币
    @ColumnInfo(name = "wallet_list")
    val list: List<WalletBean>,
    val admittedBetScore: Long,//当前投注分数
    val requiredAdmittedBetScore: Long,//准入投注分数
    val vipStage: Int,//vip阶段
    val nicknameChangeCount: Int //呢称修改次数
)

data class AvatarEmbedded(
    val url: String,
    val thumbhash: String,
    val type: Int
)

data class WalletBean(
    val currency: String,
    val balance: Long,
    val convertedAmount: Long
)