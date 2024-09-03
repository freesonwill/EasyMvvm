package com.cn.game.sdk2.websocket

import android.content.Context
import android.util.Log
import com.cn.game.sdk2.BuildConfig
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_BOOM_1
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_BOOM_ALL
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_DEFAULT_BIG
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_DEFAULT_DOUBLE
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_DEFAULT_SINGLE
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_DEFAULT_SMALL
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_DOUBLE_1
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_SINGLE_1
import com.cn.game.sdk2.utils.BettingAreaUtil.BET_AREA_SUM_4
import com.cn.game.sdk2.utils.BettingAreaUtil.getAllBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getBoomBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getDefaultBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getDoubleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getSingleBets
import com.cn.game.sdk2.utils.BettingAreaUtil.getSumBets
import com.cn.game.sdk2.websocket.bean.AreaBetConfigBean
import com.cn.game.sdk2.websocket.bean.Betting
import com.cn.game.sdk2.websocket.bean.BettingRecordBean
import com.cn.game.sdk2.websocket.bean.ObservableArrayList
import com.cn.game.sdk2.websocket.bean.SINGLE
import com.cn.game.sdk2.websocket.bean.VerifyDoubleResultBean
import com.cn.game.sdk2.websocket.constants.BettingState
import com.cn.game.sdk2.websocket.constants.BettingStatus
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.imp.UIMethodImpl
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import kotlin.random.Random
//GameKtx没有按照责任,耦合度太高
val tokenArray = listOf(
    "15:ZKbkjdBW"
)

////测试打包专用 99:mFGB4ljy
////92:ZyBmhNCJ   87:MHxIHlYM  93:Ufx3Dy8y 94:0aPEwiYK   金额少：97:nMz8aSsZ  98:gCrUd5Gz
//val tokenArray = listOf(
//    "101:PcI4jEcP",
//    "99:mFGB4ljy",
//    "42:aRYvqlC5",
//    "33:ZtG5WhUh",
//    "29:zNbNe45L",
//    "37:QyJbGSGR",
//    "24:zQQBFVFI",
//    "50:OtdAVXdd",
////    "74:4wNIFMMi",//失效的token
//    "51:Ja9L1rG6",
//    "35:BIyxvrqa",
////    "69:cAjjzn2s",//失效的token
//)
//
//val tokenIndex = Random.nextInt(tokenArray.size)
//
@Suppress("KotlinConstantConditions")
val token: String
    get() {
        return when (BuildConfig.BUILD_TYPE) {
            "debug" -> {
                tokenArray[Random.nextInt(tokenArray.size)]
            }

            "innerTest" -> {
                "99:mFGB4ljy"
            }

            "outerTest" -> {
                tokenArray[Random.nextInt(tokenArray.size)]
            }

            "release" -> {
                tokenArray[Random.nextInt(tokenArray.size)]
            }

            else -> throw IllegalStateException("wrong buildType:${BuildConfig.BUILD_TYPE}")
        }
    }


//---------------------------socket方面使用,流程控制，不是数据---------------------------------//
internal var nativeLib = NativeLib()

//internal var socketStatesCallback: GameApp.SocketStatesCallback? = null

//登录过的标记 用于重连
internal var isLogin = false

//多用户登录token失效
internal var isTokenValid = true

//token失效后，socket连接关闭，停止重连
internal var isNeedReconnect = true

//var balance: Long = 2000000
internal var isEnterRoom = false


//---------------------------app方面使用---------------------------------//
internal var appContext: Context? = null

internal var appLifecycleEnable: Boolean = false

//用户设置 打开声音
internal var isEnableSound = true

//用户设置 回调
internal var appListener: GameApp.OnSdkListener? = null

//---------------------------ui方面使用---------------------------------//
internal var gameAboutModel =  GameAboutModel() //
internal var gameMassageManager: UIMethodImpl? = null


