package arch.cayenne.module.chat.data.constants

import arch.cayenne.module.chat.R


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
    FinalBattle("/bid=10/", R.drawable.bid10);


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