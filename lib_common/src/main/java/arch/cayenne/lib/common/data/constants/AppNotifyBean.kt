package arch.cayenne.lib.common.data.constants

data class AppNotifyBean(
    val type: Int,
    val sportId: Int,
    val matchId: Long,
    val title: String,
    val content: String
)
