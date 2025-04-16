package arch.cayenne.module.bet.data.remote

data class SingleBetDataModel(
    val isSuccessful: Boolean,
    val message: String,
    val orderId: String,
    val orderStatus: Int,
    val orderStatusMsg: String
)