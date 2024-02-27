package com.xcjh.app.websocket.so;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.java_websocket.client.WebSocketClient;

import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public class MyJavaWsClientCert {

    @RequiresApi(api = Build.VERSION_CODES.N)
    void  trustAllHots(WebSocketClient client) {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        //该方法用于获取信任的证书颁发机构（CA）的证书集合。在这个实现中，返回值为null表示不信任任何证书颁发机构，因此所有证书都会被视为不安全，从而导致连接失败或抛出异常。
                        //在实际应用中，如果你需要信任某些证书颁发机构，则需要提供一个正确的X509Certificate数组作为返回值。
                        // 这个数组应该包含信任的证书颁发机构的证书。如果你想信任所有的证书，可以返回一个空的X509Certificate数组，就像上面注释掉的那一行代码所示。

//                        return new X509Certificate[0];
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

                    }
                }
        };

        try {
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new SecureRandom());
            //这段代码使用ssl对象调用getSocketFactory()方法，
            // 将返回值赋给名为sslSocketFactory的变量。getSocketFactory()方法用于获取与该SSLContext关联的SSLSocketFactory实例。
            //在 Java 中，SSLSocketFactory是一个用于创建和配置 SSL/TLS 安全套接字的工厂类。
            // 通过ssl.getSocketFactory()方法，我们可以获取到相应的SSLSocketFactory实例，以后可以用于创建安全的SSLSocket连接
            SSLSocketFactory sslSocketFactory = ssl.getSocketFactory();
            client.setSocketFactory(sslSocketFactory);
        } catch (Exception e ) {
        }
    }
}