internal fun <K, V> Map<K, V>.isNotEmpty(block: (MutableMap<K, V>) -> Unit): Boolean {
    if (this.isNotEmpty()) {
        block(this.toMutableMap())
        return true
    }
    return false
}

fun Boolean.isEmpty(block: () -> Unit) {
    if (!this) {
        block()
    }
}

//计算开奖注区
fun List<Int>.calculateArea(): ArrayList<Betting> {
    if (size != 3) return ArrayList()
    val dice1 = this[0]
    val sum = sum()

    return ArrayList<Betting>().apply {
        if (isEquals()) {
            add(getDefaultBets().first { it.number == BET_AREA_BOOM_ALL })
            add(getBoomBets().first { it.number == (BET_AREA_BOOM_1 - 1 + dice1) })
        } else {
            //------默认------
            getDefaultBets().let { defaultBets ->
                listOf(
                    //大小
                    if (sum >= 11) BET_AREA_DEFAULT_BIG else BET_AREA_DEFAULT_SMALL,
                    //单双
                    if (sum % 2 == 0) BET_AREA_DEFAULT_DOUBLE else BET_AREA_DEFAULT_SINGLE
                ).map { betArea ->
                    defaultBets.first { it.number == betArea }
                }.also {
                    addAll(it)
                }
            }
        }

        //------总和------
        getSumBets()
            .firstOrNull { it.number == (BET_AREA_SUM_4 - 4 + sum) }
            // 排除三个一样的 3 6 9 12 15 18
            ?.takeIf { sum % 3 != 0 || !isEquals() }
            ?.let { add(it) }

        //------对子------
        isPairs { double, num ->
            getDoubleBets()
                .firstOrNull { it.number == (BET_AREA_DOUBLE_1 - 1 + num) }
                ?.takeIf { double }
                ?.let { add(it) }
        }

        //----单个----
        addAll(
            countSingle().mapNotNull { (key, value) ->
                getSingleBets()
                    .firstOrNull { it.number == (BET_AREA_SINGLE_1 - 1 + key) }
                    ?.apply { count = value }
            }
        )
    }
}

fun List<Int>.isEquals(): Boolean {
    return size == 3 && all { it == this[0] }
}

//计算对子 返回是否是对子 和对子点数
fun List<Int>.isPairs(block: (double: Boolean, num: Int) -> Unit) {
    if (this.size != 3) {
        block(false, -1)
        return
    }

    val pairs = groupBy { it }.filter { it.value.size == 2 }
    if(pairs.isNotEmpty()) {
        block(true, pairs.keys.first())
    } else {
        block(false, -1)
    }
}

//计算总和
fun List<Int>.sum(): Int {
    return if(size == 3) sumOf { it } else 0
}

//大小
fun List<Int>.isBig(): Boolean {
    return sum() >= 11
}

//单双
fun List<Int>.isDouble(): Boolean {
    return sum() % 2 == 0
}

//计算单个的个数
fun List<Int>.countSingle(): HashMap<Int, Int> {
    val countMap = HashMap<Int, Int>()
    forEach {
        if (countMap.containsKey(it)) {
            countMap[it] = countMap[it]!! + 1
        } else {
            countMap[it] = 1
        }
    }
    return countMap
}

//计算用户中奖的注区
fun List<Betting>.calculateUserLotteryResult(userBettingList: MutableList<BettingRecordBean>): ArrayList<BettingRecordBean> {
    val userBettingMap: Map<Betting, BettingRecordBean> = userBettingList.convertMap()
    val userLotteryResult = ArrayList<BettingRecordBean>()
    Log.e(Fast3MainFragment.TAG, userBettingMap.toString())
    forEach {
        if (userBettingMap.containsKey(it)) {
            val betting = userBettingMap[it]!!.copy()
            betting.money = if (it is SINGLE) {
                if (it.count >= 0) (betting.money * it.multipliers[it.count - 1]).toInt()
                else 0
            } else {
                (betting.money * it.multiplier).toInt()
            }
            userLotteryResult.add(betting)
        }
    }
    return userLotteryResult
}

