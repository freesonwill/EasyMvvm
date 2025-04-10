package com.walisport.module.home.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.walisport.module.home.R

enum class LeagueType(val leagueId: Int, @DrawableRes val iconRes: Int?, @StringRes val titleRes: Int) {
    ALL(0, null, R.string.league_all),
    PREMIER_LEAGUE(1, R.drawable.ic_premier_league, R.string.league_premier),
    LA_LIGA(2, R.drawable.ic_la_liga, R.string.league_laliga),
    UCL(3, R.drawable.ic_ucl, R.string.league_ucl),
    SERIE_A(4, R.drawable.ic_serie_a, R.string.league_seriea);

    companion object {
        fun fromId(id: Int): LeagueType? = entries.find { it.leagueId == id }
    }
}
