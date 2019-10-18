package me.cl.lingxi.common.okhttp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/21
 * desc   : get请求
 * version: 1.0
 */
public class GetRequest {

    private static final long LOAD_TIME = 1500;

    private String mUrl;
    private Gson mGson;
    private Handler mDelivery;
    private OkHttpClient mOkHttpClient;

    // 请求头
    private LinkedHashMap<String, String> headers = new LinkedHashMap<>();
    // url参数
    private LinkedHashMap<String, List<String>> urlParams = new LinkedHashMap<>();
    // 是否加载延迟
    private boolean isLoadDelay;
    // 时间戳
    private long timeStamp;
    // 加载dialog
    private ProgressDialog mProgressDialog;

    /**
     * 构造函数
     */
    public GetRequest() {
        OkUtil okUtil = OkUtil.newInstance();
        mDelivery = okUtil.getDelivery();
        mOkHttpClient = okUtil.getOkHttpClient();

        // json date format
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    return format.parse(json.getAsJsonPrimitive().getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });
        mGson = gsonBuilder.create();

        headers.putAll(okUtil.getCommonHeaders());
    }

    /**
     * 设置url
     */
    public GetRequest url(@NonNull String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 添加请求头
     */
    public GetRequest addHeader(@NonNull String key, String value) {
        if (value == null) return this;
        this.headers.put(key, value);
        return this;
    }


    /**
     * 添加参数
     */
    public GetRequest addUrlParams(@NonNull String key, String value) {
        if (value == null) return this;
        List<String> strings = this.urlParams.get(key);
        if (strings == null) {
            strings = new ArrayList<>();
        }
        strings.add(value);
        this.urlParams.put(key, strings);
        return this;
    }

    /**
     * 添加数组参数
     */
    public GetRequest addUrlParams(@NonNull String key, List<String> values) {
        if (values == null || values.size() == 0) return this;
        this.urlParams.put(key, values);
        return this;
    }

    /**
     * 设置加载延迟
     */
    public GetRequest setLoadDelay() {
        this.isLoadDelay = true;
        this.timeStamp = System.currentTimeMillis();
        return this;
    }

    /**
     * 设置加载动画
     */
    public GetRequest setProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            this.mProgressDialog = progressDialog;
        }
        return this;
    }

    /**
     * header
     */
    private Headers getHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return headerBuilder.build();
    }


    /**
     * request
     */
    private Request getRequest() {
        Request.Builder builder = new Request.Builder();
        // header
        builder.headers(getHeaders());
        // 拼接参数
        mUrl = getUrlFromParams(mUrl, urlParams);
        // url
        builder.url(mUrl);
        // get
        return builder.get().build();
    }

    /**
     * 异步请求
     */
    public void execute(@NonNull final ResultCallback callback) {
        loadShow();
        mOkHttpClient.newCall(getRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setOnError(call, e, callback);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        if (Objects.equals(String.class.getGenericSuperclass(), callback.getType())) {
                            setOnSuccess(body.string(), callback);
                        } else {
                            setOnSuccess(mGson.fromJson(body.charStream(), callback.getType()), callback);
                        }
                    } else {
                        setOnError(call, new Exception("body is null"), callback);
                    }
                } catch (Exception e) {
                    setOnError(call, e, callback);
                }

            }
        });
    }

    /**
     * 异步请求，不设回调
     */
    public void execute() {
        loadShow();
        mOkHttpClient.newCall(getRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
            }
        });
    }

    /**
     * 设置请求成功回调
     */
    private void setOnSuccess(final Object object, final ResultCallback callback) {
        mDelivery.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDismiss();
                callback.onSuccess(object);
            }
        }, getLoadTime());
    }

    /**
     * 设置请求失败回调
     */
    private void setOnError(final Call call, final Exception e, final ResultCallback callback) {
        mDelivery.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDismiss();
                callback.onError(call, e);
            }
        }, getLoadTime());
    }

    /**
     * 拼装url参数，主要用于数组参数
     */
    private String getUrlFromParams(String url, Map<String, List<String>> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, List<String>> urlParams : params.entrySet()) {
                List<String> urlValues = urlParams.getValue();
                for (String value : urlValues) {
                    // 对参数进行utf-8编码
                    String urlValue = URLEncoder.encode(value, "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 获取加载时间
     */
    private long getLoadTime() {
        if (isLoadDelay) {
            timeStamp = System.currentTimeMillis() - timeStamp;
            timeStamp = timeStamp > LOAD_TIME ? 0 : LOAD_TIME - timeStamp;
        } else {
            timeStamp = 0;
        }
        return timeStamp;
    }

    /**
     * 展示加载动画
     */
    private void loadShow() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 关闭加载动画
     */
    private void loadDismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
