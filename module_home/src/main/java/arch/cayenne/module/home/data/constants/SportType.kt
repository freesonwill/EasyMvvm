package arch.cayenne.module.home.data.constants

import androidx.annotation.StringRes
import arch.cayenne.module.home.R

//串接資料時再調整
enum class SportType(val id: Int, @StringRes val titleResId: Int, val iconResActive: Int, val iconResInactive: Int) {
    Init(0, R.string.title_init, R.drawable.ic_soccer_active, R.drawable.ic_soccer_inactive),
    SOCCER(1, R.string.title_soccer, R.drawable.ic_soccer_active, R.drawable.ic_soccer_inactive),
    BASKETBALL(2, R.string.title_basketball, R.drawable.ic_basketball_active, R.drawable.ic_basketball_inactive),
    BASEBALL(3, R.string.title_baseball, R.drawable.ic_baseball_active, R.drawable.ic_baseball_inactive),
    TENNIS(5, R.string.title_tennis, R.drawable.ic_tennis_active, R.drawable.ic_tennis_inactive),
    HANDBALL(6, R.string.title_handball, R.drawable.ic_handball_active, R.drawable.ic_handball_inactive),
//    ICE_HOCKEY(7, R.string.sport_ice_hockey, R.drawable.ic_ice_hockey_active, R.drawable.ic_ice_hockey_inactive),
//    ESPORTS(8, R.string.sport_esports, R.drawable.ic_esports_active, R.drawable.ic_esports_inactive),
    GOLF(9, R.string.title_golf, R.drawable.ic_golf_active, R.drawable.ic_golf_inactive),
    RUGBY(12, R.string.title_football, R.drawable.ic_football_active, R.drawable.ic_football_inactive),
    SNOOKER(19, R.string.title_snooker,R.drawable.ic_snooker_active, R.drawable.ic_snooker_inactive),
    PING_PONG(20, R.string.title_pingpong,  R.drawable.ic_pingpong_active, R.drawable.ic_pingpong_inactive),
    VOLLEYBALL(23, R.string.title_volleyball, R.drawable.ic_volleyball_active, R.drawable.ic_volleyball_inactive),
//    HOCKEY(24, R.string.title_hockey, R.drawable.ic_volleyball_active, R.drawable.ic_volleyball_inactive),//曲棍球
    BADMINTON(31, R.string.title_badminton, R.drawable.ic_badminton_active, R.drawable.ic_badminton_inactive),

;



    companion object {
        fun fromId(id: Int): SportType? = entries.find { it.id == id }
    }
}