package arch.cayenne.lib.test.ui.popup

import android.content.Context
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.repo.CommonRepository.Companion.MATCH_APP
import arch.cayenne.lib.common.data.repo.CommonRepository.Companion.MATCH_GOAL
import arch.cayenne.lib.common.data.repo.CommonRepository.Companion.MATCH_KICK
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.test.R
import arch.cayenne.lib.test.databinding.DemoWsPopupBinding
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.lib.websocket.data.ConnectState
import com.lxj.xpopup.core.BottomPopupView
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import galaxy.common.proto.Common.Setting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import retrofit2.Response
import retrofit2.http.GET

/**
 * @date: 2025/9/17 11:21
 * @description:
 */
class WsPopup(context: Context) : BottomPopupView(context), KoinComponent {

    private val TAG = "WsPopup"

//    private val client: HttpClient by inject(named("preLoadHome"))

    val httpClient: HttpClient = getKoin().get(named("preLoadHome"))
    private val socketManager: WebSocketManager = getKoin().get()


    private var wsHomeDataCount: Long = 0
    private var wsHomeDataCost: Long = 0

    private var httpCount: Long = 0

    private var httpCost: Long = 0

    private var wsConnectCount: Long = 0

    private var wsConnectCost: Long = 0
    private var connectJob: Job? = null

    private var wsLoginCount: Long = 0

    private var wsLoginCost: Long = 0

    override fun getImplLayoutId(): Int {
        return R.layout.demo_ws_popup
    }

    private var vb: DemoWsPopupBinding? = null

