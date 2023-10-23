package com.xcjh.app.websocket

import android.os.Build
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


/**
 * @author zobo101
 * webSocket 客户端
 */
open class MyWsClient(serverUri: URI?) : WebSocketClient(serverUri, Draft_6455()) {

    companion object {
        private val TAG = MyWsClient::class.java.simpleName
    }

    init {
        if (serverUri.toString().contains("wss://")) {
            trustAllHots(this)
        }
    }

    private fun trustAllHots(client: MyWsClient) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            MyWsClientCert().trustAllHots(client)
        }
       // val trustAllCerts = TrustManager
    }

    override fun onOpen(handshakedata: ServerHandshake) {
        Log.e(TAG, "===onOpen()")
    }

    override fun onMessage(message: String) {
        Log.e(TAG, "===onMessage()")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        Log.e(TAG, "===onClose()")
    }

    override fun onError(ex: Exception) {
        Log.e(TAG, "===onError()")
    }
}