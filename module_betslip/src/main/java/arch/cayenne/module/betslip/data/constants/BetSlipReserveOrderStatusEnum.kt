package arch.cayenne.module.betslip.data.constants
import arch.cayenne.module.betslip.R

//注单结算中stauts
enum class BetSlipReserveOrderStatusEnum(val value: Int, val names:String, val rsId:Int) {
    AppointmentProgress(0,"预约中",R.drawable.bg_lose),
    AppointmentSuccess(1,"预约成功",R.drawable.bg_early_settle),
    AppointmentFail(2,"预约失败",R.drawable.bg_reser_expired),
    Cancel(3,"取消",R.drawable.bg_rejection);

    companion object{

        fun getStatus(value: Int): BetSlipReserveOrderStatusEnum? {
            return BetSlipReserveOrderStatusEnum.entries.find { it.value == value }
        }
    }
}