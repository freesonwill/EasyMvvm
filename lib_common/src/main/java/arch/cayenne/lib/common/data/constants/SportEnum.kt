package arch.cayenne.lib.common.data.constants

import androidx.annotation.DrawableRes
import arch.cayenne.lib.common.R

enum class SportEnum(val id:Int,@DrawableRes val resId:Int) {
    Soccer(1, R.drawable.soccer_color),
    BasketBall(2,R.drawable.basketball_color),
    BaseBall(3,R.drawable.baseball_color),
    Tennis(5,R.drawable.tinnis_color),
    HandBall(6,R.drawable.handball_color),
    Golf(9,R.drawable.golf_color),
    FootBall(12,R.drawable.rugby_color),
    Snooker(19,R.drawable.snooker_color),
    PingPang(20,R.drawable.pingpang_color),
    VolleyBall(23,R.drawable.volleyball_color),
    Hockey(24,-1),//曲棍球暂无
    Badminton(31,R.drawable.badminton_color),
    BeachVolleyBall(-1,R.drawable.beach_volleyball_color),//暂无
    Pool(-1,R.drawable.pool_color),
    Default(-1,R.drawable.basketball_color);//暂无

    companion object{
        fun getSportEnumById(sportId:Int):SportEnum?{
            return entries.find { sportId == it.id}
        }

    }
}