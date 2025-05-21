package arch.cayenne.module.home.data

import arch.cayenne.lib.database.entity.BaseTournamentData

sealed class TournamentListItem {
    data class Header(val letter: Char) : TournamentListItem()
    data class TournamentItem(val tournament: BaseTournamentData) : TournamentListItem()
}
