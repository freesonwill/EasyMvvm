* 是否能下注
* 判断依据：
    - 游戏状态
*
> var isCanBetting: Boolean = true

* UI将左上角结果视图赋值给该变量
* 返回给app使用
*
> var resultView: View? = null

* 同理 @see resultView
* 返回给app使用
*
> var floatingView: View? = null

* 主播可以设置直播间是否允许下注
* 默认 true
> var isAllowedBet = true

* view调用
*
> var gameMassageManager
> -
> 
> - 临时下注
> - recordBean 当前下注对象
> - block 下注成功回调
> - --isMoneyEnough 当次下注余额是否足够
> - --result 返回当次下注成功的结果，里面有当前的下注的总金额;当此参数为null时，表示上一次下注结果还未返回
> - --isMoneyEnough = false 并且 result = null 说明上一次确认下注还未返回结果
> - fun addBetting(
    recordBean: BettingRecordBean,
    block: (isMoneyEnough: Boolean, result: BettingRecordBean?) -> Unit
    ) 
> 
> - 取消下注
> - block 取消下注后返回已确认的下注
> - fun cancelBetting(block: (result: List<BettingRecordBean>?) -> Unit)
> 
> - 确认下注
> -  fun commitBetting()
> 
> - 续压
> - @return 返回上一局下注数据
> - ---如果返回null 说明上一局未下注
> - fun againBetting(): Map<Betting, BettingRecordBean>?
> 
> - 加倍
> - fun doubleBetting(block: (isMoneyEnough: Boolean, result: Map<Betting, BettingRecordBean>?) -> Unit)

##
> var gameAboutModel
> -
* 需要监听的字段
> - currentStage : 监听阶段变化
> - balance : 监听余额变化,需要缩小100倍，保留两位小数用于展示
> - syncAreaBetInfo ; 监听default牌面的人数变化
> - historyRounds : 开奖历史记录
> - isCanAgain : 显示隐藏续压按钮
* 直接使用的字段
> - countDown 阶段倒计时
> - roundId 期号
> - loginErrorMessage
> - lotteryResultList ->返回的是注区集合：结算阶段使用，开奖注区，用于展示注区的闪闪动画
> - netIncome 净收入，用于展示中奖动画；使用时需要缩小100倍
> - userLotteryResult 用户中奖后的面板砝码金额已经中奖注区