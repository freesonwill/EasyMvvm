package arch.cayenne.module.home.data.constants

import arch.cayenne.module.home.data.TournamentListItem

data class TournamentListUiState(
    val state: TournamentListState,
    val displayList: List<TournamentListItem>
)