package arch.cayenne.module.order.data.model

data class OrderAllBean(
    val isHeader: Boolean,
    val gameName: String,
    val img: String,
    val bet: String,
    val win: String,
    val supplier: String,
    val time: String
)