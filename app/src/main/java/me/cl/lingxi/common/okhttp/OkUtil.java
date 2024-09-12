package me.cl.lingxi.common.okhttp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/04/29
 * desc   : okhttp util
 * version: 1.0
 */
public class OkUtil {

    private static final long DEFAULT_MILLISECONDS = 50000;      //默认的超时时间

    private Application context;
    private Handler mDelivery;
    private OkHttpClient okHttpClient;
    // 请求头
    private LinkedHashMap<String, String> commonHeaders = new LinkedHashMap<>();

    private OkUtil() {
        mDelivery = new Handler(Looper.getMainLooper());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggerInterceptor loggingInterceptor = new HttpLoggerInterceptor(getClass().getName());
        builder.addInterceptor(loggingInterceptor);

        builder.readTimeout(OkUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkUtil.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        try {
            @SuppressLint("CustomX509TrustManager")
            TrustManager[] trustManagers = {
                    new X509TrustManager() {

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception ignored) {

        }
        okHttpClient = builder.build();
    }

    private static final class InstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final OkUtil mInstance = new OkUtil();
    }

    public static OkUtil newInstance() {
        return InstanceHolder.mInstance;
    }

    /**
     * 初始化
     */
    public OkUtil init(Application app) {
        context = app;
        return this;
    }

    /**
     * get请求
     */
    public static GetRequest get() {
        return new GetRequest();
    }

    /**
     * post请求
     */
    public static PostRequest post() {
        return new PostRequest();
    }

    /**
     * 添加公共请求头
     */
    public OkUtil addCommonHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            commonHeaders.put(key, value);
        }
        return this;
    }

    /**
     * 添加公共请求头
     */
    public OkUtil addCommonHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                commonHeaders.put(key, headers.get(key));
            }
        }
        return this;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public LinkedHashMap<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}
