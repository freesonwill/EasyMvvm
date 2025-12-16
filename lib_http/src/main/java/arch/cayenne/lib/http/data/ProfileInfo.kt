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
