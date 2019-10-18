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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
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
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/04/29
 * desc   : post请求
 * version: 1.0
 */
public class PostRequest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final long LOAD_TIME = 1500;

    private String mUrl;
    private Gson mGson;
    private Handler mDelivery;
    private OkHttpClient mOkHttpClient;

    // 请求头
    private LinkedHashMap<String, String> headers = new LinkedHashMap<>();
    // 参数
    private LinkedHashMap<String, String> params = new LinkedHashMap<>();
    // 文件
    private LinkedHashMap<String, List<File>> fileParams = new LinkedHashMap<>();
    // url参数
    private LinkedHashMap<String, List<String>> urlParams = new LinkedHashMap<>();
    // 是否包含文件
    private boolean isMultipart;
    // 是否拼接url参数
    private boolean isSpliceUrl;
    // 是否为json参数
    private boolean isJson;
    // json参数
    private String mJson;
    // 是否加载延迟
    private boolean isLoadDelay;
    // 时间戳
    private long timeStamp;
    // 加载dialog
    private ProgressDialog mProgressDialog;

    /**
     * 构造函数
     */
    public PostRequest() {
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
    public PostRequest url(@NonNull String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 添加请求头
     */
    public PostRequest addHeader(@NonNull String key, String value) {
        if (value == null) return this;
        this.headers.put(key, value);
        return this;
    }

    /**
     * 添加参数
     */
    public PostRequest addParam(@NonNull String key, Integer value) {
        if (value == null) return this;
        this.params.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加参数
     */
    public PostRequest addParam(@NonNull String key, String value) {
        if (value == null) return this;
        this.params.put(key, value);
        return this;
    }

    /**
     * 添加数组参数
     */
    public PostRequest addUrlParams(@NonNull String key, List<String> values) {
        if (values == null || values.size() == 0) return this;
        this.isSpliceUrl = true;
        this.urlParams.put(key, values);
        return this;
    }

    /**
     * 添加文件
     */
    public PostRequest addFile(@NonNull String key, File file) {
        if (file == null) return this;
        this.isMultipart = true;
        List<File> files = this.fileParams.get(key);
        if (files == null) files = new ArrayList<>();
        files.add(file);
        this.fileParams.put(key, files);
        return this;
    }

    /**
     * 添加多文件
     */
    public PostRequest addFiles(@NonNull String key, List<File> files) {
        if (files == null || files.size() == 0) return this;
        this.isMultipart = true;
        this.fileParams.put(key, files);
        return this;
    }

    /**
     * 提交json数据
     */
    public PostRequest postJson(@NonNull String json) {
        this.isJson = true;
        this.mJson = json;
        return this;
    }

    /**
     * 设置加载延迟
     */
    public PostRequest setLoadDelay() {
        this.isLoadDelay = true;
        this.timeStamp = System.currentTimeMillis();
        return this;
    }

    /**
     * 设置加载动画
     */
    public PostRequest setProgressDialog(ProgressDialog progressDialog) {
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
     * from body
     */
    private RequestBody getFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * Json body
     */
    private RequestBody getJsonBody() {
        return RequestBody.create(JSON, mJson);
    }

    /**
     * Multipart body
     */
    private RequestBody getMultipartBody() {
        MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, List<File>> entry : fileParams.entrySet()) {
            List<File> fileValues = entry.getValue();
            for (File file : fileValues) {
                RequestBody fileBody = RequestBody.create(getMimeType(file.getName()), file);
                multipartBodybuilder.addFormDataPart(entry.getKey(), file.getName(), fileBody);
            }
        }
        return multipartBodybuilder.build();
    }

    /**
     * request
     */
    private Request getRequest() {
        Request.Builder builder = new Request.Builder();
        // header
        builder.headers(getHeaders());
        // 拼接参数
        if (isSpliceUrl) {
            mUrl = getUrlFromParams(mUrl, urlParams);
        }
        // url
        builder.url(mUrl);
        // json body
        if (isJson) {
            return builder.post(getJsonBody()).build();
        }
        // multipart body
        if (isMultipart) {
            return builder.post(getMultipartBody()).build();
        }
        // from body
        return builder.post(getFormBody()).build();
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
    private void setOnSuccess(final Object object, final ResultCallback callback) throws Exception {
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
     * 根据文件名获取MIME类型
     */
    private MediaType getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        // 解决文件名中含有#号异常的问题
        fileName = fileName.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MediaType.parse("application/octet-stream");
        }
        return MediaType.parse(contentType);
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
