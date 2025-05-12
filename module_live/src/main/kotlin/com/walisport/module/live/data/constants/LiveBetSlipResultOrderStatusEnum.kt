package com.walisport.module.live.data.constants

import androidx.annotation.StringRes
import com.walisport.module.live.R

enum class LiveBetSlipResultOrderStatusEnum(val value: Int,@StringRes val names: Int, val resId: Int) {
    UnSettled(0, R.string.unsettled, R.drawable.live_bet_selection_status_light),
    Win(1, R.string.win, R.drawable.bet_settle_result),
    Tie(2, R.string.tie, R.drawable.live_bet_selection_status_light),
    Lose(3, R.string.lose, R.drawable.live_bet_selection_status_light),
    LoseHalf(5, R.string.lose_half, R.drawable.live_bet_selection_status_light),
    WinHalf(4, R.string.win_half, R.drawable.live_bet_selection_status_light),
    Cancel(6, R.string.cancel, R.drawable.live_bet_selection_status_light),
    EarlySettle(7, R.string.live_bet_early_settle, R.drawable.live_bet_selection_status_light);

   companion object{
       fun getStatus(value: Int): LiveBetSlipResultOrderStatusEnum? {
           return entries.find { it.value == value }
       }
   }
}