package com.walisport.module.live.data.constants

import com.walisport.module.live.R

enum class BidEmojiEnum(val key: String, val resId: Int) {
    Goal("/bid=1/", R.drawable.bid1),
    ScordTwice("/bid=2/", R.drawable.bid2),
    HatTrick("/bid=3/", R.drawable.bid3),
    BigFour("/bid=4/", R.drawable.bid4),
    FivePassed("/bid=5/", R.drawable.bid5),
    GoalCelebration("/bid=6/", R.drawable.bid6),
    ChampionDream("/bid=7/", R.drawable.bid7),
    TargeLock("/bid=8/", R.drawable.bid8),
    QuickFighting("/bid=9/", R.drawable.bid9),
    FinalBattle("/bid=10/", R.drawable.bid10),
    GoalCombo("/bid=11/", R.drawable.bid11),
    OnThePitch("/bid=12/", R.drawable.bid12),
    ShotNet("/bid=13/", R.drawable.bid13),
    Courageous("/bid=14/", R.drawable.bid14,),
    Passionate("/bid=15/", R.drawable.bid15,),
    Shocking("/bid=16/", R.drawable.bid16),
    BloodBoiling("/bid=17/", R.drawable.bid17),
    FinalMoment("/bid=18/", R.drawable.bid18),
    SuperPlayer("/bid=19/", R.drawable.bid19),
    Siuuu("/bid=20/", R.drawable.bid20);

    companion object {
        private val _map: MutableMap<String, Int> = mutableMapOf()
        fun getEmojiMap(): Map<String, Int> {
            BidEmojiEnum.entries.forEach {
                _map[it.key] = it.resId
            }
            return _map
        }
    }
}