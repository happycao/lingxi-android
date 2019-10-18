package me.cl.lingxi.common.okhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/04/29
 * desc   : 请求日志log
 * version: 1.0
 */

public class HttpLoggerInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private Logger logger;

    public HttpLoggerInterceptor(String tag) {
        logger = Logger.getLogger(tag);
    }

    private void log(String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();

        try {
            log("<-- " + clone.code() + ' ' + clone.message() + ' ' + clone.request().url() + " (" + tookMs + "ms）");
            if (HttpHeaders.hasBody(clone)) {
                if (responseBody == null) return response;

                if (isPlaintext(responseBody.contentType())) {
                    byte[] bytes = toByteArray(responseBody.byteStream());
                    MediaType contentType = responseBody.contentType();
                    String body = new String(bytes, getCharset(contentType));
                    log("\tresponse:" + body);
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                    return response.newBuilder().body(responseBody).build();
                } else {
                    log("\tresponse: maybe [binary body], omitted!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log("<-- END HTTP");
        }
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            return subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html");
        }
        return false;
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        while ((len = input.read(buffer)) != -1) output.write(buffer, 0, len);
        output.close();
        return output.toByteArray();
    }
}