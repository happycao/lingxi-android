package me.cl.lingxi.common.okhttp;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;

public abstract class ResultCallback<T> {

    public abstract void onSuccess(T response);

    public abstract void onError(Call call, Exception e);

    Type getType() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterize = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterize.getActualTypeArguments()[0]);
    }
}