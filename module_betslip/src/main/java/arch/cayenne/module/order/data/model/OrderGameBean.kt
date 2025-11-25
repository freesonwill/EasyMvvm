package arch.cayenne.module.order.data.model

data class OrderGameBean(
    val total: Int,
    val records: List<RecordsBean>
)

data class RecordsBean(
    val gameName: String,
    val img: String,
    val bet: String,
    val win: String,
    val supplier: String,
    val time: String
)


