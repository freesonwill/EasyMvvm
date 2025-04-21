package com.walisport.module.live.data.model

data class LiveOrderData(val betID:String)

//string bet_id = 1; //订单ID
//string price = 2;  // 提前结算1元的报价，如返回0.92，就是提前结算1元可获得0.92元，无价格或者为0表示不能进行提前结算
//int32 settle_total = 3; //单笔订单最大有效提前结算次数
//string settle_min = 4; //订单提前结算单次最小结算本金
//int32 settle_status = 5; //投注确认中，2拒单，3取消订单，4接单成功，5已结算,101 预约提前结算中,102 提前结算进行中


data class EarlySettlePrice (val betId:String,val price:String,val settleTotal:Int,val settleMin:String,val settleStatue:Int)

