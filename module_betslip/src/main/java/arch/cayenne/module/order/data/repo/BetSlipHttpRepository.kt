package arch.cayenne.module.order.data.repo

import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.module.order.utils.BetSlipApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @author: wenxi
 * @date: 26/12/25 20:30
 * @description:
 */
class BetSlipHttpRepository(override val scope: CoroutineScope, private val apiClient: HttpClient) :
    BaseRepository() {

    suspend fun getBetShare(userId: Long, settleId: String): ApiResponseState {
        val api = apiClient.create(BetSlipApi::class.java)
        return suspendCancellableCoroutine { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                apiClient.safeRequest(
                    request = {
                        api.getBetShareResult(userId, settleId)
                    },
                    onSuccess = { resp ->
                        "response------>${resp.code},${resp.message}".logi(TAG)
                        if (resp.code == 0) {
                            cancellableContinuation.resume(ApiResponseState.Succeeded(resp.data))
                        } else {
                            cancellableContinuation.resume(
                                ApiResponseState.Failed(
                                    HttpException(
                                        resp.code,
                                        resp.message
                                    )
                                )
                            )
                        }
                    },
                    onFailure = {code,msg,throwable ->
                        "response------>$code,$msg,$throwable".logi(TAG)
                        cancellableContinuation.resume(
                            ApiResponseState.Failed(
                                HttpException(
                                    code,
                                    msg ?: ""
                                )
                            )
                        )
                    }
                )

            }


        }
    }


}