package arch.cayenne.module.home.data.constants

import android.content.Context
import androidx.annotation.StringRes
import arch.cayenne.lib.database.entity.ShowType
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType.TODAY
import arch.cayenne.module.home.data.constants.PlayType.entries

enum class PlayType(val id: Int, @StringRes val titleRes: Int, val refreshInterval: Long) {
//    ALL(0,R.string.title_all, null),
//    IN_PLAY_ODDS(1,R.string.title_in_play_odds, null),
    TODAY(2,R.string.title_today, 60 * 1000),
    EARLY(3, R.string.title_early, 5 * 60 * 1000),
    CHAMPION(4, R.string.title_champion, 60 * 60 * 1000);

    fun getTitle(context: Context): String {
        return context.getString(titleRes)
    }
}

fun Int.playTypeToShowType() : ShowType {
    return when(this) {
        PlayType.TODAY.id -> ShowType.HOME_TODAY
        PlayType.EARLY.id -> ShowType.HOME_EARLY
        PlayType.CHAMPION.id -> ShowType.HOME_CHAMPION
        else -> ShowType.HOME_TODAY
    }
}

fun Int.getPlayTypeById(): PlayType {
    return entries.find { it.id == this } ?: TODAY
}

fun ShowType.toPlayTypeId() : PlayType {
    return when(this) {
        ShowType.HOME_TODAY -> PlayType.TODAY
        ShowType.HOME_EARLY -> PlayType.EARLY
        ShowType.HOME_CHAMPION -> PlayType.CHAMPION
        else -> PlayType.TODAY
    }
}