    override fun onCreate() {
        super.onCreate()
        vb = DemoWsPopupBinding.bind(popupImplView)
        vb?.apply {
            wsConnect.clickNoRepeat {
//                connectJob?.cancel()
//                newWebSocketManager.disconnect()
                "start request".logd(TAG)
                val newWebSocketManager: WebSocketManager = getKoin().get(named("test"))

                connectJob = lifecycleScope.launch {
                    val start = System.currentTimeMillis()
                    newWebSocketManager.getConnectStateFlow().collect {
                        if (it is ConnectState.ConnectSuccess) {
                            val end = System.currentTimeMillis()
                            wsConnectCount++
                            wsConnectCost += (end - start)
                            wsConnect.text = "websocket連接: 花費：${(end - start)} ms"
                            newWebSocketManager.disconnect()
                            connectJob?.cancel()
                        }
                    }
                }
                newWebSocketManager.connect("wss://betwavepro.ja700.com/fb-ws")
            }

            wsLogin.clickNoRepeat {
                val newWebSocketManager: WebSocketManager = getKoin().get(named("test"))
                connectJob = lifecycleScope.launch {
                    newWebSocketManager.getConnectStateFlow().collect {
                        if (it is ConnectState.ConnectSuccess) {
                            val start = System.currentTimeMillis()
                            val uid = 55469250L
                            val token = "NTU0NjkyNTBfMTc1ODA5NzMyMjI1MDoyellBMnpIc0lHOG02RjN3"
                            launch {
                                val res = newWebSocketManager.sendAndWaitProtoMessageResponse<Client.LoginResp>(
                                        scope = lifecycleScope,
                                        dispatcher = Dispatchers.IO,
                                        apiCode = ApiCode.LOGIN,
                                    ) {
                                        Client.LoginReq.newBuilder().apply {
                                            this.uid = uid
                                            this.token = token
                                            this.platform = 5
                                            this.setting = getSystemSetting()
                                        }.build()
                                    }
                                val end = System.currentTimeMillis()
                                wsLoginCount++
                                wsLoginCost += (end - start)
                                newWebSocketManager.isLogin = true
                                if (res.error == null && res.data != null) {
                                    wsLogin.text =
                                        "websocket登錄: 耗时：${end-start} ms"
                                }
                                newWebSocketManager.disconnect()
//                                connectJob?.cancel()
                            }
                        }
                    }
                }
                newWebSocketManager.connect("wss://betwavepro.ja700.com/fb-ws")
            }

            wsToday.clickNoRepeat {
                lifecycleScope.launch {
                    val start = System.currentTimeMillis()
                    val last = null
                    val res = socketManager.sendAndWaitProtoMessageResponse<Client.ListMatchResp>(
                        scope = lifecycleScope,
                        dispatcher = Dispatchers.IO,
                        apiCode = ApiCode.LIST_MATCH,
                    ) {
                        Client.ListMatchReq.newBuilder().apply {
                            this.sportId = 1
                            this.playType = 2
                            this.tournamentId = 0
                            this.size = 10
                            this.startTime = startTime
                            this.endTime = endTime

                        }.build()
                    }

                    val end = System.currentTimeMillis()

                    wsHomeDataCount++
                    wsHomeDataCost += (end - start)

                    wsToday.text =
                        "websocket今日数据 耗时：${end-start} ms"


                    if (res.error == null && res.data != null) {
                    } else {
                        null
                    }
                }
            }

            wsHttp.clickNoRepeat {
                val api = httpClient.create(IPreLoadHomeApi::class.java)
                lifecycleScope.launch {
                    val start = System.currentTimeMillis()
                    "start request".logd(TAG)
                    httpClient.safeRequest(
                        request = {
                            api.preLoad(
                            )
                        },
                        onSuccess = {
                            val end = System.currentTimeMillis()
                            wsHttp.text =
                                "http首开接口 耗时：${end - start} ms"
                            "response------>${it}".logd(TAG)
                        },
                        onFailure = { code, msg, throwable ->
                            val end = System.currentTimeMillis()
                            wsHttp.text =
                                "http首开接口 耗时：${end - start} ms"
                            "response------>$code,$msg,$throwable".loge(TAG)

                        }
                    )
                }
            }


            gameTest.clickNoRepeat {
                val api = httpClient.create(IPreLoadHomeApi::class.java)
                lifecycleScope.launch {
                    val start = System.currentTimeMillis()
                    "start request".logd(TAG)
                    httpClient.safeRequest(
                        request = {
                            api.gameTest(
                            )
                        },
                        onSuccess = {
                            val end = System.currentTimeMillis()
                            gameTest.text =
                                "gameTest接口 耗时：${end - start} ms"
                            "response------>${it}".logd(TAG)
                        },
                        onFailure = { code, msg, throwable ->
                            val end = System.currentTimeMillis()
                            gameTest.text =
                                "gameTest接口 耗时：${end - start} ms"
                            "response------>$code,$msg,$throwable".loge(TAG)

                        }
                    )
                }
            }
        }
    }

    private fun getSystemSetting(): Setting {
        val language = "zh-CN"
        val sysGoal = getNotifyMatchType(MATCH_GOAL)
        val sysKick = getNotifyMatchType(MATCH_KICK)
        val app = getNotifyMatchType(MATCH_APP)
        return Setting.newBuilder().apply {
            lang = language          //语言类型
            systemGoal = sysGoal     //系统通知-进球
            systemKickOff = sysKick  //系统通知-开球
            appGoal = app            //app内通知-开球
        }.build()
    }

    private fun getNotifyMatchType(type: Int): Common.NotifyMatchType {
        return Common.NotifyMatchType.newBuilder().apply {
            when (type) {
                MATCH_GOAL -> {
                    betMatch = false
                    collectMatch = false
                    allMatch = false
                }

                MATCH_KICK -> {
                    betMatch = false
                    collectMatch = false
                    allMatch = false
                }

                else -> {
                    betMatch = false
                    collectMatch = false
                    allMatch = false
                }
            }
        }.build()
    }

    interface IPreLoadHomeApi : IApi {
        @GET("sport_server/game/firstLoad")
        suspend fun preLoad(): Response<Any>

        @GET("sport_server/game/")
        suspend fun gameTest(): Response<Any>
    }
}