package arch.cayenne.module.betslip.data.model


data class ReserveOrderBean(
    val reserveId: String,          // 下注 ID
    val reserveTime: Long,          // 預約時間（毫秒時間戳）
    val betAmount: String,          // 下注金額（字串格式）
    val selection: ReserveOrderSelectionBean, // 預約選項（需要定義對應的資料類型）
    val status: Int                 // 狀態：0-預約中, 1-成功, 2-失敗, 3-取消
) : BetSlipData()