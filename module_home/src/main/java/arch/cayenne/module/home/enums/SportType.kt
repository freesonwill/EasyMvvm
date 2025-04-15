package arch.cayenne.module.home.enums

import androidx.annotation.StringRes
import arch.cayenne.module.home.R

//串接資料時再調整
enum class SportType(val id: Int, @StringRes val titleResId: Int, val iconResActive: Int, val iconResInactive: Int) {
    Soccer(1, R.string.title_soccer, R.drawable.ic_soccer_active, R.drawable.ic_soccer_inactive),
    VOLLEYBALL(2, R.string.title_volleyball, R.drawable.ic_volleyball_active, R.drawable.ic_volleyball_inactive),
    BASKETBALL(3, R.string.title_basketball, R.drawable.ic_basketball_active, R.drawable.ic_basketball_inactive),
    FOOTBALL(4, R.string.title_football, R.drawable.ic_football_active, R.drawable.ic_football_inactive),
    BASEBALL(5, R.string.title_baseball, R.drawable.ic_baseball_active, R.drawable.ic_baseball_inactive),
//    // 🔽 其他運動類型（共 24 種）
//    RUGBY(6, R.string.sport_rugby, R.drawable.ic_rugby_active, R.drawable.ic_rugby_inactive),
//    ICE_HOCKEY(7, R.string.sport_ice_hockey, R.drawable.ic_ice_hockey_active, R.drawable.ic_ice_hockey_inactive),
//    ESPORTS(8, R.string.sport_esports, R.drawable.ic_esports_active, R.drawable.ic_esports_inactive);
;
    companion object {
        fun fromId(id: Int): SportType? = entries.find { it.id == id }
    }
}