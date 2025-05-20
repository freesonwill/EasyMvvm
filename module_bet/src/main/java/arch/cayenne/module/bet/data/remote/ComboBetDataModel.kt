package arch.cayenne.module.bet.data.remote

data class ComboBetDataModel(
    val isSuccessful: Boolean,
    val message: String,
    val data: List<ComboMultiBetInfo>
)

data class ComboMultiBetInfo(
    val orderId: String,
    val comboValue: Int,
    val orderStatus: Int,
)