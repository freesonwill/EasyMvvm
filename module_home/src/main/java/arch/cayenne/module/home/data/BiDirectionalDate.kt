package arch.cayenne.module.home.data

/**
 *
 * @date: 2025/12/4 23:07
 * @description:
 */
data class BiDirectionalDate(
    val dateStr: String,
    val weekdayStr: String,
    val timestamp: Long,
    val type: BiDirectionalDateType
)

enum class BiDirectionalDateType {
    Date, Other
}