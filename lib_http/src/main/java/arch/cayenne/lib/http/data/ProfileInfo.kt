package arch.cayenne.lib.http.data

data class ProfileInfo(
    val id: Long,
    val name: String,
    val avatar: Avatar,
    val registerTime: Long,
    val vipLevel: Int,
    val balanceTotal: Long,
    val balanceWallet: Map<String, Long>,
    val currentBetAmount: Double,
    val requiredBetAmount: Double,
    val vipStage: String,
    val nicknameChangeCount: Int
)

data class Avatar(
    val url: String,
    val thumbhash: String
)

data class AccountInfo(
    val nickname: String,
    val avatar: Avatar,
    val registerTime: Long,
    val vipLevel: Int,
    val score: Long,
    val list: List<Wallet>,
    val admittedBetScore: Long,
    val requiredAdmittedBetScore: Long,
    val vipStage: String,
    val nicknameChangeCount: Int
)

data class Wallet(
    val ccy: String,
    val score: Double,
    val exchangeScore: Long
)