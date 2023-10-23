package com.xcjh.app.websocket

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import org.java_websocket.client.WebSocketClient
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManager
import javax.net.ssl.X509ExtendedTrustManager

class MyWsClientCert {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("TrustAllX509TrustManager")
    fun trustAllHots(client: WebSocketClient) {
        val trustAllCerts = arrayOf<TrustManager>(object : X509ExtendedTrustManager() {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                x509Certificates: Array<X509Certificate>,
                s: String,
                socket: Socket
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                x509Certificates: Array<X509Certificate>,
                s: String,
                socket: Socket
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                x509Certificates: Array<X509Certificate>,
                s: String,
                sslEngine: SSLEngine
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                x509Certificates: Array<X509Certificate>,
                s: String,
                sslEngine: SSLEngine
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                //        return new X509Certificate[0];
                return null
            }
        })
        try {
            val ssl = SSLContext.getInstance("SSL")
            ssl.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = ssl.socketFactory
            client.setSocketFactory(sslSocketFactory)
        } catch (e: Exception) {
        }
    }
}