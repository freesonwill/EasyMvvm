package arch.cayenne.module.home

import arch.cayenne.lib.database.entity.TournamentDataModel

/**
 * 联赛组合
 * @date: 2025/11/27 16:47
 * @description:
 */
data class TournamentCombo(val tournamentList: List<TournamentDataModel>) {
    var isSelected: Boolean = false

    val sportId: Int
        get() {
            return tournamentList[0].sportId
        }

    val leagueIdList: List<Int>
        get() {
            return tournamentList.map { it.id }
        }


    fun containsTournament(tournamentId: Int): Boolean {
        return tournamentList.any { it.id == tournamentId }
    }

    fun getTournamentName(tournamentId: Int): String {
        val model = tournamentList.firstOrNull { it.id == tournamentId }
        return model?.simpleName ?: ""
    }

}
