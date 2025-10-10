package arch.cayenne.lib.common.data.constants

import androidx.annotation.DrawableRes
import arch.cayenne.lib.common.R

enum class SportEnum(val id:Int,@DrawableRes val resId:Int) {
    Soccer(1, R.drawable.ic_soccer_active),
    BasketBall(2,R.drawable.ic_basketball_active),
    BaseBall(3,R.drawable.ic_baseball_active),
    Tennis(5,R.drawable.ic_tennis_active),
    HandBall(6,R.drawable.ic_handball_active),
    Golf(9,R.drawable.ic_golf_active),
    Rugby(12,R.drawable.ic_rugby_active),
    Snooker(19,R.drawable.ic_snooker_active),
    PingPang(20,R.drawable.ic_pingpong_active),
    VolleyBall(23,R.drawable.ic_volleyball_active),
    Hockey(24,-1),//曲棍球暂无
    Badminton(31,R.drawable.ic_badminton_active),
    BeachVolleyBall(-1,R.drawable.ic_beach_volleyball_active),//暂无
    Pool(-1,R.drawable.ic_pool_active),
    Default(-1,R.drawable.ic_baseball_active);//暂无

    companion object{
        fun getSportEnumById(sportId:Int):SportEnum?{
            return entries.find { sportId == it.id}
        }

    }
}