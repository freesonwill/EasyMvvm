package arch.cayenne.module.home.data

data class MatchUpdatePayload(
    val matchId: String,
    val clock: String? = null,
    val viewerCount: Int? = null,
    val score: String? = null,
    val selectionUpdates: List<SelectionUpdate>? = null
)

data class SelectionUpdate(
    val selectionId: String,
    val odds: String? = null,
    val active: Boolean? = null
)