//注区号转换注区对象
fun Int.convertBetting(): Betting? {
    return getAllBets().firstOrNull { it.number == this }
}

@JvmName("copyFromAreaBetConfig")
infix fun <K> MutableMap<K, List<AreaBetConfigBean>>.copyFrom(other: MutableMap<K, List<AreaBetConfigBean>>) {
    other.forEach {
        val copy = it.value
        val copyList = ArrayList<AreaBetConfigBean>()
        copy.forEach { bean ->
            copyList.add(bean.copy())
        }
        this[it.key] = copyList
    }
}

//获取指定注区的限额配置
fun List<AreaBetConfigBean>.getBeanById(betting: Betting): AreaBetConfigBean? {
    forEach {
        if (it.areaCode.number == betting.number) {
            return it
        }
    }
    return null
}

//List 转 Map
fun MutableList<BettingRecordBean>.convertMap(): MutableMap<Betting, BettingRecordBean> {
    val againList: MutableMap<Betting, BettingRecordBean> = mutableMapOf()
    filter { it.state == BettingStatus.COMMITTED }.groupBy(BettingRecordBean::bettingArea).map {
        val sumOf = it.value.sumOf { it.money }
        val copy = it.value[0].copy()
        copy.money = sumOf
        againList[it.key] = copy
    }
    return againList
}

//验证下注的有效性
internal fun List<BettingRecordBean>.verifyAdd(
    record: BettingRecordBean, areaBetConfigBean: AreaBetConfigBean?
): BettingState {
    val currentBettingTotalMoney =
        filter { it.bettingArea.number == record.bettingArea.number }.sumOf { it.money }
    if ((gameAboutModel.balance.value ?: 0) <= 5000) return BettingState.NO_MONEY_50
    val totalMoney = filter { it.state == BettingStatus.TEMP }.sumOf { it.money }
    val moneyEnough = totalMoney <= gameAboutModel.balance.value!!
    if (!moneyEnough) return BettingState.NO_MONEY
    if (areaBetConfigBean != null) {
        if (currentBettingTotalMoney > areaBetConfigBean.maxLimit) return BettingState.OFFSET_MAX
    }
    return BettingState.GO_ON
}

//生成指定注区的牌面展示对象
fun List<BettingRecordBean>.generateUiBean(betting: Betting): BettingRecordBean? {
    val currentBettingTotalMoney =
        filter { it.bettingArea.number == betting.number }.sumOf { it.money }
    val one = find { it.bettingArea.number == betting.number }?.copy()
    one?.money = currentBettingTotalMoney
    return one
}

//取消下注
fun ObservableArrayList<BettingRecordBean>.cancel(): List<BettingRecordBean?> {
    val tempTotalMoney = filter { it.state == BettingStatus.TEMP }.sumOf { it.money }
    gameAboutModel.returnTempBalance(tempTotalMoney)
    removeBy(BettingStatus.TEMP)
    modify()
    return groupBy { it.bettingArea }.map { it.value.generateUiBean(it.key) }
}

fun MutableCollection<BettingRecordBean>.removeBy(predicate: BettingStatus) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        if (predicate == iterator.next().state) {
            iterator.remove()
        }
    }
}

//验证提交的有效性
fun MutableList<BettingRecordBean>.verifyCommit(configs: List<AreaBetConfigBean>?): AreaBetConfigBean? {
    configs?.let {
        groupBy { it.bettingArea }.map {
            val money = it.value.sumOf { bean ->
                bean.money
            }
            val minLimit = configs.getBeanById(it.key)!!.minLimit
            if (money < minLimit) {
                return configs.getBeanById(it.key)
            }
        }
    }
    return null
}

//获取指定状态的总金额
fun MutableList<BettingRecordBean>.getMoneyByState(status: BettingStatus): Int {
    return filter { it.state == status }.sumOf { it.money }
}

