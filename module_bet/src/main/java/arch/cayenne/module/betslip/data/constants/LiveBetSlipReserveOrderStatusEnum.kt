package arch.cayenne.module.betslip.data.constants
import arch.cayenne.module.bet.R

//注单结算中stauts
enum class LiveBetSlipReserveOrderStatusEnum(val value: Int, val names:String, val rsId:Int) {
    AppointmentProgress(0,"预约中",R.drawable.live_bet_selection_status_normal),
    AppointmentSuccess(1,"预约成功",R.drawable.live_bet_selection_status_normal),
    AppointmentFail(2,"预约失败",R.drawable.live_bet_selection_status_normal),
    Cancel(3,"取消",R.drawable.live_bet_selection_status_normal);

    companion object{

        fun getStatus(value: Int): LiveBetSlipReserveOrderStatusEnum? {
            return LiveBetSlipReserveOrderStatusEnum.entries.find { it.value == value }
        }
    }
}