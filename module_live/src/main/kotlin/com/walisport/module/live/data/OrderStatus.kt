package com.walisport.module.live.data

import com.walisport.module.live.R

enum class OrderStatus(val value:Int,val names:String, val resId :Int) {
    UnSettled(0,"未结算",R.drawable.live_bet_selection_status_light),
    Win(1,"赢",R.drawable.bet_settle_result),
    Tie(2,"平",R.drawable.live_bet_selection_status_light),
    Lose(3,"输",R.drawable.live_bet_selection_status_light),
    LoseHalf(5,"输半",R.drawable.live_bet_selection_status_light),
    WinHalf(4,"赢半",R.drawable.live_bet_selection_status_light),
    cancel(6,"取消",R.drawable.live_bet_selection_status_light),
}