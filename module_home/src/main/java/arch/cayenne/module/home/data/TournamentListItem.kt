package arch.cayenne.module.home.data

import arch.cayenne.lib.database.entity.TournamentDataModel

sealed class TournamentListItem {
    data class Header(val letter: Char) : TournamentListItem()
    data class TournamentItem(val tournament: TournamentDataModel) : TournamentListItem()
}