//下注成功后 改变COMMITTING -》 COMMITTED
fun MutableList<BettingRecordBean>.setCommittedState() {
    filter { it.state == BettingStatus.COMMITTING }.forEach { it.state = BettingStatus.COMMITTED }
}

//下注失败后返回扣掉的钱 以及 更新注区状态
fun MutableList<BettingRecordBean>.returnTemp() {
    gameAboutModel.returnTempBalance(getMoneyByState(BettingStatus.COMMITTING))
    filter { it.state == BettingStatus.COMMITTING }.forEach { it.state = BettingStatus.TEMP }
}

fun Map<Betting, List<BettingRecordBean>>.merge(): MutableList<BettingRecordBean> {
    val map = map {
        val sumOf = it.value.sumOf { it.money }
        val copy = it.value[0].copy()
        copy.money = sumOf
        copy
    }
    return map.toMutableList()
}

fun MutableList<BettingRecordBean>.convertAgainList(): MutableMap<Betting, BettingRecordBean> {
    val againList: MutableMap<Betting, BettingRecordBean> = toMapByAreaCode()
    clear()
    return againList
}

fun MutableList<BettingRecordBean>.toMapByAreaCode(): MutableMap<Betting, BettingRecordBean> {
    val againList: MutableMap<Betting, BettingRecordBean> = mutableMapOf()
    filter { it.state == BettingStatus.COMMITTED }.groupBy(BettingRecordBean::bettingArea).map {
        val sumOf = it.value.sumOf { it.money }
        val copy = it.value[0].copy()
        copy.money = sumOf
        copy.state = BettingStatus.TEMP
        againList[it.key] = copy
    }
    return againList
}

fun MutableMap<Betting, BettingRecordBean>.againIfMoneyEnough(): Boolean {
    return this.values.sumOf { it.money } <= (gameAboutModel.balance.value ?: 0)
}

fun MutableList<BettingRecordBean>.doubleIfMoneyEnough(): Boolean {
    return sumOf { it.money } * 2 <= (gameAboutModel.balance.value ?: 0)
}

fun MutableList<BettingRecordBean>.verifyDouble(configs: List<AreaBetConfigBean>?): VerifyDoubleResultBean? {
    configs?.let {
        groupBy { it.bettingArea }.forEach {
            val money = it.value.sumOf { bean ->
                bean.money
            }
            val maxLimit = configs.getBeanById(it.key)!!.maxLimit
            if (money * 2 > gameAboutModel.balance.value!!) {
                return VerifyDoubleResultBean(true, null)
            } else if (money * 2 > maxLimit) {
                return VerifyDoubleResultBean(false, configs.getBeanById(it.key))
            }
        }
    }
    return null
}

fun MutableList<BettingRecordBean>.double(): MutableMap<Betting, BettingRecordBean?> {
    val newList = mutableListOf<BettingRecordBean>()
    forEach {
        val copy = it.copy()
        copy.state = BettingStatus.TEMP
        newList.add(copy)
    }
    addAll(newList)
    val doubleMoney = newList.sumOf { it.money }
    gameAboutModel.deductTempBalance(doubleMoney)
    val doubleMap: MutableMap<Betting, BettingRecordBean?> = mutableMapOf()
    groupBy(BettingRecordBean::bettingArea).map {
        doubleMap[it.key] = it.value.generateUiBean(it.key)
    }
    return doubleMap
}

//没用 暂时不删
fun <K> Map<K, BettingRecordBean>.copy(): MutableMap<K, BettingRecordBean> {
    val newMap = mutableMapOf<K, BettingRecordBean>()
    forEach {
        newMap[it.key] = it.value.copy()
    }
    return newMap
}

@JvmName("copyFromBettingRecord")
infix fun <K> MutableMap<K, BettingRecordBean>.copyFrom(other: MutableMap<K, BettingRecordBean>) {
    other.forEach {
        this[it.key] = it.value.copy()
    }
}