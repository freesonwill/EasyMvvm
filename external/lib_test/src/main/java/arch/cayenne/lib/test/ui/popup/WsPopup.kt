package arch.cayenne.lib.test.ui.popup

import android.content.Context
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
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
                            wsConnect.text = "websocket連接: 平均：${"%.2f".format(wsConnectCost.toFloat() / wsConnectCount)} ms   count = $wsConnectCount"
                            newWebSocketManager.disconnect()
                            connectJob?.cancel()
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
                        "websocket今日数据: 平均：${"%.2f".format(wsHomeDataCost.toFloat() / wsHomeDataCount)} ms"


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
                            httpCount++
                            httpCost += end - start
                            wsHttp.text =
                                "http首开接口: 平均：${"%.2f".format(httpCost.toFloat() / httpCount)} ms"
                            "response------>${it}".logd(TAG)
                        },
                        onFailure = { code, msg, throwable ->
                            val end = System.currentTimeMillis()
                            httpCount++
                            httpCost += end - start
                            wsHttp.text =
                                "http首开接口: 平均：${"%.2f".format(httpCost.toFloat() / httpCount)} ms"
                            "response------>$code,$msg,$throwable".loge(TAG)

                        }
                    )
                }
            }
        }
    }

    interface IPreLoadHomeApi : IApi {
        @GET("sport_server/game/firstLoad")
        suspend fun preLoad(): Response<Any>
    }
}