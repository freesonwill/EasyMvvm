package arch.cayenne.module.betslip.data.constants

import androidx.annotation.StringRes
import arch.cayenne.module.bet.R

enum class LiveBetSlipResultOrderStatusEnum(val value: Int,@StringRes val names: Int, val resId: Int) {
    UnSettled(0, R.string.unsettled, R.drawable.bg_half_lose),
    Win(1, R.string.win, R.drawable.bg_win),
    Tie(2, R.string.tie, R.drawable.bg_lose),
    Lose(3, R.string.lose, R.drawable.bg_lose),
    LoseHalf(5, R.string.lose_half, R.drawable.bg_half_lose),
    WinHalf(4, R.string.win_half, R.drawable.bg_half_lose),
    Cancel(6, R.string.cancel, R.drawable.bg_rejection),
    EarlySettle(7, R.string.live_bet_early_settle, R.drawable.bg_early_settle);

   companion object{
       fun getStatus(value: Int): LiveBetSlipResultOrderStatusEnum? {
           return entries.find { it.value == value }
       }
   }
}