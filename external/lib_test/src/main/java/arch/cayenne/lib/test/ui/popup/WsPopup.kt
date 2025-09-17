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
import com.lxj.xpopup.core.BottomPopupView
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

    var httpCount: Long = 0

    var httpCost: Long = 0

    override fun getImplLayoutId(): Int {
        return R.layout.demo_ws_popup
    }

    private var vb: DemoWsPopupBinding? = null

    override fun onCreate() {
        super.onCreate()
        vb = DemoWsPopupBinding.bind(popupImplView)
        vb?.apply {

            wsToday.clickNoRepeat {  }

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
                            wsHttp.text = "http首开接口: 平均：${httpCost.toFloat() / httpCount} ms"
                            "response------>${it}".logd(TAG)
                        },
                        onFailure = { code, msg, throwable ->
                            val end = System.currentTimeMillis()
                            httpCount++
                            httpCost += end - start
                            wsHttp.text = "http首开接口: 平均：${httpCost.toFloat() / httpCount} ms"
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