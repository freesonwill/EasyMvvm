package arch.cayenne.module.home.data

//對應API所需的play_type參數
sealed class PlayType(val id: Int) {
    //全部
    data object All: PlayType(0)
    //滾地
    data object InPlayOdds: PlayType(1)
    //今日
    data object Today: PlayType(2)
    //早盤
    data object EarlyLines: PlayType(3)
    //冠軍
    data object Champion: PlayType(4)
}