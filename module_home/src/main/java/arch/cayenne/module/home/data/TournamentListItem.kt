package arch.cayenne.module.home.data

import arch.cayenne.lib.database.entity.BaseTournamentData

sealed class TournamentListItem {
    data class Header(val letter: Char) : TournamentListItem()
    data class TournamentItem(val tournament: BaseTournamentData, val highlightStart: Int?, val highlightEnd: Int?) : TournamentListItem()
    data object FooterView : TournamentListItem()
}
